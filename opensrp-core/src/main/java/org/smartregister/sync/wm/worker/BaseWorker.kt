package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.smartregister.CoreLibrary
import org.smartregister.util.WorkerNotificationDelegate

abstract class BaseWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){

    val notificationDelegate = WorkerNotificationDelegate(context, getTitle())

    fun beforeWork(){
        val coreLibrary = CoreLibrary.getInstance()
        val syncConfiguration = coreLibrary.syncConfiguration
        if (syncConfiguration != null){
            coreLibrary.context().httpAgent
                .apply {
                    connectTimeout = syncConfiguration.connectTimeout
                    readTimeout = syncConfiguration.readTimeout
                }
        }
    }

    abstract fun getTitle() : String

}