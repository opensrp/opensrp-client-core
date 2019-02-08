package org.smartregister.sync.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Characteristic;
import org.smartregister.domain.Setting;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class CharacteristicsHelper {

    private static final String TAG = CharacteristicsHelper.class.getCanonicalName();

    public static List<Characteristic> characteristics;

    private static final Type CHARACTERISTIC_TYPE = new TypeToken<List<Characteristic>>() {
    }.getType();

    public CharacteristicsHelper(String key) {
        initializeCharacteristics(key);

    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public static List<Characteristic> fetchCharacteristicsByTypeKey(String typeKey) {
        try {

            Gson gson = new Gson();

            Setting characteristic = CoreLibrary.getInstance().context().allSettings().getSetting(typeKey);

            String jsonstring = characteristic.getValue();

            return gson.fromJson(jsonstring, CHARACTERISTIC_TYPE); // contains the whole reviews list

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return new ArrayList<>();
        }
    }

    public static JSONArray saveSetting(JSONArray serverSettings) throws JSONException {
        for (int i = 0; i < serverSettings.length(); i++) {

            JSONObject jsonObject = serverSettings.getJSONObject(i);
            Setting characteristic = new Setting();
            characteristic.setKey(jsonObject.getString(AllConstants.IDENTIFIER));
            characteristic.setValue(jsonObject.getString(AllConstants.SETTINGS));
            characteristic.setSyncStatus(SyncStatus.SYNCED.name());

            CoreLibrary.getInstance().context().allSettings().put(AllSharedPreferences.LAST_SETTINGS_SYNC_TIMESTAMP, characteristic.getVersion());
            CoreLibrary.getInstance().context().allSettings().putSetting(characteristic);

        }

        return serverSettings;
    }


    public static void updateLastSettingServerSyncTimetamp() {

        CoreLibrary.getInstance().context().allSharedPreferences().updateLastSettingsSyncTimeStamp(Calendar.getInstance().getTimeInMillis());
    }

    private static void initializeCharacteristics(String key) {

        characteristics = CharacteristicsHelper.fetchCharacteristicsByTypeKey(key);
    }
}
