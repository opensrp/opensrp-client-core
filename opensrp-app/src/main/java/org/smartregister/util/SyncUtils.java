package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Base64;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.BuildConfig;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.AllConstants.ACCOUNT_DISABLED;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION;
import static org.smartregister.AllConstants.JSON.KEY;
import static org.smartregister.AllConstants.JSON.VALUE;
import static org.smartregister.AllConstants.SETTINGS;
import static org.smartregister.util.Utils.getVersionCode;

/**
 * Created by samuelgithengi on 1/28/19.
 */
public class SyncUtils {


    private static final String DETAILS_URL = "/user-details?anm-id=";

    private org.smartregister.Context opensrpContent = CoreLibrary.getInstance().context();

    private Context context;

    public SyncUtils(Context context) {
        this.context = context;
    }

    public boolean verifyAuthorization() {
        String baseUrl = opensrpContent.configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        final String username = opensrpContent.allSharedPreferences().fetchRegisteredANM();
        final String password = opensrpContent.allSettings().fetchANMPassword();
        baseUrl = baseUrl + DETAILS_URL + username;
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            final String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
            int statusCode = urlConnection.getResponseCode();
            urlConnection.disconnect();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                Timber.i("User not authorized. User access was revoked, will log off user");
                return false;
            } else if (statusCode != HttpStatus.SC_OK) {
                Timber.w("Error occurred verifying authorization, User will not be logged off");
            } else {
                Timber.i("User is Authorized");
            }

        } catch (IOException e) {
            Timber.e(e);
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
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ACCOUNT_DISABLED, context.getString(R.string.account_disabled_logged_off));
            context.startActivity(intent);
        }
        //logoff opensrp session
        opensrpContent.userService().logoutSession();
    }

    public boolean isAppVersionAllowed() throws PackageManager.NameNotFoundException {
        boolean isAppVersionAllowed = true;

        AllSettings settingsRepository = opensrpContent.allSettings();
        Setting minAllowedAppVersionSetting = settingsRepository.getSetting(MIN_ALLOWED_APP_VERSION);
        if (minAllowedAppVersionSetting == null) {
            return isAppVersionAllowed;
        }

        int minAllowedAppVersion = extractMinAllowedAppVersion(minAllowedAppVersionSetting.getValue());
        if (getVersionCode(context) < minAllowedAppVersion) {
            isAppVersionAllowed = false;
        }

        return isAppVersionAllowed;
    }

    private int extractMinAllowedAppVersion(String setting) {
        int minAllowedAppVersion = 0;
        try {
            JSONArray settings = new JSONObject(setting).optJSONArray(SETTINGS);
            for (int i = 0; i < settings.length(); i++) {
                JSONObject currSettingObj = settings.optJSONObject(i);
                String currKey = currSettingObj.optString(KEY);
                if (MIN_ALLOWED_APP_VERSION.equals(currKey)) {
                    minAllowedAppVersion = currSettingObj.optInt(VALUE, minAllowedAppVersion);
                }
            }
        } catch (NumberFormatException e) {
            Timber.e(e, "Please ensure that the min app version is an integer");
        } catch (JSONException e) {
            Timber.e(e);
        }
        return minAllowedAppVersion;
    }
}
