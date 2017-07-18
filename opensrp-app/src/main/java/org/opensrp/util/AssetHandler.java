package org.opensrp.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by koros on 3/24/16.
 */
public class AssetHandler {

    public static final String TAG = "AssetHandler";

    public static String readFileFromAssetsFolder(String fileName, Context context){
        String fileContents = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            android.util.Log.e(TAG, ex.toString(), ex);
            return null;
        }
        //Log.d("File", fileContents);
        return fileContents;
    }
}
