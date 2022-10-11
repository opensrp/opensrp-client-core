package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.CoreLibrary
import org.smartregister.sync.wm.workerrequest.SyncWorkRequest
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class ExtendedSyncWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            val coreLibrary = CoreLibrary.getInstance()
            val actionService = coreLibrary.context().actionService()
            val syncConfiguration = coreLibrary.syncConfiguration
            if (syncConfiguration != null && !syncConfiguration.disableActionService()) actionService.fetchNewActions()
            startSyncValidation()

            Result.success().apply {
                notificationDelegate.notify("Success!!")
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure().apply {
                notificationDelegate.notify("Error: ${e.message}")
            }
        }

    }

    private fun startSyncValidation(){
        SyncWorkRequest.runWorker(applicationContext, ValidateSyncWorker::class.java)
    }

    companion object {
        const val TAG = "ExtendedSyncWorker"
    }
}