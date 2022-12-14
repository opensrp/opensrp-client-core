package org.smartregister.job;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.DuplicateZeirIdStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AppHealthUtils;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class DuplicateZeirIdsCleanerWorker extends Worker {
    private Context mContext;

    public static final String TAG = "DuplicateZeirIdsCleanerWorker";

    public DuplicateZeirIdsCleanerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    public static boolean shouldSchedule() {
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        return !allSharedPreferences.getBooleanPreference(AllConstants.PREF_KEY.DUPLICATE_IDS_FIXED);
    }

    /**
     * Schedule this job to run periodically
     *
     * @param context
     * @param mins - Duration after which the job repeatedly runs. This should be at least 15 mins
     */
    public static void schedulePeriodically(Context context, int mins, String[] registrationEventTypes) {
        Data data = new Data.Builder()
                .putStringArray(AllConstants.WorkData.EVENT_TYPES, registrationEventTypes)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(DuplicateZeirIdsCleanerWorker.class, mins, TimeUnit.MINUTES)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        String[] eventTypes = getInputData().getStringArray(AllConstants.WorkData.EVENT_TYPES);

        if (!allSharedPreferences.getBooleanPreference(AllConstants.PREF_KEY.DUPLICATE_IDS_FIXED)) {
            DuplicateZeirIdStatus duplicateZeirIdStatus = AppHealthUtils.cleanUniqueZeirIds(eventTypes);
            Timber.i("Started cleaning duplicate client-identifiers");
            if (duplicateZeirIdStatus != null && duplicateZeirIdStatus.equals(DuplicateZeirIdStatus.CLEANED)) {
                allSharedPreferences.saveBooleanPreference(AllConstants.PREF_KEY.DUPLICATE_IDS_FIXED, true);
                WorkManager.getInstance(mContext).cancelWorkById(this.getId());
            }
        } else {
            WorkManager.getInstance(mContext).cancelWorkById(this.getId());
        }

        return Result.success();
    }
}
