package org.smartregister.job;

import android.content.Context;

import androidx.annotation.NonNull;
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

public class DuplicateCleanerWorker extends Worker {
    private Context mContext;

    public static final String TAG = "DuplicateCleanerWorker";

    public DuplicateCleanerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
    public static void schedulePeriodically(Context context, int mins) {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(DuplicateCleanerWorker.class, mins, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

        if (!allSharedPreferences.getBooleanPreference(AllConstants.PREF_KEY.DUPLICATE_IDS_FIXED)) {
            DuplicateZeirIdStatus duplicateZeirIdStatus = AppHealthUtils.cleanUniqueZeirIds();
            Timber.i("Started doing duplicate client-identifier cleanup");
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
