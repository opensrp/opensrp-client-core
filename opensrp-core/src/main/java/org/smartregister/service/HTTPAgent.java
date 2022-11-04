package org.smartregister.service;

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

import android.content.Context;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountConfiguration;
import org.smartregister.account.AccountError;
import org.smartregister.account.AccountHelper;
import org.smartregister.account.AccountResponse;
import org.smartregister.account.AccountUserInfo;
import org.smartregister.compression.GZIPCompression;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseErrorStatus;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.security.SecurityHelper;
import org.smartregister.ssl.OpensrpSSLHelper;
import org.smartregister.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import timber.log.Timber;

public class HTTPAgent {

    public static final int FILE_UPLOAD_CHUNK_SIZE_BYTES = 4096;
    public static final int DOWNLOAD_BUFFER_SIZE = 1024;
    private static final String DETAILS_URL = "/user-details?anm-id=";
    private Context context;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;
    private GZIPCompression gzipCompression;
    private String boundary = "***" + System.currentTimeMillis() + "***";
    private String twoHyphens = "--";
    private String crlf = "\r\n";
    private int connectTimeout = 60000;
    private int readTimeout = 60000;
    private Gson gson;


    public HTTPAgent(Context context, AllSharedPreferences
            allSharedPreferences, DristhiConfiguration configuration) {
        this.context = context;
        this.allSharedPreferences = allSharedPreferences;
        this.configuration = configuration;
        gson = new Gson();
        gzipCompression = new GZIPCompression();
    }

