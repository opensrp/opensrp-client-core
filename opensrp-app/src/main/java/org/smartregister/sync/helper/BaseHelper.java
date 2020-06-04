package org.smartregister.sync.helper;

import android.content.Context;
import android.content.Intent;

import org.smartregister.AllConstants;
import org.smartregister.domain.SyncProgress;

/**
 * Created by Richard Kareko on 6/4/20.
 */

public class BaseHelper {

    public void sendSyncProgressBroadcast(SyncProgress syncProgress, Context context) {
        Intent intent = new Intent();
        intent.setAction(AllConstants.SYNC_PROGRESS.ACTION_SYNC_PROGRESS);
        intent.putExtra(AllConstants.SYNC_PROGRESS.SYNC_PROGRESS_DATA, syncProgress);
        context.sendBroadcast(intent);
    }
}
