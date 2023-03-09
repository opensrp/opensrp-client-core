package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.sync.helper.LocationServiceHelper
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class SyncLocationsByTeamIdsWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    override fun getTitle(): String  = "Syncing  Locations By Team Ids"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            LocationServiceHelper.getInstance()
                .apply {
                    fetchOpenMrsLocationsByTeamIds()
                }
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

    companion object {
        const val TAG = "SyncLocationsByTeamIdsWorker"
    }
}