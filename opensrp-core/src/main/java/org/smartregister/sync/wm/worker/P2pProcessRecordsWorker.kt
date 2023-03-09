package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.CoreLibrary
import org.smartregister.domain.FetchStatus
import org.smartregister.repository.EventClientRepository
import org.smartregister.util.Utils
import org.smartregister.util.WorkerNotificationDelegate
import org.smartregister.view.activity.DrishtiApplication
import timber.log.Timber

class P2pProcessRecordsWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    override fun getTitle(): String  = "Processing P2P Records"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            val coreLibrary = CoreLibrary.getInstance()
            val openSrpContext = coreLibrary.context()
            val allSharedPreferences = openSrpContext.allSharedPreferences()
            if (allSharedPreferences.isPeerToPeerUnprocessedEvents){
                coreLibrary.isPeerToPeerProcessing = true

                var eventsMaxRowId = allSharedPreferences.lastPeerToPeerSyncProcessedEvent.toLong()
                val eventClientRepository = openSrpContext.eventClientRepository

                while (eventsMaxRowId > -1) {
                    val eventClientQueryResult =
                        eventClientRepository.fetchEventClientsByRowId(eventsMaxRowId)
                    val eventClientList = eventClientQueryResult.eventClientList
                    if (eventClientList.size > 0) {
                        DrishtiApplication.getInstance<DrishtiApplication>().clientProcessor.processClient(
                            eventClientList
                        )
                        val tableMaxRowId =
                            eventClientRepository.getMaxRowId(EventClientRepository.Table.event)
                        if (tableMaxRowId == eventClientQueryResult.maxRowId) {
                            eventsMaxRowId = -1
                            allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent()
                        } else {
                            eventsMaxRowId = eventClientQueryResult.maxRowId.toLong()
                            allSharedPreferences.lastPeerToPeerSyncProcessedEvent =
                                eventClientQueryResult.maxRowId
                        }

                        // Profile images do not have a foreign key to the clients and can therefore be saved during the sync.
                        // They also do not take long to save and therefore happen during sync
                        Timber.i(
                            "Finished processing %s EventClients",
                            eventClientList.size.toString()
                        )
                    } else {
                        allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent()
                        break
                    }
                }

                sendSyncStatusBroadcastMessage(FetchStatus.fetched)
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

    fun sendSyncStatusBroadcastMessage(fetchStatus: FetchStatus) = applicationContext.sendBroadcast(Utils.completeSync(fetchStatus))

    override fun onStopped() {
        val coreLibrary = CoreLibrary.getInstance()
        coreLibrary.isPeerToPeerProcessing = false
    }

    companion object {
        const val TAG = "P2pProcessRecordsWorker"
    }
}