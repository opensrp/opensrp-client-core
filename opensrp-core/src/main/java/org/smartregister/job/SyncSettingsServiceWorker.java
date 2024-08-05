package org.smartregister.job;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.smartregister.sync.intent.SettingsSyncIntentService;

// replaces SyncSettingsServiceJob
public class SyncSettingsServiceWorker extends Worker {
    public SyncSettingsServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent(getApplicationContext(), SettingsSyncIntentService.class);
        getApplicationContext().startService(intent);
        return Result.success();
    }
}
