package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.CoreLibrary
import org.smartregister.sync.wm.workerrequest.WorkRequest
import timber.log.Timber

class ExtendedSyncWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    override fun getTitle(): String  = "Extended Sync"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running...")
        return try {
            val coreLibrary = CoreLibrary.getInstance()
            val actionService = coreLibrary.context().actionService()
            val syncConfiguration = coreLibrary.syncConfiguration

            if (syncConfiguration != null && !syncConfiguration.disableActionService()) actionService.fetchNewActions()

            startSyncValidation()

            Result.success().apply {
                notificationDelegate.notify("Complete")
                notificationDelegate.dismiss()
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure().apply {
                notificationDelegate.notify("Failed")
                notificationDelegate.dismiss()
            }
        }
    }

    private fun startSyncValidation(){
        WorkRequest.runImmediately(applicationContext, ValidateSyncWorker::class.java)
    }

    companion object {
        const val TAG = "ExtendedSyncWorker"
    }
}