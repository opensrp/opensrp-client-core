package org.smartregister.sync.helper;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;

import java.util.List;

import static org.smartregister.AllConstants.CAMPAIGNS;
import static org.smartregister.AllConstants.OPERATIONAL_AREAS;

public class LocationTaskServiceHelper {
    private static final String TAG = LocationTaskServiceHelper.class.getCanonicalName();

    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    protected final Context context;
    private TaskRepository taskRepository;
    private LocationRepository locationRepository;
    private StructureRepository structureRepository;
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    public static final String TASK_URL = "/rest/task/sync";

    public static final String LOCATION_STRUCTURE_URL = "/rest/location/sync";
    public static final String STRUCTURES_LAST_SYNC_DATE = "STRUCTURES_LAST_SYNC_DATE";
    public static final String LOCATION_LAST_SYNC_DATE = "LOCATION_LAST_SYNC_DATE";

    private static final Gson taskGson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm")).create();


    private static Gson locationGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    protected static LocationTaskServiceHelper instance;

    public static LocationTaskServiceHelper getInstance() {
        if (instance == null) {
            instance = new LocationTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository(), CoreLibrary.getInstance().context().getLocationRepository(), CoreLibrary.getInstance().context().getStructureRepository());
            instance.allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        }
        return instance;
    }

    @VisibleForTesting
    public LocationTaskServiceHelper(TaskRepository taskRepository, LocationRepository locationRepository, StructureRepository structureRepository) {
        this.context = CoreLibrary.getInstance().context().applicationContext();
        this.taskRepository = taskRepository;
        this.locationRepository = locationRepository;
        this.structureRepository = structureRepository;
    }

    public void syncTasks() {
        String campaigns = allSharedPreferences.getPreference(CAMPAIGNS);
        String groups = TextUtils.join(",", locationRepository.getAllLocationIds());

        long serverVersion = 0;
        try {
            serverVersion = Long.parseLong(allSharedPreferences.getPreference(TASK_LAST_SYNC_DATE));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        try {
            String tasksResponse = fetchTasks(campaigns, groups, serverVersion);
            List<Task> tasks = taskGson.fromJson(tasksResponse, new TypeToken<List<Task>>() {
            }.getType());
            for (Task task : tasks) {
                try {
                    taskRepository.addOrUpdate(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allSharedPreferences.savePreference(getTaskMaxServerVersion(tasks, serverVersion), TASK_LAST_SYNC_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchTasks(String campaign, String group, Long serverVersion) throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + TASK_URL + "?campaign=" + campaign + "&group=" + group + "&serverVersion=" + serverVersion;

        if (httpAgent == null) {
            throw new IllegalArgumentException(TASK_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            throw new NoHttpResponseException(TASK_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    private String getTaskMaxServerVersion(List<Task> tasks, long currentServerVersion) {
        long maxServerVersion = currentServerVersion;

        for (Task task : tasks) {
            long serverVersion = task.getServerVersion();
            if (serverVersion > currentServerVersion) {
                maxServerVersion = serverVersion;
            }
        }

        return String.valueOf(maxServerVersion);
    }


    protected void syncLocationsStructures(boolean is_jurisdiction) {
        long serverVersion = 0;
        String currentServerVersion = allSharedPreferences.getPreference(is_jurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE);
        try {
            serverVersion = (StringUtils.isEmpty(currentServerVersion)? 0: Long.parseLong(currentServerVersion));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            List<String> parentIds = locationRepository.getAllLocationIds();
            String featureResponse = fetchLocationsOrStructures(is_jurisdiction, serverVersion, TextUtils.join(",", parentIds));
            List<Location> locations = locationGson.fromJson(featureResponse, new TypeToken<List<Location>>() {
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
            String updateKey = is_jurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE;
            allSharedPreferences.savePreference(maxServerVersion, updateKey);

        } catch (Exception e) {
            e.printStackTrace();
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
            throw new IllegalArgumentException(LOCATION_STRUCTURE_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(makeURL(is_jurisdiction, serverVersion, parentId));
        if (resp.isFailure()) {
            throw new NoHttpResponseException(LOCATION_STRUCTURE_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    private String geMaxServerVersion(List<Location> locations, long serverVersionParam) {
        long currentServerVersion = serverVersionParam;
        for (Location location : locations) {
            long serverVersion = location.getServerVersion();
            if (serverVersion > currentServerVersion) {
                currentServerVersion = serverVersion;
            }
        }
        return String.valueOf(currentServerVersion);
    }

    public void fetchLocationsStructures() {
        syncLocationsStructures(true);
        syncLocationsStructures(false);
    }

}

