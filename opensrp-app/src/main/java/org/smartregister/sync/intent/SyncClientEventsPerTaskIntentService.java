package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.sync.helper.TaskServiceHelper;

/**
 * Created by cozej4 on 2020-02-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class SyncClientEventsPerTaskIntentService extends BaseSyncIntentService {
    private static final String TAG = "SyncTaskIntentService";
    private TaskServiceHelper taskServiceHelper;

    public SyncClientEventsPerTaskIntentService() {
        super(TAG);
    }

    public SyncClientEventsPerTaskIntentService(TaskServiceHelper taskServiceHelper) {
        super(TAG);
        this.taskServiceHelper = taskServiceHelper;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (taskServiceHelper == null) {
            taskServiceHelper = TaskServiceHelper.getInstance();
        }
        super.onHandleIntent(intent);
        taskServiceHelper.syncTasks();
    }

}