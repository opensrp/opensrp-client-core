package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.sync.helper.SyncServiceHelper;

public class SyncIntentService extends BaseSyncIntentService {
    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        SyncServiceHelper syncServiceHelper = SyncServiceHelper.getInstance();
        syncServiceHelper.handleSync();
    }


}
