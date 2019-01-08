package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.sync.helper.TaskServiceHelper;

public class SyncTaskIntentService extends IntentService {
    private static final String TAG = "SyncTaskIntentService";

    public SyncTaskIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TaskServiceHelper taskServiceHelper = TaskServiceHelper.getInstance();

        taskServiceHelper.syncTasks();

    }

}