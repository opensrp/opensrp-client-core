package org.smartregister.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.ByteArrayBuffer;
import org.smartregister.client.GZipEncodingHttpClient;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.FormPathService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dimas Ciputra on 3/21/15.
 */
public class DownloadForm {

    public static Response<DownloadStatus> DownloadFromURL(String downloadURL, String fileName,
                                                           final GZipEncodingHttpClient
                                                                   httpClient) {

        try {

            File dir = new File(FormPathService.sdcardPathDownload);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();
            Log.d("DownloadFormService", "download begin");
            Log.d("DownloadFormService", "download url: " + downloadURL.toString());
            Log.d("DownloadFormService", "download file name: " + fileName);

            /* Open connection to URL */
            HttpResponse response = httpClient.execute(new HttpGet(downloadURL));

            /* expect HTTP 200 OK */
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.d("DownloadFormService", "Server returned HTTP " + statusCode);
                return new Response<DownloadStatus>(ResponseStatus.failure,
                        DownloadStatus.failedDownloaded);
            }

            HttpEntity entity = response.getEntity();

            /* Define InputStreams to read from the URLConnections */
            InputStream is = entity.getContent();
            BufferedInputStream bis = new BufferedInputStream(is);

            /* This will be for count download percentage */
            long fileLength = entity.getContentLength();

            if (fileLength == 0) {
                return new Response<DownloadStatus>(ResponseStatus.success,
                        DownloadStatus.nothingDownloaded);
            }

            Log.d("DownloadFormService", "file length : " + fileLength);

            /* Read bytes to the Buffer until there is nothing more to read */
            ByteArrayBuffer baf = new ByteArrayBuffer(9999);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            /* Convert the bytes to String */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();

            Log.d("DownloadFormService",
                    "download finished in " + ((System.currentTimeMillis() - startTime) / 1000)
                            + " sec");

        } catch (IOException e) {
            Log.d("DownloadFormService", "download error : " + e);
            return new Response<DownloadStatus>(ResponseStatus.success,
                    DownloadStatus.failedDownloaded);
        }

        return new Response<DownloadStatus>(ResponseStatus.success, DownloadStatus.downloaded);
    }

}