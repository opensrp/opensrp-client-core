package org.smartregister.service;

import android.content.Context;
import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;

import java.net.HttpURLConnection;


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
    private HttpURLConnection httpURLConnection;

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
    @Ignore
    public void testFetchPassesGivenCorrectUrl(){
        Response<String> resp = httpAgent.fetch("http://google.com");
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }
}
