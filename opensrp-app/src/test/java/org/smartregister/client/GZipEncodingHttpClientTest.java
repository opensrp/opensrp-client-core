package org.smartregister.client;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

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
        HttpResponse httpResponse = new HttpResponse() {
            @Override
            public StatusLine getStatusLine() {
                return new StatusLine() {
                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        return null;
                    }

                    @Override
                    public int getStatusCode() {
                        return org.apache.http.HttpStatus.SC_OK;
                    }

                    @Override
                    public String getReasonPhrase() {
                        return null;
                    }
                };
            }

            @Override
            public void setStatusLine(StatusLine statusLine) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {

            }

            @Override
            public void setStatusCode(int i) throws IllegalStateException {

            }

            @Override
            public void setReasonPhrase(String s) throws IllegalStateException {

            }

            @Override
            public HttpEntity getEntity() {
                return new HttpEntity() {
                    @Override
                    public boolean isRepeatable() {
                        return false;
                    }

                    @Override
                    public boolean isChunked() {
                        return false;
                    }

                    @Override
                    public long getContentLength() {
                        return 0;
                    }

                    @Override
                    public Header getContentType() {
                        return null;
                    }

                    @Override
                    public Header getContentEncoding() {
                        return null;
                    }

                    @Override
                    public InputStream getContent() throws IOException, IllegalStateException {
                        return new InputStream() {
                            @Override
                            public int read() throws IOException {
                                return 0;
                            }
                        };
                    }

                    @Override
                    public void writeTo(OutputStream outputStream) throws IOException {

                    }

                    @Override
                    public boolean isStreaming() {
                        return false;
                    }

                    @Override
                    public void consumeContent() throws IOException {

                    }
                };
            }

            @Override
            public void setEntity(HttpEntity httpEntity) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public void setLocale(Locale locale) {

            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public boolean containsHeader(String s) {
                return false;
            }

            @Override
            public Header[] getHeaders(String s) {
                return new Header[0];
            }

            @Override
            public Header getFirstHeader(String s) {
                return null;
            }

            @Override
            public Header getLastHeader(String s) {
                return null;
            }

            @Override
            public Header[] getAllHeaders() {
                return new Header[0];
            }

            @Override
            public void addHeader(Header header) {

            }

            @Override
            public void addHeader(String s, String s1) {

            }

            @Override
            public void setHeader(Header header) {

            }

            @Override
            public void setHeader(String s, String s1) {

            }

            @Override
            public void setHeaders(Header[] headers) {

            }

            @Override
            public void removeHeader(Header header) {

            }

            @Override
            public void removeHeaders(String s) {

            }

            @Override
            public HeaderIterator headerIterator() {
                return null;
            }

            @Override
            public HeaderIterator headerIterator(String s) {
                return null;
            }

            @Override
            public HttpParams getParams() {
                return null;
            }

            @Override
            public void setParams(HttpParams httpParams) {

            }
        };

        Mockito.when(defaultHttpClient.execute(Mockito.any(HttpGet.class))).thenReturn(httpResponse);
        Assert.assertNotNull(gZipEncodingHttpClient.fetchContent(httpGet));

        Assert.assertNotNull(gZipEncodingHttpClient.execute(Mockito.mock(HttpGet.class)));
        Assert.assertNull(gZipEncodingHttpClient.getCredentialsProvider());
        Mockito.when(defaultHttpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
        Assert.assertNotNull(gZipEncodingHttpClient.postContent(Mockito.mock(HttpPost.class)));
    }
}
