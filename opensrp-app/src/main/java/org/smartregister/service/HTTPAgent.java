package org.smartregister.service;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.ssl.OpensrpSSLHelper;
import org.smartregister.util.DownloadForm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import static org.smartregister.domain.LoginResponse.CUSTOM_SERVER_RESPONSE;
import static org.smartregister.domain.LoginResponse.MALFORMED_URL;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TEAM_DETAILS;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TEAM_LOCATION;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TEAM_LOCATION_UUID;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TEAM_NAME;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TEAM_UUID;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TIME;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TIME_DETAILS;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_TIME_ZONE;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_USER_DETAILS;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_USER_LOCATION;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_USER_PREFERREDNAME;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITHOUT_USER_USERNAME;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITH_EMPTY_RESPONSE;
import static org.smartregister.domain.LoginResponse.TIMEOUT;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.util.HttpResponseUtil.getResponseBody;
import static org.smartregister.util.Log.logError;

public class HTTPAgent {
    public static final int CONNECTION_TIMEOUT = 60000;
    private static final int READ_TIMEOUT = 60000;
    private static final String TAG = HTTPAgent.class.getCanonicalName();
    private Context context;
    private AllSettings settings;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;

    private String boundary = "===" + System.currentTimeMillis() + "===";
    private String twoHyphens = "--";
    private String crlf = "\r\n";

    public HTTPAgent(Context context, AllSettings settings, AllSharedPreferences
            allSharedPreferences, DristhiConfiguration configuration) {
        this.context = context;
        this.settings = settings;
        this.allSharedPreferences = allSharedPreferences;
        this.configuration = configuration;
    }

