package org.smartregister.job;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.smartregister.util.AppHealthUtils;

public class DuplicateCleanerWorker extends Worker {
    private Context mContext;
    public DuplicateCleanerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if(AppHealthUtils.getUniqueIdCount()>0)
        {
            DuplicateZeirIdStatus duplicateZeirIdStatus = AppHealthUtils.cleanUniqueZeirIds();
            Timber.i("Doing some cleaning work");
            if(duplicateZeirIdStatus!=null && duplicateZeirIdStatus.equals(DuplicateZeirIdStatus.CLEANED))
                WorkManager.getInstance(mContext).cancelWorkById(this.getId());
        }

        return Result.success();
    }
}
