package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.sync.helper.PlanIntentServiceHelper
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class PlanWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    override fun getTitle(): String  = "Syncing Plans"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running...")
        return try {
            PlanIntentServiceHelper.getInstance().syncPlans()
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
        const val TAG = "PlanWorker"
    }
}