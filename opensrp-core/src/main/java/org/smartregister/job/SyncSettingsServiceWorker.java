package org.smartregister.job;

import static org.smartregister.util.Log.logError;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;
import org.smartregister.sync.intent.SettingsSyncIntentService;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

// replaces SyncSettingsServiceJob
public class SyncSettingsServiceWorker extends Worker {
    public static final String SETTINGS_URL = "/rest/settings/sync";

    private static final String TAG = SettingsSyncIntentService.class.getCanonicalName();

    protected SyncSettingsServiceHelper syncSettingsServiceHelper;

    public static final String EVENT_SYNC_COMPLETE = "event_sync_complete";

    public SyncSettingsServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        org.smartregister.Context opensrpContext = CoreLibrary.getInstance().context();
        CoreLibrary coreLibrary = CoreLibrary.getInstance();
        HTTPAgent httpAgent = coreLibrary.context().httpAgent();
        assert coreLibrary.getSyncConfiguration() != null;
        httpAgent.setConnectTimeout(coreLibrary.getSyncConfiguration().getConnectTimeout());
        httpAgent.setReadTimeout(coreLibrary.getSyncConfiguration().getReadTimeout());
        syncSettingsServiceHelper = new SyncSettingsServiceHelper(opensrpContext.configuration().dristhiBaseURL(), opensrpContext.getHttpAgent());
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.d("doing work in SyncsettingsServiceWorker");
        boolean isSuccessfulSync = processSettings();
        if (isSuccessfulSync) {
            Timber.d("processingSettings is a success enqueueing SyncSettingsServiceWorker...");
            // Schedule the sync job using WorkManager
        }

        Timber.d("returning success on SyncSettings work");
        return Result.success();
    }

    protected boolean processSettings() {
        Timber.d("In Settings SyncSettingService...");
        boolean isSuccessfulSync = true;
            try {
              syncSettingsServiceHelper.processIntent();

            } catch (JSONException e) {
                isSuccessfulSync = false;
                logError(TAG + " Error fetching client settings");
            }
        return isSuccessfulSync;
    }

    public static void enqueueOnetimeSettingsSyncIntentService(android.content.Context context){
        OneTimeWorkRequest onetimeSettingSyncWorkRequest = new OneTimeWorkRequest.Builder(SyncSettingsServiceWorker.class).build();
        WorkManager.getInstance(context).beginUniqueWork("settingsSyncService", ExistingWorkPolicy.KEEP, onetimeSettingSyncWorkRequest).enqueue();
    }

    public static void enqueuePeriodicSettingsSyncIntentService(android.content.Context context){
        PeriodicWorkRequest periodicSettingsSyncWorkRequest = new PeriodicWorkRequest.Builder(SyncSettingsServiceWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("periodicSettingsSyncService", ExistingPeriodicWorkPolicy.KEEP, periodicSettingsSyncWorkRequest);

    }
}
