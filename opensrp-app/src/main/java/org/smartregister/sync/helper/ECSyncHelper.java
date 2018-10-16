package org.smartregister.sync.helper;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.util.Utils.getPreference;

/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class ECSyncHelper {

    private final EventClientRepository eventClientRepository;
    private final Context context;

    private static ECSyncHelper instance;

    public static ECSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncHelper(context, CoreLibrary.getInstance().context().getEventClientRepository());
        }
        return instance;
    }

    private ECSyncHelper(Context context, EventClientRepository eventClientRepository) {
        this.context = context;
        this.eventClientRepository = eventClientRepository;
    }

    public boolean saveAllClientsAndEvents(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return false;
            }

            JSONArray events = jsonObject.has("events") ? jsonObject.getJSONArray("events") : new JSONArray();
            JSONArray clients = jsonObject.has("clients") ? jsonObject.getJSONArray("clients") : new JSONArray();

            batchSave(events, clients);


            return true;
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
            return false;
        }
    }

    public List<EventClient> allEventClients(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }

    public long getLastSyncTimeStamp() {
        return Long.parseLong(getPreference(context, AllConstants.LAST_SYNC_TIMESTAMP, "0"));
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        Utils.writePreference(context, AllConstants.LAST_SYNC_TIMESTAMP, lastSyncTimeStamp + "");
    }

    public void updateLastCheckTimeStamp(long lastCheckTimeStamp) {
        Utils.writePreference(context, AllConstants.LAST_CHECK_TIMESTAMP, lastCheckTimeStamp + "");
    }

    public long getLastCheckTimeStamp() {
        return Long.parseLong(getPreference(context, AllConstants.LAST_CHECK_TIMESTAMP, "0"));
    }

    public void batchSave(JSONArray events, JSONArray clients) {
        eventClientRepository.batchInsertClients(clients);
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }

}
