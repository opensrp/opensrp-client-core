package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.smartregister.CoreLibrary;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.service.ActionService;
import org.smartregister.util.NetworkUtils;


public class ExtendedSyncIntentService extends IntentService {

    private ActionService actionService;
    private static final String TAG = ExtendedSyncIntentService.class.getCanonicalName();

    public ExtendedSyncIntentService() {
        super("ExtendedSyncIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        actionService = CoreLibrary.getInstance().context().actionService();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {

            if (NetworkUtils.isNetworkAvailable()) {
                actionService.fetchNewActions();

                startSyncValidation();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void startSyncValidation() {
        ValidateSyncDataServiceJob.scheduleJobImmediately(ValidateSyncDataServiceJob.TAG);
    }


}
