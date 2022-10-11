package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.smartregister.CoreLibrary

abstract class BaseWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams){

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

}