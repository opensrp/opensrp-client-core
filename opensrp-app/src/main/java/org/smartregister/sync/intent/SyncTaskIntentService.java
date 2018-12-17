package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.sync.helper.LocationTaskServiceHelper;

public class SyncTaskIntentService extends IntentService {
    private static final String TAG = "SyncTaskIntentService";

    public SyncTaskIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocationTaskServiceHelper locationTaskServiceHelper = new LocationTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository(), CoreLibrary.getInstance().context().getLocationRepository(), CoreLibrary.getInstance().context().getStructureRepository());

        locationTaskServiceHelper.syncTasks();
    }

}