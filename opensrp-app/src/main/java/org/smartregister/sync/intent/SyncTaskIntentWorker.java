package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.net.SocketException;

public class SyncTaskIntentWorker extends BaseSyncIntentWorker {
    private static final String TAG = "SyncTaskIntentService";
    private TaskServiceHelper taskServiceHelper;

    public SyncTaskIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        if (taskServiceHelper == null) {
            taskServiceHelper = TaskServiceHelper.getInstance();
        }
        taskServiceHelper.syncTasks();
    }

}