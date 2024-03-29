package org.smartregister.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.google.common.io.BaseEncoding;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.TestSyncConfiguration;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountConfiguration;
import org.smartregister.account.AccountHelper;
import org.smartregister.account.AccountResponse;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.CredentialsHelper;
import org.smartregister.util.LoginResponseTestData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class HTTPAgentTest {
    public static final String TEST_BASE_URL = "https://my-server.com/";
    protected static final String TEST_BASE_ENTITY_ID = "23ka2-3e23h2-n3g2i4-9q3b-yts4-20";
    protected static final String TEST_ACCOUNT_TYPE = "org.smartregister.my-health-app";
    private static final String TEST_USERNAME = "demo";
    private static final char[] TEST_PASSWORD = "password".toCharArray();
    private static final String TEST_TOKEN_ENDPOINT = "https://my-server.com/oauth/token";
    private static final String SECURE_RESOURCE_ENDPOINT = "https://my-server.com/my/secure/resource";
    private static final String KEYClOAK_CONFIGURATION_ENDPOINT = "https://my-server.com/rest/config/keycloak";
    private static final String USER_DETAILS_ENDPOINT = "https://my-server.com/opensrp/security/authenticate";
    private static final String TEST_IMAGE_DOWNLOAD_ENDPOINT = "https://my-server.com/opensrp/multimedia/myimage.jpg";
    private static final String TEST_IMAGE_UPLOAD_ENDPOINT = "https://my-server.com/opensrp/multimedia/";
    private static final String TEST_USER_INFO_ENDPOINT = "https://keycloak.my-server.com/auth/userinfo";
    private static final String TEST_IMAGE_FILE_PATH = "file://usr/sdcard/dev0/data/org.smartregister.core/localimage.jpg";
    private static final String TOKEN_REQUEST_SERVER_RESPONSE = "{\r\naccess_token:\"1r9A8zi5E3r@Zz\",\r\ntoken_type: \"bearer\",\r\nrefresh_token: \"text_token\",\r\nexpires_in: 3600,\r\nrefresh_expires_in: 36000,\r\nscope: \"read write trust\"\r\n\r\n}";
    private static final String TOKEN_BAD_REQUEST_SERVER_RESPONSE = "{status_code:400,\"error\":\"invalid_grant\",\"error_description\":\"Code not valid\"}";
    private static final String TOKEN_INTERNAL_SERVER_RESPONSE = "{status_code:500,\"error\":\"internal server error\",\"error_description\":\"Oops, something went wrong\"}";
    private static final String OAUTH_CONFIGURATION_SERVER_RESPONSE = "{\"issuer\":\"https://my-server.com/oauth/issuer\",\r\n\"authorization_endpoint\": \"https://my-server.com/oauth/auth\",\r\n\"token_endpoint\": \"https://my-server.com/oauth/token\",\r\n\"grant_types_supported\":[\"authorization code\",\"implicit\",\"password\"]\r\n}";
    private static final String FETCH_DATA_REQUEST_SERVER_RESPONSE = "{status:{\"response_status\":\"success\"},payload: \"My secure resources from the server\"\r\n\r\n}";
    private static final String SAMPLE_POST_REQUEST_PAYLOAD = "{\"payload\":\"My POST Payload\"}";
    private static final String ACCOUNT_INFO_REQUEST_SERVER_RESPONSE = "{   \"name\":\"Test User\",   \"email\":\"demo@smartegister.org\",   \"enabled\":true,   \"preferred_username\":\"demo\",   \"email_verified\":true}";
    private static final String TEST_FILE_NAME = "Profile";
    private final String SAMPLE_TEST_TOKEN = "sample-test-token";
    private final String SAMPLE_REFRESH_TOKEN = "sample-refresh-token";
    @Mock
    private android.content.Context context;
    @Mock
    private Context openSrpContext;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private DristhiConfiguration dristhiConfiguration;
    @Mock
    private ProfileImage profileImage;
    @Mock
    private AccountAuthenticatorXml accountAuthenticatorXml;
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private AccountManager accountManager;
    @Mock
    private SyncConfiguration syncConfiguration;
    @Mock
    private HttpURLConnection httpURLConnection;
    @Mock
    private HttpsURLConnection httpsURLConnection;
    @Mock
    private OutputStream outputStream;

    private InputStream inputStream;
    @Mock
    private FileInputStream fileInputStream;
    @Mock
    private InputStream errorStream;
    @Mock
    private File dirFile;
    @Mock
    private File file;
    @Mock
    private BufferedInputStream bufferedInputStream;
    @Mock
    private FileOutputStream fileOutputStream;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private UserService userService;

    private HTTPAgent httpAgent;

    private AutoCloseable autoCloseable;

    @After
    public void tearDownSuper() throws Exception {
        if (autoCloseable != null)
            autoCloseable.close();

        try {
            Mockito.validateMockitoUsage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);

        try (MockedStatic<CredentialsHelper> credentialsHelperMockedStatic = Mockito.mockStatic(CredentialsHelper.class)) {
            credentialsHelperMockedStatic.when(() -> CredentialsHelper.shouldMigrate()).thenReturn(false);

            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

            openSrpContext.updateApplicationContext(context);

            Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
            Mockito.doReturn(allSharedPreferences).when(openSrpContext).allSharedPreferences();
            Mockito.doReturn(userService).when(openSrpContext).userService();
            Mockito.doNothing().when(allSharedPreferences).migratePassphrase();
            Mockito.doReturn(allSharedPreferences).when(userService).getAllSharedPreferences();
            Mockito.doReturn(accountAuthenticatorXml).when(coreLibrary).getAccountAuthenticatorXml();

            Account account = new Account(TEST_USERNAME, TEST_ACCOUNT_TYPE);

            //Weird bug, the properties of the account object are never set by the constructor call above
            Whitebox.setInternalState(account, "name", TEST_USERNAME);
            Whitebox.setInternalState(account, "type", TEST_ACCOUNT_TYPE);

            Mockito.doReturn(new Account[]{account}).when(accountManager).getAccountsByType(ArgumentMatchers.anyString());

            Mockito.when(accountManager.blockingGetAuthToken(account, accountAuthenticatorXml.getAccountType(), true)).thenReturn(SAMPLE_TEST_TOKEN);

            inputStream = IOUtils.toInputStream("{\"name\":\"Marvel\"}", StandardCharsets.UTF_8);
            bufferedInputStream = new BufferedInputStream(inputStream);

            Mockito.doReturn(context).when(context).getApplicationContext();

            Mockito.doReturn(accountManager).when(coreLibrary).getAccountManager();
            Mockito.doReturn(TEST_ACCOUNT_TYPE).when(accountAuthenticatorXml).getAccountType();
            Mockito.doReturn(TEST_USERNAME).when(accountAuthenticatorXml).getAccountLabel();
            Mockito.doReturn(accountAuthenticatorXml).when(coreLibrary).getAccountAuthenticatorXml();

            Mockito.doReturn(accountManager).when(coreLibrary).getAccountManager();
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
            Mockito.doReturn(1).when(syncConfiguration).getMaxAuthenticationRetries();
            Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();

            Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
            Mockito.doReturn(true).when(sharedPreferences).getBoolean(AccountHelper.CONFIGURATION_CONSTANTS.IS_KEYCLOAK_CONFIGURED, false);

            httpAgent = new HTTPAgent(context, allSharedPreferences, dristhiConfiguration);
            httpAgent.setConnectTimeout(60000);
            httpAgent.setReadTimeout(60000);

            CoreLibrary.init(openSrpContext, new TestSyncConfiguration(), 1588062490000l);
        }
    }

    @After
    public void tearDown() throws Exception {
        if (bufferedInputStream != null)
            bufferedInputStream.close();
        if (inputStream != null)
            inputStream.close();


    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchFailsGivenWrongUrl() {
        Response<String> resp = httpAgent.fetch("wrong.url");
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testFetchPassesGivenCorrectUrl() {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            Response<String> resp = httpAgent.fetch("https://google.com");
            Assert.assertEquals(ResponseStatus.success, resp.status());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostFailsGivenWrongUrl() {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.post("wrong.url", jObject.toString());
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testPostPassesGivenCorrectUrl() {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            HashMap<String, String> map = new HashMap<>();
            map.put("title", "OpenSRP Testing Tuesdays");
            JSONObject jObject = new JSONObject(map);
            Response<String> resp = httpAgent.post("http://www.mocky.io/v2/5e54d9333100006300eb33a8", jObject.toString());
            Assert.assertEquals(ResponseStatus.success, resp.status());
        }
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentials() {
        try (MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");
            LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("http://www.mocky.io/v2/5e54de89310000d559eb33d9", "", "".toCharArray());
            Assert.assertEquals(LoginResponse.SUCCESS.message(), resp.message());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUrlCanBeAccessWithGivenCredentialsGivenWrongUrl() {
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("wrong.url", "", "".toCharArray());
        Assert.assertEquals(LoginResponse.MALFORMED_URL.message(), resp.message());
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsGivenEmptyResp() {
        try (MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");
            LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("http://mockbin.org/bin/e42f7256-18b2-40b9-a20c-40fdc564d06f", "", "".toCharArray());
            Assert.assertEquals(LoginResponse.SUCCESS_WITH_EMPTY_RESPONSE.message(), resp.message());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testfetchWithCredentialsFailsGivenWrongUrl() {
        Response<String> resp = httpAgent.fetchWithCredentials("wrong.url", SAMPLE_TEST_TOKEN);
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testfetchWithCredentialsPassesGivenCorrectUrl() {
        Response<String> resp = httpAgent.fetchWithCredentials("https://google.com", SAMPLE_TEST_TOKEN);
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHttpImagePostGivenWrongUrl() {
        String resp = httpAgent.httpImagePost("wrong.url", profileImage);
        Assert.assertEquals("", resp);
    }

    @Test
    public void testHttpImagePostTimeout() {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ProfileImage profileImage2 = new ProfileImage();
            profileImage2.setFilepath("test");

            String resp = httpAgent.httpImagePost("http://www.mocky.io/v2/5e54de89310000d559eb33d9?mocky-delay=60000ms", profileImage2);
            Assert.assertEquals("", resp);
        }
    }

    @Test
    public void testPostWithJsonResponse() throws IOException, URISyntaxException {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            HashMap<String, String> map = new HashMap<>();
            map.put("title", "OpenSRP Testing Tuesdays");
            JSONObject jObject = new JSONObject(map);

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);

            Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
            Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
            Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();
            Mockito.doNothing().when(httpURLConnection).disconnect();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).generatePostRequest(TEST_IMAGE_UPLOAD_ENDPOINT, jObject.toString());
            Mockito.doReturn("1").when(httpURLConnection).getHeaderField(AllConstants.SyncProgressConstants.TOTAL_RECORDS);

            Response<String> resp = httpAgentSpy.postWithJsonResponse(TEST_IMAGE_UPLOAD_ENDPOINT, jObject.toString());

            Assert.assertNotNull(resp);
            Assert.assertEquals(ResponseStatus.success, resp.status());
            Mockito.verify(allSharedPreferences).updateLastAuthenticationHttpStatus(HttpURLConnection.HTTP_OK);
            Mockito.verify(httpURLConnection).disconnect();
        }
    }

    @Test
    public void testOauth2authenticateCreatesUrlConnectionWithCorrectParametersForThePasswordGrantType() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

        AccountResponse accountResponse;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();

            accountResponse = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);

            Assert.assertNotNull(accountResponse);
            Assert.assertEquals(200, accountResponse.getStatus());
            Assert.assertEquals("1r9A8zi5E3r@Zz", accountResponse.getAccessToken());
            Assert.assertEquals("bearer", accountResponse.getTokenType());
            Assert.assertEquals("text_token", accountResponse.getRefreshToken());
            Assert.assertEquals(Integer.valueOf("3600"), accountResponse.getExpiresIn());
            Assert.assertEquals(Integer.valueOf("36000"), accountResponse.getRefreshExpiresIn());
            Assert.assertEquals("read write trust", accountResponse.getScope());

            Mockito.verify(httpURLConnection).setConnectTimeout(60000);
            Mockito.verify(httpURLConnection).setReadTimeout(60000);

            String requestParams = "&username=" + TEST_USERNAME + "&password=" + String.valueOf(TEST_PASSWORD) + "&grant_type=" + AccountHelper.OAUTH.GRANT_TYPE.PASSWORD;
            ArgumentCaptor<Integer> paramLengthCaptor = ArgumentCaptor.forClass(Integer.class);
            Mockito.verify(httpURLConnection).setFixedLengthStreamingMode(paramLengthCaptor.capture());
            Assert.assertEquals((Integer) requestParams.getBytes(CharEncoding.UTF_8).length, paramLengthCaptor.getValue());

            Mockito.verify(httpURLConnection).setDoOutput(true);
            Mockito.verify(httpURLConnection).setInstanceFollowRedirects(false);
            Mockito.verify(httpURLConnection).setRequestMethod("POST");
            Mockito.verify(httpURLConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            Mockito.verify(httpURLConnection).setRequestProperty("charset", "utf-8");
            Mockito.verify(httpURLConnection).setRequestProperty(ArgumentMatchers.eq("Content-Length"), ArgumentMatchers.anyString());
            Mockito.verify(httpURLConnection).setUseCaches(false);
            final String base64Auth = BaseEncoding.base64().encode((TestSyncConfiguration.OAUTH_CLIENT_ID + ":" + TestSyncConfiguration.OAUTH_CLIENT_SECRET).getBytes(CharEncoding.UTF_8));
            Mockito.verify(httpURLConnection).setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BASIC + " " + base64Auth);
            Mockito.verify(httpURLConnection).setInstanceFollowRedirects(false);

        }

    }

    @Test
    public void testOauth2authenticateCreatesUrlConnectionWithCorrectParametersForTheRefreshTokenGrantType() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(ArgumentMatchers.anyString());

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

        Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
        Mockito.doReturn(TEST_TOKEN_ENDPOINT).when(sharedPreferences).getString(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL, "");

        AccountResponse accountResponse;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
            accountResponse = httpAgentSpy.oauth2authenticateRefreshToken(SAMPLE_REFRESH_TOKEN);
        }

        Assert.assertNotNull(accountResponse);
        Assert.assertEquals(200, accountResponse.getStatus());
        Assert.assertEquals("1r9A8zi5E3r@Zz", accountResponse.getAccessToken());
        Assert.assertEquals("bearer", accountResponse.getTokenType());
        Assert.assertEquals("text_token", accountResponse.getRefreshToken());
        Assert.assertEquals(Integer.valueOf("3600"), accountResponse.getExpiresIn());
        Assert.assertEquals(Integer.valueOf("36000"), accountResponse.getRefreshExpiresIn());
        Assert.assertEquals("read write trust", accountResponse.getScope());

        Mockito.verify(httpURLConnection).setConnectTimeout(60000);
        Mockito.verify(httpURLConnection).setReadTimeout(60000);

        String requestParams = "&refresh_token=" + SAMPLE_REFRESH_TOKEN + "&grant_type=" + AccountHelper.OAUTH.GRANT_TYPE.REFRESH_TOKEN;
        ArgumentCaptor<Integer> paramLengthCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(httpURLConnection).setFixedLengthStreamingMode(paramLengthCaptor.capture());
        Assert.assertEquals((Integer) requestParams.getBytes(CharEncoding.UTF_8).length, paramLengthCaptor.getValue());

        Mockito.verify(httpURLConnection).setDoOutput(true);
        Mockito.verify(httpURLConnection).setInstanceFollowRedirects(false);
        Mockito.verify(httpURLConnection).setRequestMethod("POST");
        Mockito.verify(httpURLConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        Mockito.verify(httpURLConnection).setRequestProperty("charset", "utf-8");
        Mockito.verify(httpURLConnection).setRequestProperty(ArgumentMatchers.eq("Content-Length"), ArgumentMatchers.anyString());
        Mockito.verify(httpURLConnection).setUseCaches(false);
        final String base64Auth = BaseEncoding.base64().encode((TestSyncConfiguration.OAUTH_CLIENT_ID + ":" + TestSyncConfiguration.OAUTH_CLIENT_SECRET).getBytes(CharEncoding.UTF_8));
        Mockito.verify(httpURLConnection).setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BASIC + " " + base64Auth);
        Mockito.verify(httpURLConnection).setInstanceFollowRedirects(false);
    }

    @Test
    public void testOauth2authenticateReturnsCorrectResponseForBadRequest() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_BAD_REQUEST).when(httpURLConnection).getResponseCode();

        AccountResponse accountResponse;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();

            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn(TOKEN_BAD_REQUEST_SERVER_RESPONSE);

            accountResponse = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);

        }

        Assert.assertNotNull(accountResponse);
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, accountResponse.getStatus());
        Assert.assertNotNull(accountResponse.getAccountError());
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, accountResponse.getAccountError().getStatusCode());
        Assert.assertEquals("Code not valid", accountResponse.getAccountError().getErrorDescription());
        Assert.assertEquals("invalid_grant", accountResponse.getAccountError().getError());

    }

    @Test
    public void testOauth2authenticateReturnsCorrectAccountErrorResponseForMalformedURL() throws Exception {
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

            Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
            Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();

            Mockito.doThrow(new MalformedURLException()).when(httpURLConnection).getResponseCode();

            AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
            Assert.assertNotNull(response);
            Assert.assertNotNull(response.getAccountError());
            Assert.assertEquals(0, response.getAccountError().getStatusCode());
            Assert.assertEquals(LoginResponse.MALFORMED_URL.name(), response.getAccountError().getError());
        }

    }

    @Test
    public void testOauth2authenticateReturnsCorrectAccountErrorResponseForSocketTimeout() throws Exception {
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

            Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
            Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();

            Mockito.doThrow(new SocketTimeoutException()).when(httpURLConnection).getResponseCode();

            AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
            Assert.assertNotNull(response);
            Assert.assertNotNull(response.getAccountError());
            Assert.assertEquals(0, response.getAccountError().getStatusCode());
            Assert.assertEquals(LoginResponse.TIMEOUT.name(), response.getAccountError().getError());
        }

    }

    @Test
    public void testOauth2authenticateReturnsCorrectAccountErrorResponseForIOException() throws Exception {
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

            Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
            Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();

            Mockito.doThrow(new IOException()).when(httpURLConnection).getResponseCode();

            AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
            Assert.assertNotNull(response);
            Assert.assertNotNull(response.getAccountError());
            Assert.assertEquals(0, response.getAccountError().getStatusCode());
            Assert.assertEquals(LoginResponse.NO_INTERNET_CONNECTIVITY.name(), response.getAccountError().getError());
        }
    }

    @Test
    public void testOauth2authenticateReturnsNonNullAccountErrorResponseForRandomException() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();

        Mockito.doReturn(HttpURLConnection.HTTP_INTERNAL_ERROR).when(httpURLConnection).getResponseCode();
        AccountResponse response;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_ID).when(syncConfiguration).getOauthClientId();
            Mockito.doReturn(TestSyncConfiguration.OAUTH_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn(TOKEN_INTERNAL_SERVER_RESPONSE);
            response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
        }
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getAccountError());
        Assert.assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, response.getAccountError().getStatusCode());
        Assert.assertNotNull(response.getAccountError().getError());
        Assert.assertEquals("Oops, something went wrong", response.getAccountError().getErrorDescription());
        Assert.assertEquals("internal server error", response.getAccountError().getError());

    }

    @Test
    public void testFetchOAuthConfigurationProcessesConfigurationResponseCorrectly() throws Exception {
        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(KEYClOAK_CONFIGURATION_ENDPOINT);

        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();
        AccountConfiguration accountConfiguration;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class)) {
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(OAUTH_CONFIGURATION_SERVER_RESPONSE);

            accountConfiguration = httpAgentSpy.fetchOAuthConfiguration();
        }

        Assert.assertNotNull(accountConfiguration);
        Assert.assertEquals("https://my-server.com/oauth/auth", accountConfiguration.getAuthorizationEndpoint());
        Assert.assertEquals("https://my-server.com/oauth/issuer", accountConfiguration.getIssuerEndpoint());
        Assert.assertEquals(TEST_TOKEN_ENDPOINT, accountConfiguration.getTokenEndpoint());

        List<String> grantTypes = accountConfiguration.getGrantTypesSupported();
        Assert.assertNotNull(grantTypes);
        Assert.assertEquals("authorization code", grantTypes.get(0));
        Assert.assertEquals("implicit", grantTypes.get(1));
        Assert.assertEquals("password", grantTypes.get(2));
    }

    @Test
    public void testFetchInvalidatesCacheIfUnauthorizedAndReturnsCorrectResponse() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(SECURE_RESOURCE_ENDPOINT);
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();
        Response<String> response;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn(FETCH_DATA_REQUEST_SERVER_RESPONSE);

            response = httpAgentSpy.fetch(SECURE_RESOURCE_ENDPOINT);

            ioUtilsMockedStatic.verify(() -> AccountHelper.invalidateAuthToken(accountAuthenticatorXml.getAccountType(), SAMPLE_TEST_TOKEN));
        }
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseStatus.failure, response.status());
        Assert.assertEquals(FETCH_DATA_REQUEST_SERVER_RESPONSE, response.payload());


    }

    @Test
    public void testPostInvokesInvalidateCacheIfUnauthorizedOnFirstAttempt() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(SECURE_RESOURCE_ENDPOINT);
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class);
             MockedStatic<AccountHelper> accountHelperMockedStatic = Mockito.mockStatic(AccountHelper.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            accountHelperMockedStatic.when(() -> AccountHelper.getCachedOAuthToken(TEST_USERNAME, accountAuthenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER)).thenReturn(SAMPLE_TEST_TOKEN);

            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn(FETCH_DATA_REQUEST_SERVER_RESPONSE);

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).generatePostRequest(SECURE_RESOURCE_ENDPOINT, SAMPLE_POST_REQUEST_PAYLOAD);

            httpAgentSpy.post(SECURE_RESOURCE_ENDPOINT, SAMPLE_POST_REQUEST_PAYLOAD);

            Mockito.verify(httpAgentSpy).invalidateExpiredCachedAccessToken();
        }

    }

    @Test
    public void testFetchWithCredentialsInvokesInvalidateCacheIfUnauthorizedOnFirstAttempt() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(SECURE_RESOURCE_ENDPOINT);
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);

            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn(FETCH_DATA_REQUEST_SERVER_RESPONSE);

            Response<String> response = httpAgentSpy.fetchWithCredentials(SECURE_RESOURCE_ENDPOINT, SAMPLE_TEST_TOKEN);

            ioUtilsMockedStatic.verify(() -> AccountHelper.invalidateAuthToken(accountAuthenticatorXml.getAccountType(), SAMPLE_TEST_TOKEN));

            Assert.assertNotNull(response);
        }

    }

    @Test
    public void testOauth2authenticateRefreshTokenInvokesOauth2authenticateCoreWithCorrectParams() throws Exception {

        Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
        Mockito.doReturn(TEST_TOKEN_ENDPOINT).when(sharedPreferences).getString(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL, "");

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        AccountResponse accountResponse = Mockito.mock(AccountResponse.class);

        Mockito.doReturn(accountResponse).when(httpAgentSpy).oauth2authenticateCore(ArgumentMatchers.any(StringBuffer.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        ArgumentCaptor<StringBuffer> requestParamStringBuilder = ArgumentCaptor.forClass(StringBuffer.class);
        ArgumentCaptor<String> grantType = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> tokenEndPoint = ArgumentCaptor.forClass(String.class);

        httpAgentSpy.oauth2authenticateRefreshToken(SAMPLE_REFRESH_TOKEN);

        Mockito.verify(httpAgentSpy).oauth2authenticateCore(requestParamStringBuilder.capture(), grantType.capture(), tokenEndPoint.capture());

        String capturedRefreshTokenRequestValue = requestParamStringBuilder.getValue().toString();
        String capturedGrantTypeValue = grantType.getValue();
        String capturedTokenEndpointValue = tokenEndPoint.getValue();

        Assert.assertEquals("&refresh_token=" + SAMPLE_REFRESH_TOKEN, capturedRefreshTokenRequestValue);

        Assert.assertEquals(AccountHelper.OAUTH.GRANT_TYPE.REFRESH_TOKEN, capturedGrantTypeValue);
        Assert.assertEquals(TEST_TOKEN_ENDPOINT, capturedTokenEndpointValue);


    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponse() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doReturn(inputStream).when(httpsURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class)) {
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(LoginResponseTestData.USER_DETAILS_REQUEST_SERVER_RESPONSE);
            loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);
        }

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Login successful.", loginResponse.message());
        Assert.assertNotNull(loginResponse.payload());

        Assert.assertNotNull(loginResponse.payload().user);
        Assert.assertEquals("demo", loginResponse.payload().user.getUsername());
        Assert.assertEquals("Demo User", loginResponse.payload().user.getPreferredName());
        Assert.assertEquals("93c6526-6667-3333-a611112-f3b309999999", loginResponse.payload().user.getBaseEntityId());

        Assert.assertNotNull(loginResponse.payload().time);
        Assert.assertEquals("2020-06-02 08:21:40", loginResponse.payload().time.getTime());
        Assert.assertEquals("Africa/Nairobi", loginResponse.payload().time.getTimeZone());

        Assert.assertNotNull(loginResponse.payload().locations);
        Assert.assertNotNull(loginResponse.payload().locations.getLocationsHierarchy());

        Assert.assertNotNull(loginResponse.payload().jurisdictions);
        Assert.assertEquals(1, loginResponse.payload().jurisdictions.size());
        Assert.assertNotNull("Health Team Kasarani", loginResponse.payload().jurisdictions.get(0));

        Assert.assertNotNull(loginResponse.payload().team);
        Assert.assertEquals("93c6526-6667-3333-a611112-f3b309999999", loginResponse.payload().team.identifier);
        Assert.assertEquals("93c6526-6667-3333-a611112-f3b309999999", loginResponse.payload().team.uuid);

        Assert.assertEquals("SUCCESS", loginResponse.name());

        ArgumentCaptor<String> headerKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValue = ArgumentCaptor.forClass(String.class);

        Mockito.verify(httpsURLConnection).setRequestProperty(headerKey.capture(), headerValue.capture());
        String capturedKey = headerKey.getValue();
        String capturedValue = headerValue.getValue();

        Assert.assertEquals(capturedKey, AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION);
        Assert.assertEquals(capturedValue, AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BEARER + " " + SAMPLE_TEST_TOKEN);

    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForUnauthorizedRequests() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Please check the credentials", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("UNAUTHORIZED", loginResponse.name());

    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForRandomServerError() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doReturn(errorStream).when(httpsURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_INTERNAL_ERROR).when(httpsURLConnection).getResponseCode();
        LoginResponse loginResponse;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class)) {

            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn("<html><p><b>message</b> Oops, something went wrong </u></p></html>");
            loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);
        }


        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Oops, something went wrong", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("CUSTOM_SERVER_RESPONSE", loginResponse.name());

    }


    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForMalformedURLRequests() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doThrow(new MalformedURLException()).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Incorrect url", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("MALFORMED_URL", loginResponse.name());

    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForConnectionTimedOutRequests() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doThrow(new SocketTimeoutException()).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("The server could not be reached. Try again", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("TIMEOUT", loginResponse.name());

    }


    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForRequestsWithoutNetworkConnectivity() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doThrow(new IOException()).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("No internet connection. Please ensure data connectivity", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("NO_INTERNET_CONNECTIVITY", loginResponse.name());

    }


    @Test
    public void testVerifyAuthorizationLegacyReturnsTrueForAuthorizedResponse() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            URL url = Mockito.mock(URL.class);
            Assert.assertNotNull(url);

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(KEYClOAK_CONFIGURATION_ENDPOINT);

            Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

            boolean isVerified = httpAgentSpy.verifyAuthorizationLegacy();
            Assert.assertTrue(isVerified);
        }

    }

    @Test
    public void testVerifyAuthorizationLegacyReturnsFalseForUnauthorizedResponse() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            URL url = Mockito.mock(URL.class);
            Assert.assertNotNull(url);

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection("https://my-server.com/user-details?anm-id=" + TEST_USERNAME);

            Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

            boolean isVerified = httpAgentSpy.verifyAuthorizationLegacy();
            Assert.assertFalse(isVerified);
        }

    }


    @Test
    public void testVerifyAuthorizationReturnsTrueForAuthorizedResponse() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();

        Mockito.doReturn(TEST_USER_INFO_ENDPOINT).when(sharedPreferences).getString(AccountHelper.CONFIGURATION_CONSTANTS.USERINFO_ENDPOINT_URL, "");

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_USER_INFO_ENDPOINT);

        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        boolean isVerified;
        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            ioUtilsMockedStatic.when(() -> IOUtils.toString(inputStream)).thenReturn(ACCOUNT_INFO_REQUEST_SERVER_RESPONSE);
            isVerified = httpAgentSpy.verifyAuthorization();
        }


        Assert.assertTrue(isVerified);

    }

    @Test
    public void testVerifyAuthorizationReturnsFalseForUnauthorizedResponse() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            URL url = Mockito.mock(URL.class);
            Assert.assertNotNull(url);

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();

            Mockito.doReturn(TEST_USER_INFO_ENDPOINT).when(sharedPreferences).getString(AccountHelper.CONFIGURATION_CONSTANTS.USERINFO_ENDPOINT_URL, "");

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_USER_INFO_ENDPOINT);

            Mockito.doReturn(false).when(httpAgentSpy).verifyAuthorizationLegacy();

            Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

            boolean isVerified = httpAgentSpy.verifyAuthorization();
            Assert.assertFalse(isVerified);
        }

    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsUnauthorizedResponse() throws Exception {
        try (MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

            Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

            LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
            Assert.assertNotNull(response);
            Assert.assertNotNull(response.message());
            Assert.assertNull(response.payload());
            Assert.assertEquals("Please check the credentials", response.message());
        }
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsErrorResponseForMalformedURL() throws Exception {

        try (MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

            Mockito.doThrow(new MalformedURLException()).when(httpURLConnection).getResponseCode();

            LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
            Assert.assertNotNull(response);
            Assert.assertNull(response.payload());
            Assert.assertNotNull(response.message());
            Assert.assertEquals(LoginResponse.MALFORMED_URL.name(), response.name());
        }
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsCorrectErrorResponseForSocketTimeout() throws Exception {

        try (MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

            Mockito.doThrow(new SocketTimeoutException()).when(httpURLConnection).getResponseCode();


            LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
            Assert.assertNotNull(response);
            Assert.assertNull(response.payload());
            Assert.assertNotNull(response.message());
            Assert.assertEquals(LoginResponse.TIMEOUT.name(), response.name());
        }
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsCorrectErrorResponseForIOException() throws Exception {

        try (MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

            Mockito.doThrow(new IOException()).when(httpURLConnection).getResponseCode();


            LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
            Assert.assertNotNull(response);
            Assert.assertNull(response.payload());
            Assert.assertNotNull(response.message());
            Assert.assertEquals(LoginResponse.NO_INTERNET_CONNECTIVITY.name(), response.name());
        }
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsCorrectResponseForRandomServerError() throws Exception {

        URL url = Mockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doReturn(errorStream).when(httpsURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_INTERNAL_ERROR).when(httpsURLConnection).getResponseCode();
        LoginResponse loginResponse;

        try (MockedStatic<IOUtils> ioUtilsMockedStatic = Mockito.mockStatic(IOUtils.class); MockedStatic<Base64> base64MockedStatic = Mockito.mockStatic(Base64.class)) {
            base64MockedStatic.when(() -> Base64.encodeToString(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(Base64.NO_WRAP))).thenReturn("");
            ioUtilsMockedStatic.when(() -> IOUtils.toString(errorStream)).thenReturn("<html><p><b>message</b> Oops, something went wrong </u></p></html>");
            loginResponse = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
        }

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Oops, something went wrong", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("CUSTOM_SERVER_RESPONSE", loginResponse.name());

    }

    @Test
    public void testDownloadFromUrl() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);
        String imageContentType = "image/png";
        String ext = "png";
        DownloadStatus downloadStatus;

        try (MockedStatic<MimeTypeMap> mimeTypeMapMockedStatic = Mockito.mockStatic(MimeTypeMap.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            MimeTypeMap mockMimeTypeMap = Mockito.mock(MimeTypeMap.class);
            mimeTypeMapMockedStatic.when(() -> MimeTypeMap.getSingleton()).thenReturn(mockMimeTypeMap);
            Mockito.doReturn(ext).when(mockMimeTypeMap).getExtensionFromMimeType(imageContentType);


            Mockito.doReturn(dirFile).when(httpAgentSpy).getSDCardDownloadPath();
            Mockito.doReturn(file).when(httpAgentSpy).getFile(TEST_FILE_NAME + "." + ext, dirFile);
            Mockito.doReturn(false).when(dirFile).exists();
            Mockito.doReturn(true).when(dirFile).mkdirs();

            Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_IMAGE_DOWNLOAD_ENDPOINT);
            Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpsURLConnection).getResponseCode();
            Mockito.doReturn(inputStream).when(httpsURLConnection).getInputStream();
            Mockito.doReturn(imageContentType).when(httpsURLConnection).getContentType();
            Mockito.doReturn(bufferedInputStream).when(httpAgentSpy).getBufferedInputStream(inputStream);
            Mockito.doReturn(fileOutputStream).when(httpAgentSpy).getFileOutputStream(file);

            downloadStatus = httpAgentSpy.downloadFromUrl(TEST_IMAGE_DOWNLOAD_ENDPOINT, TEST_FILE_NAME);
        }

        Assert.assertNotNull(downloadStatus);
        Assert.assertEquals("Download successful", downloadStatus.displayValue());

        Mockito.verify(fileOutputStream).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.eq(0), ArgumentMatchers.anyInt());
        Mockito.verify(fileOutputStream).flush();
        Mockito.verify(fileOutputStream).close();

    }


    @Test
    public void testDownloadFromUrlReturnsCorrectResponseIfNothingDownloaded() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(dirFile).when(httpAgentSpy).getSDCardDownloadPath();
            Mockito.doReturn(file).when(httpAgentSpy).getFile(TEST_FILE_NAME, dirFile);
            Mockito.doReturn(false).when(dirFile).exists();
            Mockito.doReturn(true).when(dirFile).mkdirs();

            Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_IMAGE_DOWNLOAD_ENDPOINT);
            Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpsURLConnection).getResponseCode();

            Mockito.doReturn(inputStream).when(httpsURLConnection).getInputStream();

            DownloadStatus downloadStatus = httpAgentSpy.downloadFromUrl(TEST_IMAGE_DOWNLOAD_ENDPOINT, TEST_FILE_NAME);
            Assert.assertNotNull(downloadStatus);
            Assert.assertEquals("Nothing downloaded.", downloadStatus.displayValue());

        }
    }

    @Test
    public void testDownloadFromUrlReturnsCorrectResponseIfIOExceptionThrown() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(dirFile).when(httpAgentSpy).getSDCardDownloadPath();
            Mockito.doReturn(file).when(httpAgentSpy).getFile(TEST_FILE_NAME, dirFile);
            Mockito.doReturn(false).when(dirFile).exists();
            Mockito.doReturn(true).when(dirFile).mkdirs();

            Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_IMAGE_DOWNLOAD_ENDPOINT);
            Mockito.doThrow(new IOException()).when(httpsURLConnection).getResponseCode();

            DownloadStatus downloadStatus = httpAgentSpy.downloadFromUrl(TEST_IMAGE_DOWNLOAD_ENDPOINT, TEST_FILE_NAME);
            Assert.assertNotNull(downloadStatus);
            Assert.assertEquals("Download failed.", downloadStatus.displayValue());
        }
    }

    @Test
    public void testDownloadFromUrlReturnsCorrectResponseIfConnectionStatusIsNOT200() throws Exception {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(dirFile).when(httpAgentSpy).getSDCardDownloadPath();
            Mockito.doReturn(file).when(httpAgentSpy).getFile(TEST_FILE_NAME, dirFile);
            Mockito.doReturn(true).when(dirFile).exists();

            Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_IMAGE_DOWNLOAD_ENDPOINT);
            Mockito.doReturn(HttpURLConnection.HTTP_NOT_FOUND).when(httpsURLConnection).getResponseCode();

            DownloadStatus downloadStatus = httpAgentSpy.downloadFromUrl(TEST_IMAGE_DOWNLOAD_ENDPOINT, TEST_FILE_NAME);
            Assert.assertNotNull(downloadStatus);
            Assert.assertEquals("Download failed.", downloadStatus.displayValue());
        }

    }

    @Test
    public void testHttpImagePostConfiguresConnectionRequestCorrectly() throws Exception {

        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);

            HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

            Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_IMAGE_UPLOAD_ENDPOINT);
            Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
            Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();
            Mockito.doReturn(file).when(httpAgentSpy).getDownloadFolder(TEST_IMAGE_FILE_PATH);
            Mockito.doReturn(fileInputStream).when(httpAgentSpy).getFileInputStream(file);
            Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
            Mockito.doReturn(-1).when(fileInputStream).read(ArgumentMatchers.any(byte[].class));

            Mockito.doReturn(printWriter).when(httpAgentSpy).getPrintWriter(outputStream);
            Mockito.doReturn("myFileName").when(file).getName();
            Mockito.doReturn(printWriter).when(printWriter).append(ArgumentMatchers.any(CharSequence.class));
            Mockito.doReturn(SAMPLE_TEST_TOKEN).when(httpAgentSpy).getBearerToken();

            ProfileImage profileImage = new ProfileImage();
            profileImage.setFilepath(TEST_IMAGE_FILE_PATH);
            profileImage.setAnmId(TEST_USERNAME);
            profileImage.setEntityID(TEST_BASE_ENTITY_ID);
            profileImage.setContenttype("png");
            profileImage.setFilecategory("coverpic");

            httpAgentSpy.httpImagePost(TEST_IMAGE_UPLOAD_ENDPOINT, profileImage);

            Mockito.verify(httpURLConnection).setDoOutput(true);
            Mockito.verify(httpURLConnection).setDoInput(true);
            Mockito.verify(httpURLConnection).setRequestMethod("POST");

            ArgumentCaptor<String> requestAttributeCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> requestValueCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(httpURLConnection, Mockito.times(2)).setRequestProperty(requestAttributeCaptor.capture(), requestValueCaptor.capture());
            List<String> requestAttributeCaptorValues = requestAttributeCaptor.getAllValues();
            List<String> requestValueCaptorValues = requestValueCaptor.getAllValues();

            Assert.assertEquals("Authorization", requestAttributeCaptorValues.get(0));
            Assert.assertEquals("Bearer " + SAMPLE_TEST_TOKEN, requestValueCaptorValues.get(0));

            Assert.assertEquals("Content-Type", requestAttributeCaptorValues.get(1));
            Assert.assertTrue(requestValueCaptorValues.get(1).startsWith("multipart/form-data;boundary="));

            Mockito.verify(httpURLConnection).setUseCaches(false);
            Mockito.verify(httpURLConnection).setChunkedStreamingMode(HTTPAgent.FILE_UPLOAD_CHUNK_SIZE_BYTES);

            //Attach Image
            Mockito.verify(httpAgentSpy).getDownloadFolder(TEST_IMAGE_FILE_PATH);

            ArgumentCaptor<CharSequence> printWriterCaptor = ArgumentCaptor.forClass(CharSequence.class);

            Mockito.verify(printWriter, Mockito.times(49)).append(printWriterCaptor.capture());

            List<CharSequence> printWriterAppendedValues = printWriterCaptor.getAllValues();

            Assert.assertTrue(printWriterAppendedValues.contains("Content-Disposition: form-data; name=\"file\"; filename=\"myFileName\""));
            Assert.assertTrue(printWriterAppendedValues.contains("Content-Disposition: form-data; name=\"anm-id\""));
            Assert.assertTrue(printWriterAppendedValues.contains("Content-Disposition: form-data; name=\"entity-id\""));
            Assert.assertTrue(printWriterAppendedValues.contains("Content-Disposition: form-data; name=\"file-category\""));
            Assert.assertTrue(printWriterAppendedValues.contains("Content-Disposition: form-data; name=\"content-type\""));
            Assert.assertTrue(printWriterAppendedValues.contains(profileImage.getAnmId()));
            Assert.assertTrue(printWriterAppendedValues.contains(profileImage.getEntityID()));
            Assert.assertTrue(printWriterAppendedValues.contains(profileImage.getFilecategory()));
            Assert.assertTrue(printWriterAppendedValues.contains(profileImage.getContenttype()));
            Assert.assertTrue(printWriterAppendedValues.contains("Content-Type: text/plain; charset=UTF-8"));

            Mockito.verify(printWriter, Mockito.times(7)).flush();
        }

    }

    @Test
    public void testOauth2authenticateEncodesPasswordCorrectly() {
        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        StringBuffer stringBuffer = ReflectionHelpers.callInstanceMethod(httpAgentSpy, "getRequestParams",
                ReflectionHelpers.ClassParameter.from(String.class, "testUser"),
                ReflectionHelpers.ClassParameter.from(char[].class, "abc123%^&.".toCharArray()));
        Assert.assertEquals("&username=testUser&password=abc123%25%5E%26.", stringBuffer.toString());
    }
}
