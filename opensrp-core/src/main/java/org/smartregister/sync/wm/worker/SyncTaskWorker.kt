package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.sync.helper.TaskServiceHelper
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class SyncTaskWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            TaskServiceHelper.getInstance()
                .apply {
                    syncTasks()
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
        const val TAG = "SyncTaskWorker"
    }
}