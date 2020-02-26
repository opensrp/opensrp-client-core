package org.smartregister.service;

import android.content.Context;
import android.util.Base64;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;

import java.net.HttpURLConnection;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Base64.class)
public class HTTPAgentTest {
    @Mock
    private Context context;

    @Mock
    private AllSettings allSettings;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private DristhiConfiguration dristhiConfiguration;

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
        Response<String> resp = httpAgent.fetch("http://google.com");
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
    public void testfetchWithCredentialsFailsGivenWrongUrl(){
        Response<String> resp = httpAgent.fetchWithCredentials("wrong.url", "", "");
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testfetchWithCredentialsPassesGivenCorrectUrl(){
        PowerMockito.mockStatic(Base64.class);
        Response<String> resp = httpAgent.fetchWithCredentials("http://google.com", "", "");
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }
}
