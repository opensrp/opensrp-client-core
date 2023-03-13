package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.json.JSONException
import org.smartregister.CoreLibrary
import org.smartregister.sync.helper.SyncSettingsServiceHelper
import org.smartregister.sync.wm.workerrequest.WorkRequest
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class SettingsSyncWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    override fun getTitle(): String  = "Syncing Settings"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running...")
        return try {
            val openSrpContext = CoreLibrary.getInstance().context()
            val syncSettingsServiceHelper = SyncSettingsServiceHelper(openSrpContext.configuration().dristhiBaseURL(), openSrpContext.httpAgent)
            val isSuccessfulSync = processSettings(syncSettingsServiceHelper)
            if (isSuccessfulSync) {
                WorkRequest.runImmediately(applicationContext, SyncWorker::class.java)
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

    private fun processSettings(syncSettingsServiceHelper: SyncSettingsServiceHelper): Boolean {
        var isSuccessfulSync = true
        try {
            val count: Int = syncSettingsServiceHelper.processIntent()
        } catch (e: JSONException) {
            isSuccessfulSync = false
            Timber.e(e, "Error fetching client settings")
        }
        return isSuccessfulSync
    }

    companion object {
        const val TAG = "SettingsSyncWorker"
    }
}