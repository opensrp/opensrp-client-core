package org.smartregister.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.sync.UpdateActionsTask;

import static org.smartregister.util.Log.logInfo;

public class SyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(context,
                CoreLibrary.getInstance().context().actionService(),
                CoreLibrary.getInstance().context().formSubmissionSyncService(),
                new SyncProgressIndicator(),
                CoreLibrary.getInstance().context().allFormVersionSyncService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}

