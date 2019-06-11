package org.smartregister.sync.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.Response;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;
import org.smartregister.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

import static org.smartregister.AllConstants.OPERATIONAL_AREAS;

public class LocationServiceHelper {

    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    protected final Context context;
    private LocationRepository locationRepository;
    private StructureRepository structureRepository;

    public static final String LOCATION_STRUCTURE_URL = "/rest/location/sync";
    public static final String CREATE_STRUCTURE_URL = "/rest/location/add";
    public static final String STRUCTURES_LAST_SYNC_DATE = "STRUCTURES_LAST_SYNC_DATE";
    public static final String LOCATION_LAST_SYNC_DATE = "LOCATION_LAST_SYNC_DATE";

    private static Gson locationGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    protected static LocationServiceHelper instance;

    public static LocationServiceHelper getInstance() {
        if (instance == null) {
            instance = new LocationServiceHelper(CoreLibrary.getInstance().context().getLocationRepository(), CoreLibrary.getInstance().context().getStructureRepository());
        }
        return instance;
    }

    public LocationServiceHelper(LocationRepository locationRepository, StructureRepository structureRepository) {
        this.context = CoreLibrary.getInstance().context().applicationContext();
        this.locationRepository = locationRepository;
        this.structureRepository = structureRepository;
    }

    protected List<Location> syncLocationsStructures(boolean isJurisdiction) {
        long serverVersion = 0;
        String currentServerVersion = allSharedPreferences.getPreference(isJurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE);
        try {
            serverVersion = (StringUtils.isEmpty(currentServerVersion) ? 0 : Long.parseLong(currentServerVersion));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (serverVersion > 0) serverVersion += 1;
        try {
            List<String> parentIds = locationRepository.getAllLocationIds();
            String featureResponse = fetchLocationsOrStructures(isJurisdiction, serverVersion, TextUtils.join(",", parentIds));
            List<Location> locations = locationGson.fromJson(featureResponse, new TypeToken<List<Location>>() {
            }.getType());

            for (Location location : locations) {
                try {
                    location.setSyncStatus(BaseRepository.TYPE_Synced);
                    if (isJurisdiction)
                        locationRepository.addOrUpdate(location);
                    else {
                        structureRepository.addOrUpdate(location);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!Utils.isEmptyCollection(locations)) {
                String maxServerVersion = getMaxServerVersion(locations);
                String updateKey = isJurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE;
                allSharedPreferences.savePreference(updateKey, maxServerVersion);
            }
            return locations;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String makeURL(boolean isJurisdiction, long serverVersion, String parentId) {
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        if (isJurisdiction) {
            String preferenceLocationNames = null;
            try {
                preferenceLocationNames = URLEncoder.encode(allSharedPreferences.getPreference(OPERATIONAL_AREAS), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
            return baseUrl + LOCATION_STRUCTURE_URL + "?is_jurisdiction=" + isJurisdiction + "&location_names=" + preferenceLocationNames + "&serverVersion=" + serverVersion;
        }
        return baseUrl + LOCATION_STRUCTURE_URL + "?parent_id=" + parentId + "&isJurisdiction=" + isJurisdiction + "&serverVersion=" + serverVersion;

    }

    private String fetchLocationsOrStructures(boolean isJurisdiction, Long serverVersion, String parentId) throws Exception {

        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(LOCATION_STRUCTURE_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(makeURL(isJurisdiction, serverVersion, parentId));
        if (resp.isFailure()) {
            throw new NoHttpResponseException(LOCATION_STRUCTURE_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    private String getMaxServerVersion(List<Location> locations) {
        long maxServerVersion = 0;
        for (Location location : locations) {
            long serverVersion = location.getServerVersion();
            if (serverVersion > maxServerVersion) {
                maxServerVersion = serverVersion;
            }
        }
        return String.valueOf(maxServerVersion);
    }

    public List<Location> fetchLocationsStructures() {
        syncLocationsStructures(true);
        List<Location> locations = syncLocationsStructures(false);
        syncCreatedStructureToServer();
        return locations;
    }

    public void syncCreatedStructureToServer() {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        List<Location> locations = structureRepository.getAllUnsynchedCreatedStructures();
        if (!locations.isEmpty()) {
            String jsonPayload = locationGson.toJson(locations);
            String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
            Response<String> response = httpAgent.post(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            CREATE_STRUCTURE_URL),
                    jsonPayload);
            if (response.isFailure()) {
                Log.e(getClass().getName(), "Failed to create new locations on server.");
                return;
            }

            for (Location location : locations) {
                structureRepository.markStructuresAsSynced(location.getId());
            }
        }
    }

}

