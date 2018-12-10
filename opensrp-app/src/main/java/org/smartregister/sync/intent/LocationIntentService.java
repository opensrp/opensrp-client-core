package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.NoHttpResponseException;
import org.json.JSONArray;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Location;
import org.smartregister.domain.Response;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.SyncIntentServiceHelper;

import java.util.List;

import static org.smartregister.AllConstants.OPERATIONAL_AREAS;

public class LocationIntentService extends IntentService {
    public static final String LOCATION_STRUCTURE_URL = "/rest/location/sync";
    public static final String STRUCTURES_LAST_SYNC_DATE = "STRUCTURES_LAST_SYNC_DATE";
    private static final String TAG = LocationIntentService.class.getCanonicalName();
    private LocationRepository locationRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncLocationsStructures(true);
        syncLocationsStructures(false);
    }


    protected void syncLocationsStructures(boolean is_jurisdiction) {
        long serverVersion = Long.parseLong(allSharedPreferences.getPreference(STRUCTURES_LAST_SYNC_DATE));
        try {
            JSONArray tasksResponse = fetchLocationsOrStructures(is_jurisdiction, serverVersion);
            List<Location> locations = SyncIntentServiceHelper.parseTasksFromServer(tasksResponse, Location.class);
            for (Location location : locations) {
                try {
                    locationRepository.addOrUpdate(location);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allSharedPreferences.savePreference(String.valueOf(geMaxServerVersion(locations, serverVersion)), STRUCTURES_LAST_SYNC_DATE);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    private String makeURL(boolean is_jurisdiction, long serverVersion) {
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        if (is_jurisdiction) {
            String preferenceLocationNames = allSharedPreferences.getPreference(OPERATIONAL_AREAS);
            return baseUrl + LOCATION_STRUCTURE_URL + "?is_jurisdiction=" + is_jurisdiction + "&location_names=" + preferenceLocationNames;
        }
        String parent_ids = android.text.TextUtils.join(",", locationRepository.getAllLocationIds());
        return baseUrl + LOCATION_STRUCTURE_URL + "?parent_id=" + parent_ids + "&is_jurisdiction=" + is_jurisdiction + "&serverVersion=" + serverVersion;

    }

    private JSONArray fetchLocationsOrStructures(boolean is_jurisdiction, Long serverVersion) throws Exception {

        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            sendBroadcast(SyncIntentServiceHelper.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(LOCATION_STRUCTURE_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(makeURL(is_jurisdiction, serverVersion));
        if (resp.isFailure()) {
            sendBroadcast(SyncIntentServiceHelper.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(LOCATION_STRUCTURE_URL + " not returned data");
        }

        return new JSONArray((String) resp.payload());
    }

    public long geMaxServerVersion(List<Location> locations, long serverVersionParam) {
        long currentServerVersion = serverVersionParam;
        for (Location location : locations) {
            long serverVersion = location.getServerVersion();
            if (serverVersion > currentServerVersion) {
                currentServerVersion = serverVersion;
            }
        }

        return currentServerVersion;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        return super.onStartCommand(intent, flags, startId);
    }

}