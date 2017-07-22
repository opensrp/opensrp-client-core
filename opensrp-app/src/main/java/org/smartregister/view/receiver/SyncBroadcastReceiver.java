package org.smartregister.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.sync.UpdateActionsTask;

import static org.smartregister.util.Log.logInfo;

public class SyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                context,
                org.smartregister.Context.getInstance().actionService(),
                org.smartregister.Context.getInstance().formSubmissionSyncService(),
                new SyncProgressIndicator(),
                org.smartregister.Context.getInstance().allFormVersionSyncService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}

