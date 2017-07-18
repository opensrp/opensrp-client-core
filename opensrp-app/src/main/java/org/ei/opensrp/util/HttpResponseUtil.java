package org.ei.opensrp.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.util.Log.logError;

public class HttpResponseUtil {
    public static InputStream getResponseStream(HttpResponse response) throws IOException {
        if (response.getEntity() != null && response.getEntity().getContentEncoding() != null) {
            HeaderElement[] codecs = response.getEntity().getContentEncoding().getElements();
            for (HeaderElement codec : codecs) {
                if (codec.getName().equalsIgnoreCase("gzip")) {
                    return new GZIPInputStream(response.getEntity().getContent());
                }
            }
        }
        return response.getEntity().getContent();
    }

    public static String getResponseBody(HttpResponse response) {
        try {
            InputStream responseStream = getResponseStream(response);
            return IOUtils.toString(responseStream);
        } catch (IOException e) {
            logError(format("Cannot read anm location from response due to exception: {0}. Stack trace: {1}",
                    e.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return "";
    }
}
