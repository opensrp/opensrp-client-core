package org.smartregister.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.smartregister.util.HttpResponseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

public class GZipEncodingHttpClientImpl extends GZipEncodingHttpClient {

    public GZipEncodingHttpClientImpl(DefaultHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public String retrieveStringResponse(HttpResponse httpResponse) throws IOException, ParseException {
        if (httpResponse == null) {
            return null;
        }
        return "Response";
    }
}
