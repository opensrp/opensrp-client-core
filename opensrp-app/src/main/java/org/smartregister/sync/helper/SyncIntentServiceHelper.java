package org.smartregister.sync.helper;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import java.util.ArrayList;
import java.util.List;

public class SyncIntentServiceHelper {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .serializeNulls().create();

    public static <T> List<T> parseTasksFromServer(JSONArray responsesFromServer, Class<T> objectClass) {
        List<T> responseObjects = new ArrayList<>();
        for (int i = 0; i < responsesFromServer.length(); i++) {
            try {
                responseObjects.add(gson.fromJson(responsesFromServer.getJSONObject(i).toString(), objectClass));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return responseObjects;
    }

    public static Intent completeSync(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);
        return intent;
    }

}