    /**
     * This method initializes httpurlconnection
     *
     * @param requestURLPath This is the url to open http connection to.
     * @param setOauthToken  A boolean to flag whether to set the OAuth2 bearer access token in the Authorization header of request.
     * @return HttpURLConnection Http connection to the OpenSRP server.
     */
    private HttpURLConnection initializeHttp(String requestURLPath, boolean setOauthToken) throws IOException, URISyntaxException {

        HttpURLConnection urlConnection = getHttpURLConnection(requestURLPath);

        if (urlConnection instanceof HttpsURLConnection) {
            OpensrpSSLHelper opensrpSSLHelper = new OpensrpSSLHelper(context, configuration);
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(opensrpSSLHelper.getSSLSocketFactory());
        }
        urlConnection.setConnectTimeout(getConnectTimeout());
        urlConnection.setReadTimeout(getReadTimeout());
        if (setOauthToken) {
            AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
            if (AccountHelper.getOauthAccountByNameAndType(allSharedPreferences.fetchRegisteredANM(), authenticatorXml.getAccountType()) != null)
                urlConnection.setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, new StringBuilder(AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BEARER + " ").append(getBearerToken()).toString());
        }
        return urlConnection;
    }

    @VisibleForTesting
    protected String getBearerToken() {
        return AccountHelper.getOAuthToken(allSharedPreferences.fetchRegisteredANM(), CoreLibrary.getInstance().getAccountAuthenticatorXml().getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER);
    }

    @VisibleForTesting
    protected HttpURLConnection getHttpURLConnection(String requestURLPath) throws IOException, URISyntaxException {
        URI inputURI = new URI(requestURLPath.replaceAll(" ", "%20"));
        URL url = inputURI.normalize().toURL();
        return (HttpURLConnection) url.openConnection();
    }

    public Response<String> fetch(String requestURLPath) {
        try {

            HttpURLConnection urlConnection = initializeHttp(requestURLPath, true);

            int responseCode = urlConnection.getResponseCode();

            allSharedPreferences.updateLastAuthenticationHttpStatus(responseCode);

            //If unauthorized invalidate cache of old token retry
            if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {

                invalidateExpiredCachedAccessToken();

                urlConnection = initializeHttp(requestURLPath, true);

            }

            return processResponse(urlConnection);

        } catch (IOException | URISyntaxException exception) {
            Timber.e(exception, "EXCEPTION %s", exception.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public void invalidateExpiredCachedAccessToken() {
        AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
        String authToken = AccountHelper.getCachedOAuthToken(allSharedPreferences.fetchRegisteredANM(), authenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER);
        if (authToken != null)
            AccountHelper.invalidateAuthToken(authenticatorXml.getAccountType(), authToken);
    }

    public Response<String> post(String postURLPath, String jsonPayload) {
        HttpURLConnection urlConnection;
        try {

            urlConnection = generatePostRequest(postURLPath, jsonPayload);

            int responseCode = urlConnection.getResponseCode();

            allSharedPreferences.updateLastAuthenticationHttpStatus(responseCode);

            //If unauthorized invalidate cache of old token retry
            if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {

                invalidateExpiredCachedAccessToken();

                urlConnection = generatePostRequest(postURLPath, jsonPayload);

            }

            return processResponse(urlConnection);

        } catch (IOException | URISyntaxException exception) {
            Timber.e(exception, "EXCEPTION: %s", exception.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    @NonNull
    @VisibleForTesting
    protected HttpURLConnection generatePostRequest(String postURLPath, String jsonPayload) throws IOException, URISyntaxException {
        HttpURLConnection urlConnection;
        urlConnection = initializeHttp(postURLPath, true);

        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        urlConnection.setRequestProperty("Content-Encoding", "gzip");

        byte[] content = gzipCompression.compress(jsonPayload);
        urlConnection.setFixedLengthStreamingMode(content.length);

        OutputStream os = urlConnection.getOutputStream();
        BufferedOutputStream writer = new BufferedOutputStream(os);

        writer.write(content);
        writer.flush();
        writer.close();
        os.close();

        urlConnection.connect();
        return urlConnection;
    }

    public Response<String> postWithJsonResponse(String postURLPath, String jsonPayload) {
        logResponse(postURLPath, jsonPayload);
        return post(postURLPath, jsonPayload);
    }

    private void logResponse(String postURLPath, String jsonPayload) {
        Timber.d("postURLPath: %s and jsonPayLoad: %s", postURLPath, jsonPayload);
    }

    public LoginResponse urlCanBeAccessWithGivenCredentials(String requestURL, String userName, char[] password) {
        LoginResponse loginResponse = null;
        HttpURLConnection urlConnection = null;
        String url = null;
        try {
            url = requestURL.replaceAll("\\s+", "");
            urlConnection = initializeHttp(url, false);

            byte[] credentials = SecurityHelper.toBytes(new StringBuffer(userName).append(':').append(password));
            final String basicAuth = AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BASIC + " " + Base64.encodeToString(credentials, Base64.NO_WRAP);
            urlConnection.setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, basicAuth);
            int statusCode = urlConnection.getResponseCode();
            InputStream inputStream;
            String responseString = "";
            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();
            if (inputStream != null)
                responseString = IOUtils.toString(inputStream);
            if (statusCode == HttpURLConnection.HTTP_OK) {

                Timber.d("response String: %s using request url %s", responseString, url);
                LoginResponseData responseData = getResponseBody(responseString);
                loginResponse = retrieveResponse(responseData);
            } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
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
        } catch (MalformedURLException | URISyntaxException exception) {
            Timber.e(exception, "Failed to check credentials bad url %s", url);
            loginResponse = MALFORMED_URL;
        } catch (SocketTimeoutException exception) {
            Timber.e(exception, "SocketTimeoutException when authenticating %s", userName);
            loginResponse = TIMEOUT;
            Timber.e(exception, "Failed to check credentials of: %s using %s . Error: %s", userName, url, exception.toString());
        } catch (IOException exception) {
            Timber.e(exception, "Failed to check credentials of: %s  using %s . Error: %s", userName, url, exception.toString());
            loginResponse = NO_INTERNET_CONNECTIVITY;
        } finally {
            closeConnection(urlConnection);
        }
        return loginResponse;
    }

    public DownloadStatus downloadFromUrl(String url, String filename) {

        Response<DownloadStatus> status = downloadFromURL(url, filename);
        Timber.d("downloading file name : %s and url %s", filename, url);
        return status.payload();
    }

    public Response<String> fetchWithCredentials(String requestURL, String accessToken) {

        try {

            HttpURLConnection urlConnection = initializeHttp(requestURL, false);
            urlConnection.setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, new StringBuilder(AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BEARER + " ").append(accessToken).toString());

            int responseCode = urlConnection.getResponseCode();

            allSharedPreferences.updateLastAuthenticationHttpStatus(responseCode);
            //If unauthorized invalidate cache of old token retry
            if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {

                AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
                AccountHelper.invalidateAuthToken(authenticatorXml.getAccountType(), accessToken);

                urlConnection = initializeHttp(requestURL, true);

            }
            return processResponse(urlConnection);

        } catch (IOException | URISyntaxException exception) {
            Timber.e(exception, "EXCEPTION %s", exception.toString());
            return new Response<>(ResponseStatus.failure, null);
        }

    }

    private Response<String> processResponse(HttpURLConnection urlConnection) {
        String responseString;
        String totalRecords;
        int statusCode = -1;
        try {
            statusCode = urlConnection.getResponseCode();

            InputStream inputStream = null;

            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            responseString = IOUtils.toString(inputStream);

            totalRecords = urlConnection.getHeaderField(AllConstants.SyncProgressConstants.TOTAL_RECORDS);

            Timber.d("response string: %s using url %s", responseString, urlConnection.getURL());

        } catch (MalformedURLException exception) {
            Timber.e(exception, "%s %s", MALFORMED_URL, exception.toString());
            ResponseStatus.failure.setDisplayValue(ResponseErrorStatus.malformed_url.name());
            return new Response<>(ResponseStatus.failure, null);
        } catch (SocketTimeoutException exception) {
            Timber.e(exception, "%s %s", TIMEOUT, exception.toString());
            ResponseStatus.failure.setDisplayValue(ResponseErrorStatus.timeout.name());
            return new Response<>(ResponseStatus.failure, null);
        } catch (IOException exception) {
            Timber.e(exception, "%s %s", NO_INTERNET_CONNECTIVITY, exception.toString());
            return new Response<>(ResponseStatus.failure, null);
        } finally {
            closeConnection(urlConnection);
        }
        return new Response<>(statusCode >= HttpURLConnection.HTTP_BAD_REQUEST ? ResponseStatus.failure : ResponseStatus.success, responseString)
                .withTotalRecords(Utils.tryParseLong(totalRecords, 0));
    }


    /**
     * @param urlString This is the url of the image, TAG,
     * @param image     This is the image to be uploaded to opensrp server.
     * @return String This returns the response obtained from the opensrp server.
     * @author Rodgers Andati
     * @since 2019-04-25
     * This method uploads an image to opensrp server. Migration from the old method that used httpclient
     */
    public String httpImagePost(String urlString, ProfileImage image) {
        OutputStream outputStream;
        PrintWriter writer;
        String responseString = "";
        HttpURLConnection httpUrlConnection = null;

        try {

            httpUrlConnection = initializeHttp(urlString, true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpUrlConnection.setChunkedStreamingMode(FILE_UPLOAD_CHUNK_SIZE_BYTES);

            outputStream = httpUrlConnection.getOutputStream();
            writer = getPrintWriter(outputStream);

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
                Timber.e("SERVER RESPONSE %s Server returned non-OK status: %s :-", status, httpUrlConnection.getResponseMessage());
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpUrlConnection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    Timber.e("SERVER RESPONSE %s", line);
                }
                reader.close();
            }

        } catch (ProtocolException exception) {
            Timber.e(exception, "Protocol exception %s", exception.toString());
        } catch (SocketTimeoutException exception) {
            Timber.e(exception, "SocketTimeout %s %s", TIMEOUT, exception.toString());
        } catch (MalformedURLException | URISyntaxException exception) {
            Timber.e(exception, "MalformedUrl %s %s", MALFORMED_URL, exception.toString());
        } catch (IOException exception) {
            Timber.e(exception, "IOException %s %s", NO_INTERNET_CONNECTIVITY, exception.toString());
        } finally {

            closeConnection(httpUrlConnection);
        }
        return responseString;
    }

    @VisibleForTesting
    @NonNull
    protected PrintWriter getPrintWriter(OutputStream outputStream) throws UnsupportedEncodingException {
        return new PrintWriter(new OutputStreamWriter(outputStream, CharEncoding.UTF_8), true);
    }

    private void attachImage(PrintWriter writer, ProfileImage image, OutputStream outputStream) throws IOException {
        File uploadImageFile = getDownloadFolder(image.getFilepath());
        String fileName = uploadImageFile.getName();

        writer.append("--" + boundary).append(crlf);
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"").append(crlf);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(crlf);
        writer.append("Content-Transfer-Encoding: binary").append(crlf);
        writer.append(crlf);
        writer.flush();

        FileInputStream inputStream = getFileInputStream(uploadImageFile);
        byte[] buffer = new byte[FILE_UPLOAD_CHUNK_SIZE_BYTES];
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
        writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"").append(crlf);
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append(crlf);
        writer.append(crlf);
        writer.append(paramValue).append(crlf);
        writer.flush();

        Timber.d("http agent param name: %s and param value %s ", paramName, paramValue);
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
     * Sets the read timeout in milliseconds
     * <p>
     * Setting this will call {@link java.net.HttpURLConnection#setReadTimeout(int)}
     * on the {@link java.net.HttpURLConnection} instance in {@link org.smartregister.service.HTTPAgent}
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
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
     * <p>
     * Setting this will call {@link java.net.HttpURLConnection#setConnectTimeout(int)}
     * on the {@link java.net.HttpURLConnection} instance in {@link org.smartregister.service.HTTPAgent}
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public AccountResponse oauth2authenticateCore(StringBuffer requestParamBuffer, String grantType, String tokenEndpointURL) {


        AccountError accountError;
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        BufferedOutputStream writer = null;
        InputStream inputStream;

        try {
            urlConnection = initializeHttp(tokenEndpointURL, false);

            String clientId = CoreLibrary.getInstance().getSyncConfiguration().getOauthClientId();
            String clientSecret = CoreLibrary.getInstance().getSyncConfiguration().getOauthClientSecret();

            final String base64Auth = BaseEncoding.base64().encode(new StringBuffer(clientId).append(':').append(clientSecret).toString().getBytes(CharEncoding.UTF_8));

            requestParamBuffer.append("&grant_type=").append(grantType);

            urlConnection.setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BASIC + " " + base64Auth);


            byte[] postData = requestParamBuffer.toString().getBytes(CharEncoding.UTF_8);
            int postDataLength = postData.length;

            urlConnection.setFixedLengthStreamingMode(postDataLength);
            urlConnection.setDoOutput(true);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "utf-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            urlConnection.setUseCaches(false);

            outputStream = urlConnection.getOutputStream();
            writer = new BufferedOutputStream(outputStream);
            writer.write(postData);
            writer.flush();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();
            String responseString = IOUtils.toString(inputStream);
            if (statusCode == HttpURLConnection.HTTP_OK) {

                Timber.d("response String: %s using request url %s", responseString, tokenEndpointURL);

                AccountResponse accountResponse = gson.fromJson(responseString, AccountResponse.class);
                accountResponse.setStatus(statusCode);
                return accountResponse;

            } else {

                accountError = gson.fromJson(responseString, AccountError.class);
                return new AccountResponse(statusCode, accountError);

            }
        } catch (MalformedURLException | URISyntaxException exception) {
            Timber.e(exception, "Failed to check credentials bad url %s", tokenEndpointURL);
            accountError = new AccountError(0, MALFORMED_URL.name());

        } catch (SocketTimeoutException exception) {
            Timber.e(exception, "SocketTimeoutException when authenticating");

            accountError = new AccountError(0, TIMEOUT.name());

            Timber.e(exception, "Failed to check credentials using %s . Error: %s", tokenEndpointURL, exception.toString());
        } catch (IOException exception) {
            Timber.e(exception, "Failed to check credentials using %s . Error: %s", tokenEndpointURL, exception.toString());
            accountError = new AccountError(0, NO_INTERNET_CONNECTIVITY.name());

        } finally {

            closeConnection(urlConnection);
            closeIOStream(writer);
            closeIOStream(outputStream);

        }

        //If we got here there was an issue with no server status code
        return new AccountResponse(0, accountError);

    }

    public AccountResponse oauth2authenticate(String username, char[] password, String grantType, String tokenEndpointURL) {
        StringBuffer requestParamBuilder = getRequestParams(username, password);
        return oauth2authenticateCore(requestParamBuilder, grantType, tokenEndpointURL);
    }

    private StringBuffer getRequestParams(String username, char[] password) {
        StringBuffer requestParamBuilder = new StringBuffer();
        requestParamBuilder.append("&username=").append(username);
        String urlEncodedPassword = new String(password);
        try {
            urlEncodedPassword = URLEncoder.encode(urlEncodedPassword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e);
        }
        requestParamBuilder.append("&password=").append(urlEncodedPassword);
        return requestParamBuilder;
    }

    public AccountResponse oauth2authenticateRefreshToken(String refreshToken) {

        String tokenEndpointURL = allSharedPreferences.getPreferences().getString(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL, "");
        StringBuffer requestParamBuilder = new StringBuffer();
        requestParamBuilder.append("&refresh_token=").append(refreshToken);

        return oauth2authenticateCore(requestParamBuilder, AccountHelper.OAUTH.GRANT_TYPE.REFRESH_TOKEN, tokenEndpointURL);
    }

    public LoginResponse fetchUserDetails(String requestURL, String oauthAccessToken) {
        LoginResponse loginResponse = null;
        String url = null;
        HttpURLConnection urlConnection = null;
        try {
            url = requestURL.replaceAll("\\s+", "");

            urlConnection = initializeHttp(url, false);
            urlConnection.setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, new StringBuilder(AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BEARER + " ").append(oauthAccessToken).toString());

            int statusCode = urlConnection.getResponseCode();
            InputStream inputStream;
            String responseString = null;
            if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST)
                inputStream = urlConnection.getErrorStream();
            else
                inputStream = urlConnection.getInputStream();

            if (inputStream != null)
                responseString = IOUtils.toString(inputStream);

            if (statusCode == HttpURLConnection.HTTP_OK) {

                Timber.d("response String: %s using request url %s", responseString, url);
                LoginResponseData responseData = getResponseBody(responseString);
                loginResponse = retrieveResponse(responseData);
            } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                Timber.e("Invalid credentials accessing: %s using token %s", url, oauthAccessToken);
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
                Timber.e("Bad response from Server. Status code: %s using %s ", statusCode, url);
                loginResponse = UNKNOWN_RESPONSE;
            }
        } catch (MalformedURLException | URISyntaxException exception) {
            Timber.e(exception, "Failed to check credentials bad url %s", url);
            loginResponse = MALFORMED_URL;
        } catch (SocketTimeoutException exception) {
            Timber.e(exception, "SocketTimeoutException when authenticating");
            loginResponse = TIMEOUT;
        } catch (IOException exception) {
            Timber.e(exception, "Failed to connect to %s check, check internet connection. Error: %s", url, exception.toString());
            loginResponse = NO_INTERNET_CONNECTIVITY;
        } finally {

            closeConnection(urlConnection);
        }
        return loginResponse;
    }

    /**
     * @param downloadURL_ This is the url of the image
     * @param fileName     This is how the image should be name after it has been downloaded.
     * @return Response<DownloadStatus> This returns whether the download succeeded or failed.
     */
    public Response<DownloadStatus> downloadFromURL(String downloadURL_, String fileName) {
        return downloadFromURL(downloadURL_, fileName, new HashMap<>());
    }

    /**
     * @param downloadURL_ This is the url of the image
     * @param fileName     This is how the image should be name after it has been downloaded.
     * @param detailsMap   store values that might be needed after save of file
     * @return Response<DownloadStatus> This returns whether the download succeeded or failed.
     */
    public Response<DownloadStatus> downloadFromURL(String downloadURL_, String fileName, Map<String, String> detailsMap) {

        HttpURLConnection httpUrlConnection = null;
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fos = null;
        try {

            File dir = getSDCardDownloadPath();
            String tempFileName = fileName;
            if (!dir.exists()) {
                dir.mkdirs();
            }

            long startTime = System.currentTimeMillis();
            Timber.d("DownloadFormService %s", "download begin");
            Timber.d("DownloadFormService %s %s", "download url: ", downloadURL_);
            Timber.d("DownloadFormService %s %s", "download file name: ", tempFileName);


            String downloadURL = downloadURL_.replaceAll("\\s+", "");

            httpUrlConnection = initializeHttp(downloadURL, true);

            int status = httpUrlConnection.getResponseCode();

            allSharedPreferences.updateLastAuthenticationHttpStatus(status);

            if (status == HttpURLConnection.HTTP_OK) {

                if (StringUtils.isBlank(httpUrlConnection.getContentType()))
                    return new Response<>(ResponseStatus.success,
                            DownloadStatus.nothingDownloaded);

                int periodIndex = tempFileName.lastIndexOf(".");
                if (periodIndex == -1) {
                    String mimeType = httpUrlConnection.getContentType().split(";")[0];
                    tempFileName = String.format("%s.%s", tempFileName, MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType));
                }

                File file = getFile(tempFileName, dir);

                detailsMap.put(AllConstants.DownloadFileConstants.FILE_NAME, tempFileName);
                detailsMap.put(AllConstants.DownloadFileConstants.FILE_PATH, file.getPath());

                bufferedInputStream = getBufferedInputStream(httpUrlConnection.getInputStream());

                Timber.d("DownloadFormService file content type : %s", httpUrlConnection.getContentType());

                fos = getFileOutputStream(file);

                byte data[] = new byte[DOWNLOAD_BUFFER_SIZE];
                int count;
                while ((count = bufferedInputStream.read(data, 0, DOWNLOAD_BUFFER_SIZE)) != -1) {
                    fos.write(data, 0, count);
                }
                fos.flush();

                Timber.d("DownloadFormService %s %d %s",
                        "download finished in ", ((System.currentTimeMillis() - startTime) / 1000)
                        , " sec");

            } else {
                Timber.d("RESPONSE %s %s ", "Server returned non-OK status: ", status);
                return new Response<>(ResponseStatus.failure, DownloadStatus.failedDownloaded);
            }

        } catch (IOException | URISyntaxException exception) {
            Timber.d(exception, "DownloadFormService");
            return new Response<>(ResponseStatus.success, DownloadStatus.failedDownloaded);
        } finally {

            try {
                if (fos != null)
                    fos.close();

                if (bufferedInputStream != null)
                    bufferedInputStream.close();

            } catch (IOException e) {
                Timber.e(e);
            }

            closeConnection(httpUrlConnection);
        }

        return new Response<>(ResponseStatus.success, DownloadStatus.downloaded);
    }

    @VisibleForTesting
    @NonNull
    protected FileInputStream getFileInputStream(File uploadImageFile) throws FileNotFoundException {
        return new FileInputStream(uploadImageFile);
    }

    @VisibleForTesting
    @NonNull
    protected FileOutputStream getFileOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    @VisibleForTesting
    @NonNull
    protected BufferedInputStream getBufferedInputStream(InputStream inputStream) {
        return new BufferedInputStream(inputStream);
    }

    @VisibleForTesting
    @NonNull
    protected File getFile(String fileName, File dir) {
        return new File(dir, fileName);
    }

    @VisibleForTesting
    protected File getSDCardDownloadPath() {
        return getDownloadFolder(FormPathService.sdcardPathDownload);
    }

    @NonNull
    @VisibleForTesting
    protected File getDownloadFolder(String sdcardPathDownload) {
        return new File(sdcardPathDownload);
    }

    public boolean verifyAuthorization() {

        String userInfoUrl = allSharedPreferences.getPreferences().getString(AccountHelper.CONFIGURATION_CONSTANTS.USERINFO_ENDPOINT_URL, "");

        if (StringUtils.isBlank(userInfoUrl)) {

            return verifyAuthorizationLegacy();
        }

        HttpURLConnection urlConnection = null;

        InputStream inputStream = null;

        try {

            urlConnection = initializeHttp(userInfoUrl, true);

            AccountUserInfo userInfo = null;

            int responseCode = urlConnection.getResponseCode();

            allSharedPreferences.updateLastAuthenticationHttpStatus(responseCode);

            //If unauthorized invalidate cache of old token retry
            if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {

                invalidateExpiredCachedAccessToken();

                urlConnection = initializeHttp(userInfoUrl, true);

            }

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                inputStream = urlConnection.getInputStream();

                String responseString = IOUtils.toString(inputStream);

                userInfo = gson.fromJson(responseString, AccountUserInfo.class);

            } else {
                Timber.w("Error occurred verifying authorization, User will not be logged off");
            }

            if (userInfo == null || userInfo.getEnabled() == null)
                return verifyAuthorizationLegacy();

            if (userInfo.getEnabled()) {

                Timber.i("User is Authorized");
                return true;

            } else {

                Timber.i("User not authorized. User access was revoked, will log off user");
                return false;
            }

        } catch (IOException | URISyntaxException exception) {

            Timber.e(exception);

        } finally {

            closeConnection(urlConnection);
            closeIOStream(inputStream);
        }
        return true;
    }

    //For backward compatibility
    public boolean verifyAuthorizationLegacy() {

        String baseUrl = configuration.dristhiBaseURL();

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        final String username = allSharedPreferences.fetchRegisteredANM();
        baseUrl = baseUrl + DETAILS_URL + username;

        HttpURLConnection urlConnection = null;

        try {

            urlConnection = initializeHttp(baseUrl, true);

            int statusCode = urlConnection.getResponseCode();

            allSharedPreferences.updateLastAuthenticationHttpStatus(statusCode);

            //If unauthorized invalidate cache of old token retry
            if (HttpURLConnection.HTTP_UNAUTHORIZED == statusCode) {

                invalidateExpiredCachedAccessToken();

                urlConnection = initializeHttp(baseUrl, true);

                if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
                    return true;

                } else if (HttpURLConnection.HTTP_UNAUTHORIZED == urlConnection.getResponseCode()) {

                    Timber.i("User not authorized. User access was revoked, will log off user");
                    return false;
                }

            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                Timber.w("Error occurred verifying authorization, User will not be logged off");
            } else {
                Timber.i("User is Authorized");
            }

        } catch (IOException | URISyntaxException exception) {
            Timber.e(exception);
        } finally {

            closeConnection(urlConnection);
        }
        return true;
    }

    public AccountConfiguration fetchOAuthConfiguration() {

        String baseUrl = configuration.dristhiBaseURL();

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        baseUrl = baseUrl + AccountHelper.OAUTH.ACCOUNT_CONFIGURATION_ENDPOINT;

        HttpURLConnection urlConnection = null;

        InputStream inputStream = null;
        try {

            urlConnection = getHttpURLConnection(baseUrl);

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {

                inputStream = urlConnection.getInputStream();

                String responseString = IOUtils.toString(inputStream);

                return gson.fromJson(responseString, AccountConfiguration.class);
            }

        } catch (Exception exception) {
            Timber.e(exception);
        } finally {

            closeConnection(urlConnection);
            closeIOStream(inputStream);

        }
        return null;
    }

    private void closeConnection(HttpURLConnection urlConnection) {
        if (urlConnection != null) {
            try {
                urlConnection.disconnect();
            } catch (Exception exception) {
                Timber.e(exception, "Error closing input HttpUrlConnection");
            }

        }

    }

    private void closeIOStream(Closeable inputStream) {
        if (inputStream != null) {

            try {
                inputStream.close();
            } catch (IOException exception) {

                Timber.e(exception, "Error closing input stream");
            }

        }

    }
}