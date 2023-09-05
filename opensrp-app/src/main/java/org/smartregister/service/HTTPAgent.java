package org.smartregister.service;

import android.content.Context;
import android.util.Base64;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseErrorStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import timber.log.Timber;

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

public class HTTPAgent {
    private Context context;
    private AllSettings settings;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;

    private String boundary = "***" + System.currentTimeMillis() + "***";
    private String twoHyphens = "--";
    private String crlf = "\r\n";

    private int connectTimeout = 60000;
    private int readTimeout = 60000;

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
        urlConnection.setConnectTimeout(getConnectTimeout());
        urlConnection.setReadTimeout(getReadTimeout());

        if(useBasicAuth) {
            final String basicAuth = "Basic " + Base64.encodeToString((allSharedPreferences.fetchRegisteredANM() +
                    ":" + settings.fetchANMPassword()).getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
        }
        return urlConnection;
    }
    private HttpURLConnection initializeHttp(String requestURLPath, String  authUser, String authPass) throws IOException {
        URL url = new URL(requestURLPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection instanceof HttpsURLConnection) {
            OpensrpSSLHelper opensrpSSLHelper = new OpensrpSSLHelper(context, configuration);
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(opensrpSSLHelper.getSSLSocketFactory());
        }
        urlConnection.setConnectTimeout(getConnectTimeout());
        urlConnection.setReadTimeout(getReadTimeout());
        final String basicAuth = "Basic " + Base64.encodeToString((authUser +
                ":" + authPass).getBytes(), Base64.NO_WRAP);
        urlConnection.setRequestProperty("Authorization", basicAuth);

        return urlConnection;
    }
    public Response<String> fetch(String requestURLPath) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(requestURLPath, true);
            return handleResponse(urlConnection);

        } catch (IOException ex) {
            Timber.e(ex, "EXCEPTION %s", ex.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }
    public Response<String> fetchWithoutAuth(String requestURLPath) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(requestURLPath, false);
            return handleResponse(urlConnection);

        } catch (IOException ex) {
            Timber.e(ex, "EXCEPTION %s", ex.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }
    public Response<String> post(String postURLPath, String jsonPayload, HashMap<String,String> headerList) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(postURLPath, true);
            if(headerList!=null){

                for(String headers: headerList.keySet()){
                    urlConnection.setRequestProperty(headers,headerList.get(headers));
                }
            }
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
            Timber.e(ex,  "EXCEPTION: %s", ex.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }
    public Response<String> postWithHeaderAndAuthInfo(String postURLPath, String jsonPayload, HashMap<String,String> headerList,String authUser, String authPass) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(postURLPath, authUser,authPass);
            if(headerList!=null){

                for(String headers: headerList.keySet()){
                    urlConnection.setRequestProperty(headers,headerList.get(headers));
                }
            }
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
            Timber.e(ex,  "EXCEPTION: %s", ex.toString());
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
            Timber.e(ex,  "EXCEPTION: %s", ex.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }
    public Response<String> postWithBasicAuthInfo(String postURLPath, String jsonPayload, String authUser, String authPass) {
        HttpURLConnection urlConnection;
        try {
            urlConnection = initializeHttp(postURLPath, authUser,authPass);

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
            Timber.e(ex,  "EXCEPTION: %s", ex.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }
    public Response<String> postWithJsonResponse(String postURLPath, String jsonPayload) {
        return post(postURLPath, jsonPayload);
    }

    public LoginResponse urlCanBeAccessWithGivenCredentials(String requestURL, String userName, String password) {
        LoginResponse loginResponse = null;
        HttpURLConnection urlConnection = null;
        String url = null;
        try {
            url = requestURL.replaceAll("\\s+", "");
            urlConnection = initializeHttp(url, false);

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
                Timber.e("Invalid credentials for: %s using %s", userName, url);
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
                Timber.e("Bad response from Dristhi. Status code: %s username: %s using %s ", statusCode, userName, url);
                loginResponse = UNKNOWN_RESPONSE;
            }
        } catch (MalformedURLException e) {
            Timber.e(e,  "Failed to check credentials bad url %s", url);
            loginResponse = MALFORMED_URL;
        } catch (SocketTimeoutException e) {
            Timber.e(e,"SocketTimeoutException when authenticating %s", userName);
            loginResponse = TIMEOUT;
            Timber.e(e,"Failed to check credentials of: %s using %s . Error: %s", userName, url, e.toString());
        } catch (IOException e) {
            Timber.e(e,"Failed to check credentials of: %s  using %s . Error: %s", userName, url, e.toString());
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
            Timber.e(ex,  "EXCEPTION %s", ex.toString());
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
        int statusCode;
        try {
            statusCode = urlConnection.getResponseCode();

            InputStream inputStream;
            if (statusCode >= HttpStatus.SC_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            responseString = IOUtils.toString(inputStream);

        } catch (MalformedURLException e) {
            Timber.e(e,  "%s %s", MALFORMED_URL, e.toString());
            ResponseStatus.failure.setDisplayValue(ResponseErrorStatus.malformed_url.name());
            return new Response<>(ResponseStatus.failure, null);
        } catch (SocketTimeoutException e) {
            Timber.e(e,  "%s %s", TIMEOUT, e.toString());
            ResponseStatus.failure.setDisplayValue(ResponseErrorStatus.timeout.name());
            return new Response<>(ResponseStatus.failure, null);
        } catch (IOException e) {
            Timber.e(e,  "%s %s", NO_INTERNET_CONNECTIVITY, e.toString());
            return new Response<>(ResponseStatus.failure, null);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new Response<>(statusCode >= HttpStatus.SC_BAD_REQUEST ? ResponseStatus.failure : ResponseStatus.success, responseString);
    }


    /**
     * @author  Rodgers Andati
     * @since   2019-04-25
     * This method uploads an image to opensrp server. Migration from the old method that used httpclient
     * @param urlString This is the url of the image, TAG,
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
            String line;
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpUrlConnection.getInputStream()));

                while ((line = reader.readLine()) != null) {
                    responseString = line;
                    Timber.d("SERVER RESPONSE %s", line);
                }
                reader.close();
            } else {
                Timber.d("SERVER RESPONSE %s Server returned non-OK status: %s :-", status, httpUrlConnection.getResponseMessage());
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpUrlConnection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    Timber.d("SERVER RESPONSE %s", line);
                }
                reader.close();
            }
            httpUrlConnection.disconnect();

        } catch (ProtocolException e) {
            Timber.e(e, "Protocol exception %s", e.toString());
        } catch (SocketTimeoutException e) {
            Timber.e(e,  "SocketTimeout %s %s", TIMEOUT, e.toString());
        } catch (MalformedURLException e) {
            Timber.e(e, "MalformedUrl %s %s", MALFORMED_URL, e.toString());
        } catch (IOException e) {
            Timber.e(e, "IOException %s %s", NO_INTERNET_CONNECTIVITY, e.toString());
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
            Timber.e("Empty Response using: %s ", SUCCESS_WITH_EMPTY_RESPONSE.name());
            return SUCCESS_WITH_EMPTY_RESPONSE;
        }

        if (responseData.team == null || responseData.team.team == null) {
            Timber.e("Empty Response in: %s ", SUCCESS_WITHOUT_TEAM_DETAILS.name());
            return SUCCESS_WITHOUT_TEAM_DETAILS.withPayload(responseData);
        } else if (responseData.team.team.location == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TEAM_LOCATION.name());
            return SUCCESS_WITHOUT_TEAM_LOCATION.withPayload(responseData);
        } else if (responseData.team.team.location.uuid == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TEAM_LOCATION_UUID.name());
            return SUCCESS_WITHOUT_TEAM_LOCATION_UUID.withPayload(responseData);
        } else if (responseData.team.team.uuid == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TEAM_UUID.name());
            return SUCCESS_WITHOUT_TEAM_UUID.withPayload(responseData);
        } else if (responseData.team.team.teamName == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TEAM_NAME.name());
            return SUCCESS_WITHOUT_TEAM_NAME.withPayload(responseData);
        }

        if (responseData.user == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_USER_DETAILS.name());
            return SUCCESS_WITHOUT_USER_DETAILS.withPayload(responseData);
        } else if (responseData.user.getUsername() == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_USER_USERNAME.name());
            return SUCCESS_WITHOUT_USER_USERNAME.withPayload(responseData);
        } else if (responseData.user.getPreferredName() == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_USER_PREFERREDNAME.name());
            return SUCCESS_WITHOUT_USER_PREFERREDNAME.withPayload(responseData);
        }

        if (responseData.locations == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_USER_LOCATION.name());
            return SUCCESS_WITHOUT_USER_LOCATION.withPayload(responseData);
        }
        if (responseData.time == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TIME_DETAILS.name());
            return SUCCESS_WITHOUT_TIME_DETAILS.withPayload(responseData);
        } else if (responseData.time.getTime() == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TIME.name());
            return SUCCESS_WITHOUT_TIME.withPayload(responseData);
        } else if (responseData.time.getTimeZone() == null) {
            Timber.e("Empty Response in: %s", SUCCESS_WITHOUT_TIME_ZONE.name());
            return SUCCESS_WITHOUT_TIME_ZONE.withPayload(responseData);
        }

        return SUCCESS.withPayload(responseData);
    }

    /**
     * Returns the read timeout in milliseconds
     *
     * @return read timeout value in milliseconds
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Returns the connection timeout in milliseconds
     *
     * @return connection timeout value in milliseconds
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connection timeout in milliseconds
     *
     * Setting this will call {@link java.net.HttpURLConnection#setConnectTimeout(int)}
     * on the {@link java.net.HttpURLConnection} instance in {@link org.smartregister.service.HTTPAgent}
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Sets the read timeout in milliseconds
     *
     * Setting this will call {@link java.net.HttpURLConnection#setReadTimeout(int)}
     * on the {@link java.net.HttpURLConnection} instance in {@link org.smartregister.service.HTTPAgent}
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
