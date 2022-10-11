package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.json.JSONException
import org.smartregister.CoreLibrary
import org.smartregister.job.SyncServiceJob
import org.smartregister.sync.helper.SyncSettingsServiceHelper
import org.smartregister.sync.wm.workerrequest.SyncWorkRequest
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class SettingsSyncWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            val openSrpContext = CoreLibrary.getInstance().context()
            val syncSettingsServiceHelper = SyncSettingsServiceHelper(openSrpContext.configuration().dristhiBaseURL(), openSrpContext.httpAgent)
            val isSuccessfulSync = processSettings(syncSettingsServiceHelper)
            if (isSuccessfulSync) {
                SyncWorkRequest.runWorker(applicationContext, SyncWorker::class.java)
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

    fun processSettings(syncSettingsServiceHelper: SyncSettingsServiceHelper): Boolean {
        Timber.d("In Settings Sync Intent Service...")
        var isSuccessfulSync = true
        try {
            val count: Int = syncSettingsServiceHelper.processIntent()
        } catch (e: JSONException) {
            isSuccessfulSync = false
            Timber.e(" Error fetching client settings")
        }
        return isSuccessfulSync
    }

    companion object {
        const val TAG = "SettingsSyncWorker"
    }
}