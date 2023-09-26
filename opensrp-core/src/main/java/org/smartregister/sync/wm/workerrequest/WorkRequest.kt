package org.smartregister.sync.wm.workerrequest

import android.content.Context
import android.os.Build
import androidx.work.*
import timber.log.Timber
import java.util.concurrent.TimeUnit


object WorkRequest {

    @JvmStatic
    fun runImmediately(
        context: Context,
        workerClass: Class<out ListenableWorker>,
        workName: String = workerClass::class.java.name,
        inputs: Data = Data.EMPTY
    ) {
        val request = OneTimeWorkRequest.Builder(workerClass)
            .setInputData(getInoutData(inputs))
            .setConstraints(getConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2L, TimeUnit.MINUTES)
            .build()
        Timber.d("Scheduling job with name $workName immediately with JOB ID ${request.id}")
        WorkManager.getInstance(context)
            .enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, request)
    }

    @JvmStatic
    fun runPeriodically(
        context: Context,
        workerClass: Class<out ListenableWorker>,
        workName: String = workerClass::class.java.name,
        interval: Long = 15,
        flexInterval: Long = 5,
        timeUnit: TimeUnit = TimeUnit.MINUTES,
        inputs: Data = Data.EMPTY
    ) {
        val request = PeriodicWorkRequest.Builder(workerClass, interval, timeUnit, flexInterval, timeUnit)
            .setInputData(getInoutData(inputs))
            .setConstraints(getConstraints())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2L, TimeUnit.MINUTES)
            .build()
        Timber.d("Scheduling job with name $workName periodically with JOB ID ${request.id}")
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.REPLACE, request)
    }

    private fun getConstraints(): Constraints {
        val constraintsBuilder = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(false)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            constraintsBuilder.setRequiresDeviceIdle(false)
        }
        return constraintsBuilder.build()
    }

    private fun getInoutData(inputs: Data) = Data.Builder().putAll(inputs).build()

    @JvmStatic
    fun cancelWork(
        context: Context,
        workerClass: Class<out ListenableWorker>,
        workName: String = workerClass::class.java.name
    ) {
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }

}