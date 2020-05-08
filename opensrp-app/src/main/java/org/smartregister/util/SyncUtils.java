package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Setting;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.BaseRepository;

import java.util.List;

import timber.log.Timber;

import static org.smartregister.AllConstants.ACCOUNT_DISABLED;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION;
import static org.smartregister.AllConstants.FORCED_LOGOUT.MIN_ALLOWED_APP_VERSION_SETTING;
import static org.smartregister.AllConstants.JSON.KEY;
import static org.smartregister.AllConstants.JSON.VALUE;
import static org.smartregister.AllConstants.SETTINGS;
import static org.smartregister.util.Utils.getVersionCode;

/**
 * Created by samuelgithengi on 1/28/19.
 */
public class SyncUtils {

    private org.smartregister.Context opensrpContent = CoreLibrary.getInstance().context();

    private Context context;

    public SyncUtils(Context context) {
        this.context = context;
    }

    public boolean verifyAuthorization() {
        return CoreLibrary.getInstance().context().getHttpAgent().verifyAuthorization();
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

        // see if setting was synced
        AllSettings settingsRepository = opensrpContent.allSettings();
        Setting rawMinAllowedAppVersionSetting = settingsRepository.getSetting(MIN_ALLOWED_APP_VERSION_SETTING);
        if (rawMinAllowedAppVersionSetting == null) {
            return isAppVersionAllowed;
        }

        // if min version is already extracted
        Setting extractedMinAllowedAppVersionSetting = settingsRepository.getSetting(MIN_ALLOWED_APP_VERSION);
        if (extractedMinAllowedAppVersionSetting != null
                && isNewerSetting(extractedMinAllowedAppVersionSetting, rawMinAllowedAppVersionSetting)) {
            return !isOutdatedVersion(Long.valueOf(extractedMinAllowedAppVersionSetting.getValue()));
        }

        // else, attempt to extract it
        int minAllowedAppVersion = extractMinAllowedAppVersion(rawMinAllowedAppVersionSetting.getValue());
        if (isOutdatedVersion(minAllowedAppVersion)) {
            isAppVersionAllowed = false;
        }

        // update settings repository with the extracted version of the min allowed setting
        extractedMinAllowedAppVersionSetting = new Setting();
        extractedMinAllowedAppVersionSetting.setValue(String.valueOf(minAllowedAppVersion));
        extractedMinAllowedAppVersionSetting.setKey(MIN_ALLOWED_APP_VERSION);
        extractedMinAllowedAppVersionSetting.setSyncStatus(BaseRepository.TYPE_Synced);
        extractedMinAllowedAppVersionSetting.setIdentifier(MIN_ALLOWED_APP_VERSION);
        extractedMinAllowedAppVersionSetting.setVersion(getIncrementedServerVersion(rawMinAllowedAppVersionSetting));
        settingsRepository.putSetting(extractedMinAllowedAppVersionSetting);

        return isAppVersionAllowed;
    }

    /**
     * Returns true if {@param setting1} was updated more recently when compared to {@param setting2},
     * and false otherwise
     *
     * @param setting1
     * @param setting2
     * @return
     */
    private boolean isNewerSetting(Setting setting1, Setting setting2) {
        return Long.valueOf(setting1.getVersion()) > Long.valueOf(setting2.getVersion());
    }

    private synchronized String getIncrementedServerVersion(Setting setting) {
        if (setting == null || StringUtils.isBlank(setting.getVersion())) {
            return null;
        }
        return String.valueOf(Long.valueOf(setting.getVersion()) + 1);
    }

    private boolean isOutdatedVersion(long minAllowedAppVersion) throws PackageManager.NameNotFoundException {
        return getVersionCode(context) < minAllowedAppVersion;
    }

    private int extractMinAllowedAppVersion(String setting) {
        int minAllowedAppVersion = 0;
        try {
            JSONArray settings = new JSONObject(setting).optJSONArray(SETTINGS);
            for (int i = 0; i < settings.length(); i++) {
                JSONObject currSettingObj = settings.optJSONObject(i);
                String currKey = currSettingObj.optString(KEY);
                if (MIN_ALLOWED_APP_VERSION_SETTING.equals(currKey)) {
                    minAllowedAppVersion = currSettingObj.optInt(VALUE, minAllowedAppVersion);
                    break;
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
