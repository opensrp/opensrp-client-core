package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.NoHttpResponseException;
import org.json.JSONArray;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.SyncIntentServiceHelper;

import static org.smartregister.AllConstants.REVEAL_CAMPAIGNS;
import static org.smartregister.AllConstants.REVEAL_OPERATIONAL_AREAS;

public class TaskIntentService extends IntentService {
    public static final String CAMPAIGN_URL = "/rest/task/sync";
    public static final String TASK_LAST_SYNC_DATE = "TASK_LAST_SYNC_DATE";
    private static final String TAG = TaskIntentService.class.getCanonicalName();
    private TaskRepository taskRepository;
    AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    public TaskIntentService() {
        super("FetchTasks");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncTasks();
    }

    protected void syncTasks() {
        String campaigns = allSharedPreferences.getRevealCampaignsOperationalArea(REVEAL_CAMPAIGNS);
        String groups = allSharedPreferences.getRevealCampaignsOperationalArea(REVEAL_OPERATIONAL_AREAS);
        long serverVersion = allSharedPreferences.fetchRevealIntentServiceLastSyncDate(TASK_LAST_SYNC_DATE);
        try {

            JSONArray tasksResponse = fetchTasks(campaigns, groups, serverVersion);
            for (Task task : SyncIntentServiceHelper.parseTasksFromServer(tasksResponse,Task.class)) {
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
