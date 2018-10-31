package org.smartregister.sync.helper;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class ECSyncHelper {

    protected final EventClientRepository eventClientRepository;
    protected final Context context;

    protected AllSharedPreferences allSharedPreferences;

    protected static ECSyncHelper instance;

    public static ECSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncHelper(context, CoreLibrary.getInstance().context().getEventClientRepository());
            instance.allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        }
        return instance;
    }

    @VisibleForTesting
    protected ECSyncHelper(Context context, EventClientRepository eventClientRepository) {
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
        return allSharedPreferences.fetchLastSyncDate(0);
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        allSharedPreferences.saveLastSyncDate(lastSyncTimeStamp);
    }

    public void updateLastCheckTimeStamp(long lastCheckTimeStamp) {
        allSharedPreferences.updateLastCheckTimeStamp(lastCheckTimeStamp);
    }

    public long getLastCheckTimeStamp() {
        return allSharedPreferences.fetchLastCheckTimeStamp();
    }

    public void batchSave(JSONArray events, JSONArray clients) {
        eventClientRepository.batchInsertClients(clients);
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }

}
