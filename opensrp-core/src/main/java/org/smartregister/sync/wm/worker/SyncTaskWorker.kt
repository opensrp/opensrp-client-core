package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.sync.helper.TaskServiceHelper
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class SyncTaskWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    override fun getTitle(): String  = "Syncing Tasks"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running...")
        return try {
            TaskServiceHelper.getInstance()
                .apply {
                    syncTasks()
                }

            Result.success().apply {
                notificationDelegate.notify("Complete")
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure().apply {
                notificationDelegate.notify("Failed")
            }
        } finally {
            notificationDelegate.dismiss()
        }
    }

    companion object {
        const val TAG = "SyncTaskWorker"
    }
}