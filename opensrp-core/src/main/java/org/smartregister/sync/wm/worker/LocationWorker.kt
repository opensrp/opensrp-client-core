package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.sync.helper.LocationServiceHelper
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class LocationWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    override fun getTitle(): String  = "Fetching Locations"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running...")
        return try {
            LocationServiceHelper.getInstance()
                .apply {
                    fetchLocationsStructures()
                }

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

    companion object {
        const val TAG = "LocationWorker"
    }
}