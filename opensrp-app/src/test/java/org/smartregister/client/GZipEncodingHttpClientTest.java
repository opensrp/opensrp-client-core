package org.smartregister.client;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class GZipEncodingHttpClientTest extends BaseUnitTest {

    private GZipEncodingHttpClient gZipEncodingHttpClient;

    @Mock
    private DefaultHttpClient defaultHttpClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gZipEncodingHttpClient = new GZipEncodingHttpClient(defaultHttpClient);
    }

    @Test
    public void assertFetchContentNotNull() throws Exception {
        HttpGet httpGet = Mockito.mock(HttpGet.class);
        HttpResponse httpResponse = new MockHttpResponse();

        Mockito.when(defaultHttpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        Assert.assertNotNull(gZipEncodingHttpClient.fetchContent(httpGet));

        Assert.assertNotNull(gZipEncodingHttpClient.execute(Mockito.mock(HttpGet.class)));
        Assert.assertNull(gZipEncodingHttpClient.getCredentialsProvider());
        Mockito.when(defaultHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
        Assert.assertNotNull(gZipEncodingHttpClient.postContent(Mockito.mock(HttpPost.class)));
    }
}
