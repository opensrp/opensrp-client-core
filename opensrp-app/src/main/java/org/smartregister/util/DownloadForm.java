package org.smartregister.util;

import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.FormPathService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dimas Ciputra on 3/21/15.
 */
public class DownloadForm {

    /**
     * @author  Rodgers Andati
     * @since   2019-04-25
     * This method downloads images given url. Migration from the old method that used httpclient
     * @param downloadURL This is the url of the image
     * @param fileName This is how the image should be name after it has been downloaded.
     * @param username This is the username used to authenticate when accessing the url endpoint.
     * @param password This is the password used to authenticate when accessing the url endpoint.
     * @return Response<DownloadStatus> This returns whether the download succeeded or failed.
     */
    public static Response<DownloadStatus> downloadFromURL(String downloadURL, String fileName, String username, String password) {
        HttpURLConnection httpUrlConnection;
        try {
            File dir = new File(FormPathService.sdcardPathDownload);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();
            Log.d("DownloadFormService", "download begin");
            Log.d("DownloadFormService", "download url: " + downloadURL);
            Log.d("DownloadFormService", "download file name: " + fileName);

            /* Open connection to URL */
            URL url = new URL(downloadURL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();

            httpUrlConnection.setRequestProperty("username", username);
            httpUrlConnection.setRequestProperty("password", password);

            httpUrlConnection.connect();

            int status = httpUrlConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {

                InputStream inputStream = httpUrlConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                long fileLength = bufferedInputStream.available();
                if (fileLength == 0) {
                    return new Response<DownloadStatus>(ResponseStatus.success,
                            DownloadStatus.nothingDownloaded);
                }
                Log.d("DownloadFormService", "file length : " + fileLength);

                ByteArrayBuffer baf = new ByteArrayBuffer(9999);
                int current = 0;
                while ((current = bufferedInputStream.read()) != -1) {
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
                httpUrlConnection.disconnect();

            } else {
                Log.d("RESPONSE", "Server returned non-OK status: " + status);
                return new Response<DownloadStatus>(ResponseStatus.failure, DownloadStatus.failedDownloaded);
            }

        } catch (IOException e) {
            Log.d("DownloadFormService", "download error : " + e);
            return new Response<DownloadStatus>(ResponseStatus.success, DownloadStatus.failedDownloaded);
        }

        return new Response<DownloadStatus>(ResponseStatus.success, DownloadStatus.downloaded);
    }

}