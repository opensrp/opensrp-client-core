package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NoHttpResponseException;
import org.json.JSONArray;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.SyncIntentServiceHelper;

import java.util.List;

import static org.smartregister.AllConstants.CAMPAIGNS;

public class SyncTaskIntentService extends IntentService {
    public static final String TASK_URL = "/rest/task/sync";
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    private static final String TAG = SyncTaskIntentService.class.getCanonicalName();
    private TaskRepository taskRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    private LocationRepository locationRepository;

    public SyncTaskIntentService() {
        super("SyncTaskIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncTasks();
    }

    protected void syncTasks() {
        String campaigns = allSharedPreferences.getPreference(CAMPAIGNS);
        String groups = TextUtils.join(",", locationRepository.getAllLocationIds());

        long serverVersion = 0;
        try {
            serverVersion = Long.parseLong(allSharedPreferences.getPreference(TASK_LAST_SYNC_DATE));
        } catch (NumberFormatException e) {
        }
        try {
            JSONArray tasksResponse = fetchTasks(campaigns, groups, serverVersion);
            List<Task> tasks = SyncIntentServiceHelper.parseTasksFromServer(tasksResponse, Task.class);

            for (Task task : tasks) {
                try {
                    taskRepository.addOrUpdate(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allSharedPreferences.savePreference(geMaxServerVersion(tasks, serverVersion), TASK_LAST_SYNC_DATE);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private JSONArray fetchTasks(String campaign, String group, Long serverVersion) throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + TASK_URL + "?campaign=" + campaign + "&group=" + group + "&serverVersion=" + serverVersion;

        if (httpAgent == null) {
            sendBroadcast(SyncIntentServiceHelper.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(TASK_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            sendBroadcast(SyncIntentServiceHelper.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(TASK_URL + " not returned data");
        }

        return new JSONArray((String) resp.payload());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    public String geMaxServerVersion(List<Task> tasks, long currentServerVersion) {
        long maxServerVersion =currentServerVersion;

        for (Task task : tasks) {
            long serverVersion = task.getServerVersion();
            if (serverVersion > currentServerVersion) {
                maxServerVersion = serverVersion;
            }
        }

        return String.valueOf(maxServerVersion);
    }
}