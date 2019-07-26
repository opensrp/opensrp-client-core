package org.smartregister.sync.helper;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.domain.TaskUpdate;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

public class TaskServiceHelper {
    private static final String TAG = TaskServiceHelper.class.getCanonicalName();

    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    protected final Context context;
    private TaskRepository taskRepository;
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    public static final String UPDATE_STATUS_URL = "/rest/task/update_status";
    public static final String ADD_TASK_URL = "/rest/task/add";
    public static final String SYNC_TASK_URL = "/rest/task/sync";

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

    public List<Task> syncTasks() {
        syncCreatedTaskToServer();
        syncTaskStatusToServer();
        return fetchTasksFromServer();
    }

    public List<Task> fetchTasksFromServer() {
        Set<String> planDefinitions = CoreLibrary.getInstance().context().getPlanDefinitionRepository().findAllPlanDefinitionIds();
        String groups = TextUtils.join(",", CoreLibrary.getInstance().context().getLocationRepository().getAllLocationIds());
        long serverVersion = 0;
        try {
            serverVersion = Long.parseLong(allSharedPreferences.getPreference(TASK_LAST_SYNC_DATE));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (serverVersion > 0) serverVersion += 1;
        try {
            String tasksResponse = fetchTasks(TextUtils.join(",", planDefinitions), groups, serverVersion);
            List<Task> tasks = taskGson.fromJson(tasksResponse, new TypeToken<List<Task>>() {
            }.getType());
            for (Task task : tasks) {
                try {
                    task.setSyncStatus(BaseRepository.TYPE_Synced);
                    taskRepository.addOrUpdate(task);
                } catch (Exception e) {
                    Log.e(TAG, "Error saving task " + task.getIdentifier(), e);
                }
            }
            if (!Utils.isEmptyCollection(tasks)) {
                allSharedPreferences.savePreference(TASK_LAST_SYNC_DATE, getTaskMaxServerVersion(tasks));
            }
            return tasks;
        } catch (Exception e) {
            Log.e(TAG, "Error fetching tasks from server ", e);
        }
        return null;
    }

    private String fetchTasks(String plan, String group, Long serverVersion) throws NoHttpResponseException {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + SYNC_TASK_URL + "?plan=" + plan + "&group=" + group + "&serverVersion=" + serverVersion;

        if (httpAgent == null) {
            throw new IllegalArgumentException(SYNC_TASK_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            throw new NoHttpResponseException(SYNC_TASK_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    private String getTaskMaxServerVersion(List<Task> tasks) {
        long maxServerVersion = 0;

        for (Task task : tasks) {
            long serverVersion = task.getServerVersion();
            if (serverVersion > maxServerVersion) {
                maxServerVersion = serverVersion;
            }
        }

        return String.valueOf(maxServerVersion);
    }

    public void syncTaskStatusToServer() {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        List<TaskUpdate> updates = taskRepository.getUnSyncedTaskStatus();
        if (!updates.isEmpty()) {
            String jsonPayload = new Gson().toJson(updates);

            String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
            Response<String> response = httpAgent.postWithJsonResponse(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            UPDATE_STATUS_URL),
                    jsonPayload);

            if (response.isFailure()) {
                Log.e(getClass().getName(), "Update Status failed.");
                return;
            }

            if (response.payload() != null) {
                try {
                    JSONObject idObject = new JSONObject(response.payload());
                    JSONArray updatedIds = idObject.optJSONArray("task_ids");
                    if (updatedIds != null) {
                        for (int i = 0; i < updatedIds.length(); i++) {
                            taskRepository.markTaskAsSynced(updatedIds.get(i).toString());
                        }
                    }
                } catch (JSONException e) {
                    Log.e(getClass().getName(), "Error processing the tasks payload: " + response.payload());
                }
            }
        }
    }

    public void syncCreatedTaskToServer() {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        List<Task> tasks = taskRepository.getAllUnsynchedCreatedTasks();
        if (!tasks.isEmpty()) {
            String jsonPayload = taskGson.toJson(tasks);
            String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
            Response<String> response = httpAgent.post(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            ADD_TASK_URL),
                    jsonPayload);
            if (response.isFailure()) {
                Log.e(getClass().getName(), "Failed to create new tasks on server.");
                return;
            }

            for (Task task : tasks) {
                taskRepository.markTaskAsSynced(task.getIdentifier());
            }
        }
    }

}

