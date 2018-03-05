package org.smartregister.service;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.smartregister.DristhiConfiguration;
import org.smartregister.client.GZipEncodingHttpClient;
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
import org.smartregister.util.FileUtilities;
import org.smartregister.util.HttpResponseUtil;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;

import static org.apache.http.HttpStatus.SC_OK;
import static org.smartregister.AllConstants.REALM;
import static org.smartregister.domain.LoginResponse.EMPTY_REPONSE;
import static org.smartregister.domain.LoginResponse.MALFORMED_URL;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.TIMEOUT;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.util.HttpResponseUtil.getResponseBody;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logWarn;

public class HTTPAgent {
    private static final String TAG = HTTPAgent.class.getCanonicalName();
    private GZipEncodingHttpClient httpClient;
    private Context context;
    private AllSettings settings;
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;

    public static final int CONNECTION_TIMEOUT = 60000;

    public HTTPAgent(Context context, AllSettings settings, AllSharedPreferences
            allSharedPreferences, DristhiConfiguration configuration) {
        this.context = context;
        this.settings = settings;
        this.allSharedPreferences = allSharedPreferences;
        this.configuration = configuration;

        setupHttpClient();
    }

    public void setupHttpClient() {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);

        ConnManagerParams.setTimeout(basicHttpParams, CONNECTION_TIMEOUT);

        SchemeRegistry registry = new SchemeRegistry();
        OpensrpSSLHelper opensrpSSLHelper = new OpensrpSSLHelper(context, configuration);
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", opensrpSSLHelper.getSslSocketFactoryWithOpenSrpCertificate(), 443));

        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(basicHttpParams,
                registry);
        httpClient = new GZipEncodingHttpClient(
                new DefaultHttpClient(connectionManager, basicHttpParams));
    }

    public Response<String> fetch(String requestURLPath) {
        try {
            setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
            String responseContent = IOUtils
                    .toString(httpClient.fetchContent(new HttpGet(requestURLPath)));
            return new Response<>(ResponseStatus.success, responseContent);
        } catch (Exception e) {
            logWarn(e.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public Response<String> post(String postURLPath, String jsonPayload) {
        try {
            setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
            HttpPost httpPost = new HttpPost(postURLPath);
            Log.v("jsonpayload", jsonPayload);
            FileUtilities fu = new FileUtilities();
            fu.write("jsonpayload.txt", jsonPayload);

            StringEntity entity = new StringEntity(jsonPayload, HTTP.UTF_8);
            entity.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.postContent(httpPost);

            ResponseStatus responseStatus =
                    response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED
                            ? ResponseStatus.success : ResponseStatus.failure;
            response.getEntity().consumeContent();
            return new Response<>(responseStatus, null);
        } catch (Exception e) {
            logWarn(e.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public Response<String> postWithJsonResponse(String postURLPath, String jsonPayload) {
        try {
            setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
            HttpPost httpPost = new HttpPost(postURLPath);
            Log.v("jsonpayload", jsonPayload);
            FileUtilities fu = new FileUtilities();
            fu.write("jsonpayload.txt", jsonPayload);

            StringEntity entity = new StringEntity(jsonPayload, HTTP.UTF_8);
            entity.setContentType("application/json; charset=utf-8");
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.postContent(httpPost);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                return new Response<>(ResponseStatus.failure, "Invalid status code: " + response.getStatusLine().getStatusCode());
            } else {
                String payload = IOUtils.toString(HttpResponseUtil.getResponseStream(response));
                return new Response<>(ResponseStatus.success, payload);
            }
        } catch (Exception e) {
            logWarn(e.toString());
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public LoginResponse urlCanBeAccessWithGivenCredentials(String requestURL, String userName,
                                                            String password) {
        setCredentials(userName, password);
        try {
            requestURL = requestURL.replaceAll("\\s+", "");
            HttpResponse response = httpClient.execute(new HttpGet(requestURL));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                LoginResponseData responseData = getResponseBody(response);
                if (responseData == null) {
                    logError("Empty Response using " + requestURL);
                    return EMPTY_REPONSE;
                }
                return SUCCESS.withPayload(responseData);
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                logError("Invalid credentials for: " + userName + " using " + requestURL);
                return UNAUTHORIZED;
            } else {
                logError("Bad response from Dristhi. Status code:  " + statusCode + " username: "
                        + userName + " using " + requestURL);
                return UNKNOWN_RESPONSE;
            }
        } catch (ConnectionPoolTimeoutException | SocketTimeoutException e) {
            logError(e.getMessage());
            return TIMEOUT;
        } catch (IOException e) {
            logError("Failed to check credentials of: " + userName + " using " + requestURL + ". "
                    + "" + "" + "Error: " + e.toString());
            return NO_INTERNET_CONNECTIVITY;
        } catch (IllegalArgumentException e) {
            logError(e.getMessage());
            return MALFORMED_URL;

        }
    }

    public DownloadStatus downloadFromUrl(String url, String filename) {
        setCredentials(allSharedPreferences.fetchRegisteredANM(), settings.fetchANMPassword());
        Response<DownloadStatus> status = DownloadForm.DownloadFromURL(url, filename, httpClient);
        return status.payload();
    }

    private void setCredentials(String userName, String password) {
        httpClient.getCredentialsProvider()
                .setCredentials(new AuthScope(configuration.host(), configuration.port(), REALM),
                        new UsernamePasswordCredentials(userName, password));
    }

    public Response<String> fetchWithCredentials(String uri, String username, String password) {
        setCredentials(username, password);
        try {
            String responseContent = IOUtils.toString(httpClient.fetchContent(new HttpGet(uri)));
            return new Response<>(ResponseStatus.success, responseContent);
        } catch (IOException | ParseException e) {
            logError("Failed to fetch unique id");
            return new Response<>(ResponseStatus.failure, null);
        }
    }

    public String httpImagePost(String url, ProfileImage image) {

        String responseString = "";
        try {
            File uploadFile = new File(image.getFilepath());
            if (uploadFile.exists()) {
                setCredentials(allSharedPreferences.fetchRegisteredANM(),
                        settings.fetchANMPassword());

                HttpPost httpost = new HttpPost(url);

                httpost.setHeader("Accept", "multipart/form-data");
                File filetoupload = new File(image.getFilepath());
                Log.v("file to upload", "" + filetoupload.length());
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("anm-id", new StringBody(image.getAnmId()));
                entity.addPart("entity-id", new StringBody(image.getEntityID()));
                entity.addPart("content-type", new StringBody(
                        image.getContenttype() != null ? image.getContenttype() : "jpeg"));
                entity.addPart("file-category", new StringBody(
                        image.getFilecategory() != null ? image.getFilecategory() : "profilepic"));
                ContentBody cbFile = new FileBody(uploadFile, "image/jpeg");
                entity.addPart("file", cbFile);
                httpost.setEntity(entity);
                String authToken = null;
                HttpResponse response = httpClient.postContent(httpost);
                responseString = EntityUtils.toString(response.getEntity());
                Log.v("response so many", responseString);
                int RESPONSE_OK = 200;
                int RESPONSE_OK_ = 201;

                if (response.getStatusLine().getStatusCode() != RESPONSE_OK_
                        && response.getStatusLine().getStatusCode() != RESPONSE_OK) {
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return responseString;
    }

    private String nullToLiteral(String s) {
        if (s == null) {
            return "";
        }
        return s;

    }


}
