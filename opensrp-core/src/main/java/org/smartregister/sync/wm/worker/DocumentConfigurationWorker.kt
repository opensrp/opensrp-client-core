package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.CoreLibrary
import org.smartregister.service.DocumentConfigurationService
import timber.log.Timber

class DocumentConfigurationWorker(context: Context, workerParams: WorkerParameters): BaseWorker(context, workerParams) {

    override fun getTitle(): String = "Fetching Document Configuration"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            val openSrpContext = CoreLibrary.getInstance().context()
            val httpAgent = openSrpContext.httpAgent
            val manifestRepository = openSrpContext.manifestRepository
            val clientFormRepository = openSrpContext.clientFormRepository
            val configuration = openSrpContext.configuration()
            DocumentConfigurationService(httpAgent, manifestRepository, clientFormRepository, configuration)
                .fetchManifest()

            Result.success().apply {
                notificationDelegate.notify("Success!!")
            }
        } catch (e:Exception){
            Timber.e(e)
            Result.failure().apply {
                notificationDelegate.notify("Error: ${e.message}")
            }
        }

    }

    companion object{
        const val TAG = "DocumentConfigurationWorker"
    }
}