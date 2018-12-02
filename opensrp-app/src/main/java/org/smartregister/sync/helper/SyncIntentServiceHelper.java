package org.smartregister.sync.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import java.util.ArrayList;
import java.util.List;

public class SyncIntentServiceHelper {
    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .serializeNulls().create();
    public static <T> List<T> parseTasksFromServer(JSONArray campaignsFromServer, Class<T> objectClass) {
        List<T> locations = new ArrayList<>();
        for (int i = 0; i < campaignsFromServer.length(); i++) {
            try {
                locations.add(gson.fromJson(campaignsFromServer.getJSONObject(i).toString(), objectClass));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return locations;
    }
}
