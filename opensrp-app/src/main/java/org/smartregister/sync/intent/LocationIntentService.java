package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.Response;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;
import org.smartregister.util.Utils;

import java.util.List;

import static org.smartregister.AllConstants.OPERATIONAL_AREAS;

public class LocationIntentService extends IntentService {
    public static final String LOCATION_STRUCTURE_URL = "/rest/location/sync";
    public static final String STRUCTURES_LAST_SYNC_DATE = "STRUCTURES_LAST_SYNC_DATE";
    public static final String LOCATION_LAST_SYNC_DATE = "LOCATION_LAST_SYNC_DATE";
    private static final String TAG = LocationIntentService.class.getCanonicalName();
    private LocationRepository locationRepository;
    private StructureRepository structureRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();

    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncLocationsStructures(true);
        syncLocationsStructures(false);
    }


    protected void syncLocationsStructures(boolean is_jurisdiction) {
        long serverVersion = 0;
        String currentServerVersion = allSharedPreferences.getPreference(is_jurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE);
        try {
            serverVersion = Long.parseLong(currentServerVersion);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        try {
            List<String> parentIds = locationRepository.getAllLocationIds();
            String featureResponse = fetchLocationsOrStructures(is_jurisdiction, serverVersion, TextUtils.join(",", parentIds));
            List<Location> locations = gson.fromJson(featureResponse, new TypeToken<List<Location>>() {
            }.getType());

            for (Location location : locations) {
                try {
                    if (is_jurisdiction)
                        locationRepository.addOrUpdate(location);
                    else
                        structureRepository.addOrUpdate(location);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String maxServerVersion = geMaxServerVersion(locations, serverVersion);
            allSharedPreferences.savePreference(maxServerVersion, is_jurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String makeURL(boolean is_jurisdiction, long serverVersion, String parentId) {
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        if (is_jurisdiction) {
            String preferenceLocationNames = allSharedPreferences.getPreference(OPERATIONAL_AREAS);
            return baseUrl + LOCATION_STRUCTURE_URL + "?is_jurisdiction=" + is_jurisdiction + "&location_names=" + preferenceLocationNames + "&serverVersion=" + serverVersion;
        }
        return baseUrl + LOCATION_STRUCTURE_URL + "?parent_id=" + parentId + "&is_jurisdiction=" + is_jurisdiction + "&serverVersion=" + serverVersion;

    }

    private String fetchLocationsOrStructures(boolean is_jurisdiction, Long serverVersion, String parentId) throws Exception {

        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            sendBroadcast(Utils.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(LOCATION_STRUCTURE_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(makeURL(is_jurisdiction, serverVersion, parentId));
        if (resp.isFailure()) {
            sendBroadcast(Utils.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(LOCATION_STRUCTURE_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    public String geMaxServerVersion(List<Location> locations, long serverVersionParam) {
        long currentServerVersion = serverVersionParam;
        for (Location location : locations) {
            long serverVersion = location.getServerVersion();
            if (serverVersion > currentServerVersion) {
                currentServerVersion = serverVersion;
            }
        }
        return String.valueOf(currentServerVersion);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        structureRepository = CoreLibrary.getInstance().context().getStructureRepository();
        return super.onStartCommand(intent, flags, startId);
    }

}