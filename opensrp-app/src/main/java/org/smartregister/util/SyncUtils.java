package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpStatus;
import org.smartregister.CoreLibrary;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by samuelgithengi on 1/28/19.
 */
public class SyncUtils {

    private static final String TAG = "SyncUtils";

    private org.smartregister.Context opensrpContent = CoreLibrary.getInstance().context();

    private Context context;

    public SyncUtils(Context context) {
        this.context = context;
    }

    public boolean verifyAuthorization() {
        String baseUrl = opensrpContent.configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }
        final String username = opensrpContent.allSharedPreferences().fetchRegisteredANM();
        final String password = opensrpContent.allSettings().fetchANMPassword();
        baseUrl = baseUrl + "/user-details?anm-id=" + username;
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            final String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
            int statusCode = urlConnection.getResponseCode();
            urlConnection.disconnect();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                android.util.Log.i(TAG, "User not authorized. User access was revoked, will log off user");
                return false;
            } else if (statusCode != HttpStatus.SC_OK) {
                android.util.Log.w(TAG, "Error occurred verifying authorization, User will not be logged off");
            } else {
                android.util.Log.i(TAG, "User is Authorized");
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return true;
    }

    public void logoutUser() {
        //force remote login
        opensrpContent.userService().forceRemoteLogin();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());
        //retrieve the main/launcher activity defined in the manifest and open it
        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
        if (activities.size() == 1) {
            intent = intent.setClassName(context.getPackageName(), activities.get(0).activityInfo.name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
        //logoff opensrp session
        opensrpContent.userService().logoutSession();
    }

}
