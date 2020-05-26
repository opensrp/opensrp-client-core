package org.smartregister.sync.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.Response;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.intent.SettingsSyncIntentService;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SyncSettingsServiceHelper {

    private HTTPAgent httpAgent;
    private String baseUrl;
    private AllSharedPreferences sharedPreferences;

    public SyncSettingsServiceHelper(String baseUrl, HTTPAgent httpAgent) {

        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        sharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    }


    public int processIntent() throws JSONException {

        try {
            JSONObject response = pushSettingsToServer();
            if (response != null && response.has(AllConstants.INTENT_KEY.VALIDATED_RECORDS)) {
                JSONArray records = response.getJSONArray(AllConstants.INTENT_KEY.VALIDATED_RECORDS);
                Setting setting;
                for (int i = 0; i < records.length(); i++) {
                    setting = CoreLibrary.getInstance().context().allSettings().getSetting(records.getString(0));
                    setting.setSyncStatus(SyncStatus.SYNCED.name());
                    CoreLibrary.getInstance().context().allSettings().putSetting(setting);
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
        }

        AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
        String authToken = AccountHelper.getCachedOAuthToken(authenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER);
        JSONArray settings = pullSettingsFromServer(CoreLibrary.getInstance().getSyncConfiguration().getSettingsSyncFilterValue(), authToken);

        if (settings != null && settings.length() > 0) {
            settings = ServerSettingsHelper.saveSetting(settings);
        }

        return settings == null ? 0 : settings.length();
    }

    /**
     * @param syncFilterValue the actual value to use with the filter param
     */
    public JSONArray pullSettingsFromServer(String syncFilterValue, String accessToken) throws JSONException {

        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + SettingsSyncIntentService.SETTINGS_URL + "?" +
                CoreLibrary.getInstance().getSyncConfiguration().getSettingsSyncFilterParam().value() + "=" +
                syncFilterValue + "&serverVersion=" +
                sharedPreferences.fetchLastSettingsSyncTimeStamp();

        Timber.i("URL: %s", url);

        if (httpAgent == null) {
            Timber.e("%s http agent is null", url);
            return null;
        }

        Response resp = httpAgent.fetchWithCredentials(url, accessToken);

        if (resp.isFailure()) {
            Timber.e(" %s  not returned data ", url);
            return null;
        }
        return new JSONArray((String) resp.payload());
    }

    private JSONObject pushSettingsToServer() throws JSONException {

        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = MessageFormat.format("{0}/{1}", baseUrl, SettingsSyncIntentService.SETTINGS_URL);
        Timber.i("URL: %s", url);

        if (httpAgent == null) {
            Timber.e("%s http agent is null", url);
            return null;
        }

        JSONObject payload = createSettingsConfigurationPayload();

        if (payload.getJSONArray(AllConstants.INTENT_KEY.SETTING_CONFIGURATIONS).length() > 0) {

            Response<String> response = httpAgent.postWithJsonResponse(url, payload.toString());

            return new JSONObject(response.payload());

        } else return null;
    }

    private JSONObject createSettingsConfigurationPayload() throws JSONException {


        JSONObject siteSettingsPayload = new JSONObject();

        JSONArray settingsArray = new JSONArray();

        List<Setting> unsyncedSettings = CoreLibrary.getInstance().context().allSettings().getUnsyncedSettings();


        for (int i = 0; i < unsyncedSettings.size(); i++) {

            SyncableJSONObject settingsWrapper = new SyncableJSONObject(unsyncedSettings.get(i).getValue());

            settingsArray.put(settingsWrapper);
        }


        siteSettingsPayload.put(AllConstants.INTENT_KEY.SETTING_CONFIGURATIONS, settingsArray);

        return siteSettingsPayload;
    }
}

