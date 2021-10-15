package org.smartregister.job;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public abstract class BaseWorkRequest {

    public static Operation scheduleJob(@NotNull @NonNull Class<? extends ListenableWorker> workerClass, Long start, Long flex) {
        String jobTag = workerClass.getName();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(workerClass, start, TimeUnit.MINUTES)
                .setInitialDelay(flex, TimeUnit.MINUTES)
                .build();
        Timber.d("Scheduling job with name " + jobTag + " : JOB ID " + periodicWorkRequest.getId() + " periodically every " + start + " minutes and flex value of " + flex + " minutes");
        return WorkManager.getInstance().enqueueUniquePeriodicWork(jobTag, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    public static Operation scheduleJob(@NonNull @NotNull String jobTag, @NotNull @NonNull PeriodicWorkRequest periodicWorkRequest){
        Timber.d("Scheduling job with name " + jobTag + " : JOB ID " + periodicWorkRequest.getId() + " periodically");
        return WorkManager.getInstance().enqueueUniquePeriodicWork(jobTag, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    /**
     * For jobs that need to be started immediately
     */
    public static Operation scheduleJobImmediately(@NotNull @NonNull String jobTag, @NotNull @NonNull OneTimeWorkRequest oneTimeWorkRequest) {
        Timber.d("Scheduling job with name " + jobTag + " immediately with JOB ID " + oneTimeWorkRequest.getId());
        return WorkManager.getInstance()
                .enqueueUniqueWork(jobTag, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);
    }

    /**
     * For jobs that need to be started immediately
     */
    public static Operation scheduleJobImmediately(@NotNull @NonNull Class<? extends ListenableWorker> workerClass) {
        String jobTag = workerClass.getName();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(workerClass)
                .addTag(jobTag)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2L, TimeUnit.MINUTES)
                .build();

        Timber.d("Scheduling job with name " + jobTag + " immediately with JOB ID " + oneTimeWorkRequest.getId());
        return WorkManager.getInstance()
                .enqueueUniqueWork(jobTag, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);
    }
}
