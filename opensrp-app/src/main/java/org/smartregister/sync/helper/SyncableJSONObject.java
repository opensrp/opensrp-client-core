package org.smartregister.sync.helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ndegwamartin on 20/09/2018.
 */
public class SyncableJSONObject extends JSONObject {

    public SyncableJSONObject() {

        try {

            AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

            String providerId = allSharedPreferences.fetchRegisteredANM();

            put("providerId", providerId);
            put("locationId", allSharedPreferences.fetchDefaultLocalityId(providerId));
            put("teamId", allSharedPreferences.fetchDefaultTeam(providerId));
            put("teamId", allSharedPreferences.fetchDefaultTeamId(providerId));
            put("dateCreated", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                    Locale.getDefault()).format(Calendar.getInstance().getTime()));

        } catch (JSONException e) {

        }
    }
}
