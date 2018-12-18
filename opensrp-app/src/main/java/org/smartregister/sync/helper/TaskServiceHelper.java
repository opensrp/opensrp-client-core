package org.smartregister.sync.helper;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.List;

import static org.smartregister.AllConstants.CAMPAIGNS;

public class TaskServiceHelper {
    private static final String TAG = TaskServiceHelper.class.getCanonicalName();

    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    protected final Context context;
    private TaskRepository taskRepository;
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    public static final String TASK_URL = "/rest/task/sync";

    private static final Gson taskGson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm")).create();

    protected static TaskServiceHelper instance;

    public static TaskServiceHelper getInstance() {
        if (instance == null) {
            instance = new TaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository());
        }
        return instance;
    }

    @VisibleForTesting
    public TaskServiceHelper(TaskRepository taskRepository) {
        this.context = CoreLibrary.getInstance().context().applicationContext();
        this.taskRepository = taskRepository;
    }

    public void syncTasks() {
        String campaigns = allSharedPreferences.getPreference(CAMPAIGNS);
        String groups = TextUtils.join(",", CoreLibrary.getInstance().context().getLocationRepository().getAllLocationIds());

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

}

