package org.ei.opensrp.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;

import static org.ei.opensrp.util.Log.logInfo;

public class SyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                context,
                org.ei.opensrp.Context.getInstance().actionService(),
                org.ei.opensrp.Context.getInstance().formSubmissionSyncService(),
                new SyncProgressIndicator(),
                org.ei.opensrp.Context.getInstance().allFormVersionSyncService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}

