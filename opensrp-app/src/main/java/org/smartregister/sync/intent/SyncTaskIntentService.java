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
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.List;

import static org.smartregister.AllConstants.CAMPAIGNS;

public class SyncTaskIntentService extends IntentService {
    public static final String TASK_URL = "/rest/task/sync";
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    private static final String TAG = SyncIntentService.class.getCanonicalName();
    private TaskRepository taskRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    private LocationRepository locationRepository;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm")).create();

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
            Log.e(TAG, e.getMessage(), e);
        }
        try {
            String tasksResponse = fetchTasks(campaigns, groups, serverVersion);
            List<Task> tasks = gson.fromJson(tasksResponse, new TypeToken<List<Task>>() {
            }.getType());
            for (Task task : tasks) {
                try {
                    taskRepository.addOrUpdate(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allSharedPreferences.savePreference(geMaxServerVersion(tasks, serverVersion), TASK_LAST_SYNC_DATE);
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
            sendBroadcast(completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(TASK_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            sendBroadcast(completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(TASK_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    public String geMaxServerVersion(List<Task> tasks, long currentServerVersion) {
        long maxServerVersion = currentServerVersion;

        for (Task task : tasks) {
            long serverVersion = task.getServerVersion();
            if (serverVersion > currentServerVersion) {
                maxServerVersion = serverVersion;
            }
        }

        return String.valueOf(maxServerVersion);
    }

    private Intent completeSync(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);
        return intent;
    }
}