    /**
     * @author  Rodgers Andati
     * @since   2019-04-25
     * This method initializes httpurlconnection
     * @param requestURLPath This is the url to be open http connection to.
     * @param useBasicAuth This is whether to set up basic authentication or not.
     * @return HttpURLConnection This returns the http connection to opensrp server.
     */
    private HttpURLConnection initializeHttp(String requestURLPath, boolean useBasicAuth) throws IOException {
        URL url = new URL(requestURLPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection instanceof HttpsURLConnection) {
            OpensrpSSLHelper opensrpSSLHelper = new OpensrpSSLHelper(context, configuration);
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(opensrpSSLHelper.getSSLSocketFactory());
        }
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(READ_TIMEOUT);

        if(useBasicAuth) {
            final String basicAuth = "Basic " + Base64.encodeToString((allSharedPreferences.fetchRegisteredANM() +
                    ":" + settings.fetchANMPassword()).getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
        }
        return urlConnection;
    }

    public Response<String> fetch(String requestURLPath) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(requestURLPath, true);
            return handleResponse(urlConnection);

        } catch (IOException ex) {
            Log.e(TAG, "EXCEPTION" + ex.toString(), ex);
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public Response<String> post(String postURLPath, String jsonPayload) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(postURLPath, true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonPayload);
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            return handleResponse(urlConnection);

        } catch (IOException ex) {
            Log.e(TAG, "EXCEPTION" + ex.toString(), ex);
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public Response<String> postWithJsonResponse(String postURLPath, String jsonPayload) {
        return post(postURLPath, jsonPayload);
    }

    public LoginResponse urlCanBeAccessWithGivenCredentials(String requestURL, String userName,
                                                            String password) {
        LoginResponse loginResponse = null;
        HttpURLConnection urlConnection = null;
        try {
            requestURL = requestURL.replaceAll("\\s+", "");
            urlConnection = initializeHttp(requestURL, false);

            final String basicAuth = "Basic " + Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
            int statusCode = urlConnection.getResponseCode();
            InputStream inputStream;
            if (statusCode >= HttpStatus.SC_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();
            String responseString = IOUtils.toString(inputStream);
            if (statusCode == HttpStatus.SC_OK) {
                LoginResponseData responseData = getResponseBody(responseString);
                loginResponse = retrieveResponse(responseData);
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logError("Invalid credentials for: " + userName + " using " + requestURL);
                loginResponse = UNAUTHORIZED;
            } else if (StringUtils.isNotBlank(responseString)) {
                //extract message string from the default tomcat server response which is usually between <p><b>message</b> and </u></p>
                responseString = StringUtils.substringBetween(responseString, "<p><b>message</b>", "</u></p>");
                if (StringUtils.isNotBlank(responseString)) {
                    //remove the underline tag from the responseString
                    responseString = responseString.replace("<u>", "").trim();
                    loginResponse = CUSTOM_SERVER_RESPONSE.withMessage(responseString);
                }
            } else {
                logError("Bad response from Dristhi. Status code:  " + statusCode + " username: "
                        + userName + " using " + requestURL);
                loginResponse = UNKNOWN_RESPONSE;
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Failed to check credentials bad url " + requestURL, e);
            loginResponse = MALFORMED_URL;
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "SocketTimeoutException when authenticating " + userName, e);
            loginResponse = TIMEOUT;
            Log.e(TAG, "Failed to check credentials of: " + userName + " using " + requestURL + ". "
                    + "" + "" + "Error: " + e.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, "Failed to check credentials of: " + userName + " using " + requestURL + ". "
                    + "" + "" + "Error: " + e.toString(), e);
            loginResponse = NO_INTERNET_CONNECTIVITY;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return loginResponse;
    }

    public DownloadStatus downloadFromUrl(String url, String filename) {
        Response<DownloadStatus> status = DownloadForm.downloadFromURL(url, filename,
                allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
        return status.payload();
    }

    public Response<String> fetchWithCredentials(String requestURL, String username, String password) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = initializeHttp(requestURL, false);

            setCustomCredentials(urlConnection, username, password);
            return handleResponse(urlConnection);

        } catch (IOException ex) {
            Log.e(TAG, "EXCEPTION" + ex.toString(), ex);
            return new Response<>(ResponseStatus.failure, null);
        }

    }

    private void setCustomCredentials(HttpURLConnection urlConnection, String username, String password) {
        final String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(),
                Base64.NO_WRAP);
        urlConnection.setRequestProperty("Authorization", basicAuth);
    }

    private Response<String> handleResponse(HttpURLConnection urlConnection) {
        String responseString;
        try {
            int statusCode = urlConnection.getResponseCode();

            InputStream inputStream;
            if (statusCode >= HttpStatus.SC_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            responseString = IOUtils.toString(inputStream);

        } catch (MalformedURLException e) {
            Log.e(TAG, MALFORMED_URL + e.toString(), e);
            return new Response<>(ResponseStatus.failure, null);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, TIMEOUT + e.toString(), e);
            return new Response<>(ResponseStatus.failure, null);
        } catch (IOException e) {
            Log.e(TAG, NO_INTERNET_CONNECTIVITY + e.toString(), e);
            return new Response<>(ResponseStatus.failure, null);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new Response<>(ResponseStatus.success, responseString);
    }


    /**
     * @author  Rodgers Andati
     * @since   2019-04-25
     * This method uploads an image to opensrp server. Migration from the old method that used httpclient
     * @param urlString This is the url of the image
     * @param image This is the image to be uploaded to opensrp server.
     * @return String This returns the response obtained from the opensrp server.
     */
    public String httpImagePost(String urlString, ProfileImage image) {
        OutputStream outputStream;
        PrintWriter writer;
        String responseString = "";

        try {
            HttpURLConnection httpUrlConnection = initializeHttp(urlString, true);

            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = httpUrlConnection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"),true);

            // attach image
            attachImage(writer, image, outputStream);

            // adding string params
            addParameter(writer, "anm-id", image.getAnmId());
            addParameter(writer, "entity-id", image.getEntityID());
            addParameter(writer, "content-type", image.getContenttype() != null ? image.getContenttype() : "jpeg");
            addParameter(writer, "file-category", image.getFilecategory() != null ? image.getFilecategory() : "profilepic");

            // send request to server
            writer.append(crlf).flush();
            writer.append(twoHyphens + boundary + twoHyphens).append(crlf);
            writer.close();

            // checks server's status code first
            int status = httpUrlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpUrlConnection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    responseString = line;
                    Log.d("RESPONSE", line);
                }
                reader.close();
                httpUrlConnection.disconnect();
            } else {
                Log.d("RESPONSE", "Server returned non-OK status: " + status);
            }

        } catch (ProtocolException e) {
            Log.e(TAG, "Protocol exception " + e.toString(), e);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, TIMEOUT + e.toString(), e);
        } catch (MalformedURLException e) {
            Log.e(TAG, MALFORMED_URL + e.toString(), e);
        } catch (IOException e) {
            Log.e(TAG, NO_INTERNET_CONNECTIVITY + e.toString(), e);
        }
        return responseString;
    }

    private void attachImage(PrintWriter writer, ProfileImage image, OutputStream outputStream) throws IOException {
        File uploadImageFile = new File(image.getFilepath());
        String fileName = uploadImageFile.getName();

        writer.append("--" + boundary).append(crlf);
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"").append(crlf);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(crlf);
        writer.append("Content-Transfer-Encoding: binary").append(crlf);
        writer.append(crlf);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadImageFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(crlf);
        writer.flush();
    }

    private void addParameter(PrintWriter writer, String paramName, String paramValue) {
        writer.append(twoHyphens + boundary).append(crlf);
        writer.append("Content-Disposition: form-data; name=\""+ paramName +"\"").append(crlf);
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(crlf);
        writer.append(crlf);
        writer.append(paramValue).append(crlf);
        writer.flush();
    }


    private LoginResponse retrieveResponse(LoginResponseData responseData) {
        if (responseData == null) {
            logError("Empty Response using " + SUCCESS_WITH_EMPTY_RESPONSE.name());
            return SUCCESS_WITH_EMPTY_RESPONSE;
        }

        if (responseData.team == null || responseData.team.team == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TEAM_DETAILS.name());
            return SUCCESS_WITHOUT_TEAM_DETAILS.withPayload(responseData);
        } else if (responseData.team.team.location == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TEAM_LOCATION.name());
            return SUCCESS_WITHOUT_TEAM_LOCATION.withPayload(responseData);
        } else if (responseData.team.team.location.uuid == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TEAM_LOCATION_UUID.name());
            return SUCCESS_WITHOUT_TEAM_LOCATION_UUID.withPayload(responseData);
        } else if (responseData.team.team.uuid == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TEAM_UUID.name());
            return SUCCESS_WITHOUT_TEAM_UUID.withPayload(responseData);
        } else if (responseData.team.team.teamName == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TEAM_NAME.name());
            return SUCCESS_WITHOUT_TEAM_NAME.withPayload(responseData);
        }

        if (responseData.user == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_USER_DETAILS.name());
            return SUCCESS_WITHOUT_USER_DETAILS.withPayload(responseData);
        } else if (responseData.user.getUsername() == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_USER_USERNAME.name());
            return SUCCESS_WITHOUT_USER_USERNAME.withPayload(responseData);
        } else if (responseData.user.getPreferredName() == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_USER_PREFERREDNAME.name());
            return SUCCESS_WITHOUT_USER_PREFERREDNAME.withPayload(responseData);
        }

        if (responseData.locations == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_USER_LOCATION.name());
            return SUCCESS_WITHOUT_USER_LOCATION.withPayload(responseData);
        }
        if (responseData.time == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TIME_DETAILS.name());
            return SUCCESS_WITHOUT_TIME_DETAILS.withPayload(responseData);
        } else if (responseData.time.getTime() == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TIME.name());
            return SUCCESS_WITHOUT_TIME.withPayload(responseData);
        } else if (responseData.time.getTimeZone() == null) {
            logError("Empty Response in " + SUCCESS_WITHOUT_TIME_ZONE.name());
            return SUCCESS_WITHOUT_TIME_ZONE.withPayload(responseData);
        }

        return SUCCESS.withPayload(responseData);
    }

}
