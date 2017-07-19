package org.opensrp.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.opensrp.sync.SyncAfterFetchListener;
import org.opensrp.sync.SyncProgressIndicator;
import org.opensrp.sync.UpdateActionsTask;

import static org.opensrp.util.Log.logInfo;

public class SyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                context,
                org.opensrp.Context.getInstance().actionService(),
                org.opensrp.Context.getInstance().formSubmissionSyncService(),
                new SyncProgressIndicator(),
                org.opensrp.Context.getInstance().allFormVersionSyncService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}

