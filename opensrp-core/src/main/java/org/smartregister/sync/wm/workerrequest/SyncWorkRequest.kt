package org.smartregister.sync.wm.workerrequest

import android.content.Context
import android.os.Build
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import timber.log.Timber
import java.util.concurrent.TimeUnit


object SyncWorkRequest {

    fun runWorker(context: Context, workerClass: Class<out ListenableWorker>, workName: String = workerClass::class.java.name, inputs: Data = Data.EMPTY) {
        val inputData: Data = Data.Builder()
            .putAll(inputs)
            .build()

        val constraintsBuilder = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(false)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
            constraintsBuilder.setRequiresDeviceIdle(false)
        }
        val constraints: Constraints = constraintsBuilder.build()
        val request = OneTimeWorkRequest.Builder(workerClass)
            .setInputData(inputData)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2L, TimeUnit.MINUTES)
            .build()
        Timber.d("Scheduling job with name $workName immediately with JOB ID ${request.id}")
        WorkManager.getInstance(context).enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancelWork(context: Context, workerClass: Class<out ListenableWorker>, workName: String = workerClass::class.java.name){
        WorkManager.getInstance(context).cancelUniqueWork(workName)
    }

}