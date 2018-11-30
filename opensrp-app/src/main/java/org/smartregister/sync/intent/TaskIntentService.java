package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.AllConstants.REVEAL_CAMPAIGNS;
import static org.smartregister.AllConstants.REVEAL_OPERATIONAL_AREAS;

public class TaskIntentService extends IntentService {
    public static final String CAMPAIGN_URL = "/rest/task/";
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    private static final String TAG = TaskIntentService.class.getCanonicalName();
    private TaskRepository taskRepository;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .serializeNulls().create();
    AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    public TaskIntentService() {
        super("FetchTasks");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String campaigns = allSharedPreferences.getRevealCampaignsOperationalArea(REVEAL_CAMPAIGNS);
        String groups = allSharedPreferences.getRevealCampaignsOperationalArea(REVEAL_OPERATIONAL_AREAS);
        syncTasks(campaigns, groups, allSharedPreferences.fetchCampaingTaskLastSyncDate(TASK_LAST_SYNC_DATE));
    }

    protected void syncTasks(String campaign, String group, Long serverVersion) {

        try {

            JSONArray tasksResponse = fetchTasks(campaign, group, serverVersion);
            for (Task task : parseTasksFromServer(tasksResponse)) {
                try {
                    taskRepository.addOrUpdate(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected List<Task> parseTasksFromServer(JSONArray campaignsFromServer) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < campaignsFromServer.length(); i++) {
            try {
                tasks.add(gson.fromJson(campaignsFromServer.getJSONObject(i).toString(), Task.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }

    private JSONArray fetchTasks(String campaign, String group, Long serverVersion) throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + CAMPAIGN_URL + "?campaign=" + campaign + "&group=" + group + "&serverVersion=" + serverVersion;

        if (httpAgent == null) {
            throw new IllegalArgumentException(CAMPAIGN_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            throw new NoHttpResponseException(CAMPAIGN_URL + " not returned data");
        }

        return new JSONArray((String) resp.payload());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        return super.onStartCommand(intent, flags, startId);
    }


}
