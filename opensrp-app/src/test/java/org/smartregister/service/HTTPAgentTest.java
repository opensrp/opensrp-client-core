package org.smartregister.service;

import android.content.Context;
import android.util.Base64;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Base64.class, File.class, FileInputStream.class})
public class HTTPAgentTest {
    @Mock
    private Context context;

    @Mock
    private AllSettings allSettings;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private DristhiConfiguration dristhiConfiguration;

    @Mock
    private ProfileImage profileImage;

    @Rule
    private TemporaryFolder folder = new TemporaryFolder();

    private HTTPAgent httpAgent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        httpAgent = new HTTPAgent(context, allSettings, allSharedPreferences, dristhiConfiguration);
    }

    @Test
    public void testFetchFailsGivenWrongUrl(){
        Response<String> resp = httpAgent.fetch("wrong.url");
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testFetchPassesGivenCorrectUrl(){
        PowerMockito.mockStatic(Base64.class);
        Response<String> resp = httpAgent.fetch("https://google.com");
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test
    public void testPostFailsGivenWrongUrl(){
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.post("wrong.url", jObject.toString());
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testPostPassesGivenCorrectUrl(){
        PowerMockito.mockStatic(Base64.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.post("http://www.mocky.io/v2/5e54d9333100006300eb33a8", jObject.toString());
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentials(){
        PowerMockito.mockStatic(Base64.class);
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("http://www.mocky.io/v2/5e54de89310000d559eb33d9", "", "");
        Assert.assertEquals(LoginResponse.SUCCESS.message(), resp.message());
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsGivenWrongUrl(){
        PowerMockito.mockStatic(Base64.class);
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("wrong.url", "", "");
        Assert.assertEquals(LoginResponse.MALFORMED_URL.message(), resp.message());
    }

    @Ignore
    @Test
    public void testUrlCanBeAccessWithGivenCredentialsGivenEmptyResp(){
        PowerMockito.mockStatic(Base64.class);
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("http://mockbin.org/bin/e42f7256-18b2-40b9-a20c-40fdc564d06f", "", "");
        Assert.assertEquals(LoginResponse.SUCCESS_WITH_EMPTY_RESPONSE.message(), resp.message());
    }

    @Test
    public void testfetchWithCredentialsFailsGivenWrongUrl(){
        Response<String> resp = httpAgent.fetchWithCredentials("wrong.url", "", "");
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testfetchWithCredentialsPassesGivenCorrectUrl(){
        PowerMockito.mockStatic(Base64.class);
        Response<String> resp = httpAgent.fetchWithCredentials("https://google.com", "", "");
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test
    public void testHttpImagePostGivenWrongUrl(){
        String resp = httpAgent.httpImagePost("wrong.url", profileImage);
        Assert.assertEquals("", resp);
    }

    @Test
    public void testHttpImagePostTimeout() {
        PowerMockito.mockStatic(Base64.class);
        PowerMockito.mockStatic(File.class);
        PowerMockito.mockStatic(FileInputStream.class);

        ProfileImage profileImage2 = new ProfileImage();
        profileImage2.setFilepath("test");

        String resp = httpAgent.httpImagePost("http://www.mocky.io/v2/5e54de89310000d559eb33d9?mocky-delay=60000ms", profileImage2);
        Assert.assertEquals("", resp);
    }

    @Test
    public void testPostWithJsonResponse(){
        PowerMockito.mockStatic(Base64.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.postWithJsonResponse("http://www.mocky.io/v2/5e54d9333100006300eb33a8", jObject.toString());
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }
}
