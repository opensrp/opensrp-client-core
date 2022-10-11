package org.smartregister.sync.wm.worker

import android.content.Context
import android.content.Intent
import android.util.Pair
import androidx.annotation.IntRange
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkerParameters
import com.google.firebase.perf.metrics.Trace
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.json.JSONException
import org.json.JSONObject
import org.smartregister.AllConstants
import org.smartregister.AllConstants.PerformanceMonitoring
import org.smartregister.CoreLibrary
import org.smartregister.R
import org.smartregister.SyncConfiguration
import org.smartregister.domain.FetchStatus
import org.smartregister.domain.Response
import org.smartregister.domain.SyncEntity
import org.smartregister.domain.SyncProgress
import org.smartregister.receiver.SyncStatusBroadcastReceiver
import org.smartregister.repository.AllSharedPreferences
import org.smartregister.repository.EventClientRepository
import org.smartregister.service.HTTPAgent
import org.smartregister.sync.RequestParamsBuilder
import org.smartregister.sync.helper.ECSyncHelper
import org.smartregister.sync.helper.ValidateAssignmentHelper
import org.smartregister.util.NetworkUtils
import org.smartregister.util.PerformanceMonitoringUtils
import org.smartregister.util.SyncUtils
import org.smartregister.util.Utils
import org.smartregister.util.WorkerNotificationDelegate
import org.smartregister.view.activity.DrishtiApplication
import timber.log.Timber
import java.text.MessageFormat
import java.util.Date

class SyncWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            val syncUtils = SyncUtils(applicationContext)
            val httpAgent = CoreLibrary.getInstance().context().httpAgent
            val eventSyncTrace: Trace =
                PerformanceMonitoringUtils.initTrace(PerformanceMonitoring.EVENT_SYNC)
            val processClientTrace: Trace =
                PerformanceMonitoringUtils.initTrace(PerformanceMonitoring.CLIENT_PROCESSING)
            val allSharedPreferences: AllSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences()

            Syncer(applicationContext, httpAgent, syncUtils, eventSyncTrace, processClientTrace, allSharedPreferences)
                .apply {
                    doSync()
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
        const val TAG = "SyncWorker"
    }
}

class Syncer(
    private val context: Context,
    private val httpAgent: HTTPAgent,
    private val syncUtils: SyncUtils,
    private val eventSyncTrace: Trace,
    private val processClientTrace: Trace,
    allSharedPreferences: AllSharedPreferences
) {
    private val validateAssignmentHelper: ValidateAssignmentHelper
    private var totalRecords: Long = 0
    private var fetchedRecords = 0
    private var totalRecordsCount = 0
    private val team: String?
    private val providerId: String?

    //this variable using to track the sync request goes along with add events/clients
    private var isEmptyToAdd = true

    init {
        providerId = allSharedPreferences.fetchRegisteredANM()
        team = allSharedPreferences.fetchDefaultTeam(providerId)
        validateAssignmentHelper = ValidateAssignmentHelper(syncUtils)
    }

    private fun sendSyncStatusBroadcastMessage(fetchStatus: FetchStatus) {
        val intent = Intent()
        intent.action = SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus)
        context.sendBroadcast(intent)
    }

    fun doSync() {
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted)

        if (!NetworkUtils.isNetworkAvailable()) {
            complete(FetchStatus.noConnection)
            return
        }
        try {
            val hasValidAuthorization: Boolean = syncUtils.verifyAuthorization()
            var isSuccessfulPushSync = false
            if (hasValidAuthorization || !CoreLibrary.getInstance().syncConfiguration!!.disableSyncToServerIfUserIsDisabled()) {
                isSuccessfulPushSync = pushToServer()
            }
            if (!hasValidAuthorization) {
                syncUtils.logoutUser()
            } else if (!syncUtils.isAppVersionAllowed) {
                if (isSuccessfulPushSync) {
                    syncUtils.logoutUser()
                } else {
                    return
                }
            } else {
                pullECFromServer()
            }
        } catch (e: Exception) {
            Timber.e(e)
            complete(FetchStatus.fetchedFailed)
        }
    }

    fun pullECFromServer() {
        fetchRetry(0, true)
    }

    @Synchronized
    private fun fetchRetry(count: Int, returnCount: Boolean) {
        try {
            val configs = CoreLibrary.getInstance().syncConfiguration
            if (configs!!.syncFilterParam == null || StringUtils.isBlank(
                    configs.syncFilterValue
                )
            ) {
                complete(FetchStatus.fetchedFailed)
                return
            }
            val ecSyncUpdater = ECSyncHelper.getInstance(context)
            val baseUrl = getFormattedBaseUrl()
            val lastSyncDatetime = ecSyncUpdater.lastSyncTimeStamp
            Timber.i("LAST SYNC DT %s", DateTime(lastSyncDatetime))
            complete(FetchStatus.fetchedFailed)
            startEventTrace(PerformanceMonitoring.FETCH, 0)
            val syncParamBuilder = RequestParamsBuilder().configureSyncFilter(
                configs.syncFilterParam.value(), configs.syncFilterValue
            ).addServerVersion(lastSyncDatetime).addEventPullLimit(getEventPullLimit())
            val resp = getUrlResponse(
                httpAgent,
                baseUrl + SYNC_URL, syncParamBuilder,
                configs, returnCount
            )
            if (resp == null) {
                FetchStatus.fetchedFailed.setDisplayValue("Empty response")
                complete(FetchStatus.fetchedFailed)
                return
            }
            if (resp.isUrlError) {
                FetchStatus.fetchedFailed.setDisplayValue(resp.status().displayValue())
                complete(FetchStatus.fetchedFailed)
                return
            }
            if (resp.isTimeoutError) {
                FetchStatus.fetchedFailed.setDisplayValue(resp.status().displayValue())
                complete(FetchStatus.fetchedFailed)
                return
            }
            if (resp.isFailure && !resp.isUrlError && !resp.isTimeoutError) {
                fetchFailed(count)
                return
            }
            if (returnCount) {
                totalRecords = resp.totalRecords
            }
            processFetchedEvents(httpAgent, resp, ecSyncUpdater, count)
        } catch (e: java.lang.Exception) {
            Timber.e(e, "Fetch Retry Exception:  %s", e.message)
            fetchFailed(count)
        }
    }

    /**
     * This methods makes a request to the server using either Get or Post as is configured by [org.smartregister.SyncConfiguration.isSyncUsingPost]
     *
     * @param baseURL              the base url for the request
     * @param requestParamsBuilder the query string builder object
     * @param configs              the Sync Configuration object with various configurations
     * @param returnCount          a boolean flag, whether to return the total count of records as part of the response (field - total_records)
     */
    fun getUrlResponse(
        httpAgent: HTTPAgent,
        baseURL: String,
        requestParamsBuilder: RequestParamsBuilder,
        configs: SyncConfiguration,
        returnCount: Boolean
    ): Response<*> {
        val response: Response<*>
        var requestUrl = baseURL
        if (configs.isSyncUsingPost) {
            response = httpAgent.postWithJsonResponse(
                requestUrl,
                requestParamsBuilder.returnCount(returnCount).build()
            )
        } else {
            requestUrl += "?" + requestParamsBuilder.build()
            Timber.i("URL: %s", requestUrl)
            response = httpAgent.fetch(requestUrl)
        }
        return response
    }

    @Throws(JSONException::class)
    private fun processFetchedEvents(
        httpAgent: HTTPAgent,
        resp: Response<*>,
        ecSyncUpdater: ECSyncHelper,
        count: Int
    ) {
        val eCount: Int
        var jsonObject = JSONObject()
        if (resp.payload() == null) {
            eCount = 0
        } else {
            jsonObject = JSONObject(resp.payload() as String)
            eCount = fetchNumberOfEvents(jsonObject)
            Timber.i("Parse Network Event Count: %s", eCount)
        }
        if (eCount == 0) {
            complete(FetchStatus.nothingFetched)
            sendSyncProgressBroadcast(eCount) // Complete progress update
        } else if (eCount < 0) {
            fetchFailed(count)
        } else {
            val serverVersionPair = getMinMaxServerVersions(jsonObject)
            var lastServerVersion = serverVersionPair.second - 1
            if (eCount < getEventPullLimit()) {
                lastServerVersion = serverVersionPair.second
            }
            PerformanceMonitoringUtils.addAttribute(
                eventSyncTrace,
                AllConstants.COUNT,
                eCount.toString()
            )
            PerformanceMonitoringUtils.stopTrace(eventSyncTrace)
            val isSaved = ecSyncUpdater.saveAllClientsAndEvents(jsonObject)
            //update sync time if all event client is save.
            if (isSaved) {
                PerformanceMonitoringUtils.startTrace(processClientTrace)
                processClient(serverVersionPair)
                PerformanceMonitoringUtils.addAttribute(
                    processClientTrace,
                    AllConstants.COUNT,
                    eCount.toString()
                )
                PerformanceMonitoringUtils.addAttribute(
                    processClientTrace,
                    PerformanceMonitoring.TEAM,
                    team
                )
                PerformanceMonitoringUtils.stopTrace(processClientTrace)
                ecSyncUpdater.updateLastSyncTimeStamp(lastServerVersion)
            }
            sendSyncProgressBroadcast(eCount)
            fetchRetry(0, true)
        }
    }

    private fun fetchFailed(count: Int) {
        if (count < CoreLibrary.getInstance().syncConfiguration!!.syncMaxRetries) {
            val newCount = count + 1
            fetchRetry(newCount, false)
        } else {
            complete(FetchStatus.fetchedFailed)
        }
    }

    private fun processClient(serverVersionPair: Pair<Long, Long>) {
        try {
            val ecUpdater = ECSyncHelper.getInstance(context)
            val events =
                ecUpdater.allEventClients(serverVersionPair.first - 1, serverVersionPair.second)
            DrishtiApplication.getInstance<DrishtiApplication>().clientProcessor.processClient(
                events
            )
            sendSyncStatusBroadcastMessage(FetchStatus.fetched)
        } catch (e: java.lang.Exception) {
            Timber.e(e, "Process Client Exception: %s", e.message)
        }
    }

    // PUSH TO SERVER
    private fun pushToServer(): Boolean {
        return pushECToServer(
            CoreLibrary.getInstance().context().eventClientRepository
        ) &&
                (!CoreLibrary.getInstance().context().hasForeignEvents() || pushECToServer(
                    CoreLibrary.getInstance().context().foreignEventClientRepository
                ))
    }

    private fun pushECToServer(db: EventClientRepository): Boolean {
        var isSuccessfulPushSync = true
        isEmptyToAdd = true
        // push foreign events to server
        val totalEventCount = db.unSyncedEventsCount
        var eventsUploadedCount = 0
        var baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL()
        if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
            baseUrl =
                baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)))
        }
        for (i in 0 until syncUtils.getNumOfSyncAttempts()) {
            val pendingEventsClients = db.getUnSyncedEvents(
                getEventBatchSize()!!
            )
            if (pendingEventsClients.isEmpty()) {
                break
            }
            // create request body
            val request = JSONObject()
            try {
                if (pendingEventsClients.containsKey(AllConstants.KEY.CLIENTS)) {
                    val value = pendingEventsClients[AllConstants.KEY.CLIENTS]
                    request.put(AllConstants.KEY.CLIENTS, value)
                    if (value is List<*>) {
                        eventsUploadedCount += value.size
                    }
                }
                if (pendingEventsClients.containsKey(AllConstants.KEY.EVENTS)) {
                    request.put(
                        AllConstants.KEY.EVENTS,
                        pendingEventsClients[AllConstants.KEY.EVENTS]
                    )
                }
            } catch (e: JSONException) {
                Timber.e(e)
            }
            isEmptyToAdd = false
            val jsonPayload = request.toString()
            startEventTrace(PerformanceMonitoring.PUSH, eventsUploadedCount)
            val response: Response<String> = httpAgent.post(
                MessageFormat.format(
                    "{0}/{1}",
                    baseUrl,
                    ADD_URL
                ),
                jsonPayload
            )
            if (response.isFailure) {
                Timber.e("Events sync failed.")
                isSuccessfulPushSync = false
            } else {
                // do not mark items in list of failed events/clients as synced
                var failedClients: Set<String?>? = null
                var failedEvents: Set<String?>? = null
                val responseData = response.payload()
                if (StringUtils.isNotEmpty(responseData)) {
                    try {
                        val failedEventClients = JSONObject(responseData)
                        failedClients =
                            getFailed(FAILED_CLIENTS, failedEventClients)
                        failedEvents =
                            getFailed(FAILED_EVENTS, failedEventClients)
                    } catch (e: JSONException) {
                        Timber.e(e)
                    }
                }
                db.markEventsAsSynced(pendingEventsClients, failedEvents, failedClients)
                Timber.i("Events synced successfully.")
                PerformanceMonitoringUtils.stopTrace(eventSyncTrace)
                updateProgress(eventsUploadedCount, totalEventCount)
                if (totalEventCount - eventsUploadedCount > 0) pushECToServer(db)
                break
            }
        }
        return isSuccessfulPushSync
    }

    private fun getFailed(recordType: String, failedEventClients: JSONObject): Set<String?>? {
        var set: MutableSet<String?>? = null
        try {
            val failed = failedEventClients.getJSONArray(recordType)
            if (failed.length() > 0) {
                set = HashSet()
                for (i in 0 until failed.length()) {
                    set.add(failed.getString(i))
                }
            }
        } catch (e: JSONException) {
            Timber.e(e)
        }
        return set
    }

    private fun startEventTrace(action: String, count: Int) {
        val configs = CoreLibrary.getInstance().syncConfiguration
        if (configs!!.firebasePerformanceMonitoringEnabled()) {
            PerformanceMonitoringUtils.clearTraceAttributes(eventSyncTrace)
            PerformanceMonitoringUtils.addAttribute(
                eventSyncTrace,
                PerformanceMonitoring.TEAM,
                team
            )
            PerformanceMonitoringUtils.addAttribute(
                eventSyncTrace,
                PerformanceMonitoring.ACTION,
                action
            )
            PerformanceMonitoringUtils.addAttribute(
                eventSyncTrace,
                AllConstants.COUNT,
                count.toString()
            )
            PerformanceMonitoringUtils.startTrace(eventSyncTrace)
        }
    }

    fun isEmptyToAdd(): Boolean {
        return isEmptyToAdd
    }

    fun complete(fetchStatus: FetchStatus) {
        val intent = Intent()
        intent.action = SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus)
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true)
        context.sendBroadcast(intent)

        //sync time not update if sync is fail
        if (fetchStatus != FetchStatus.noConnection && fetchStatus != FetchStatus.fetchedFailed) {
            val ecSyncUpdater = ECSyncHelper.getInstance(context)
            ecSyncUpdater.updateLastCheckTimeStamp(Date().time)
            if (CoreLibrary.getInstance().syncConfiguration!!.validateUserAssignments()) {
                validateAssignmentHelper.validateUserAssignment()
            }
        }
    }

    private fun updateProgress(
        @IntRange(from = 0) progress: Int,
        @IntRange(from = 1) total: Int
    ) {
        val uploadProgressStatus = FetchStatus.fetchProgress
        uploadProgressStatus.setDisplayValue(
            String.format(
                context.getString(R.string.sync_upload_progress_float),
                progress * 100 / total
            )
        )
        sendSyncStatusBroadcastMessage(uploadProgressStatus)
    }

    private fun getMinMaxServerVersions(jsonObject: JSONObject?): Pair<Long, Long> {
        val EVENTS = "events"
        try {
            if (jsonObject != null && jsonObject.has(EVENTS)) {
                val events = jsonObject.getJSONArray(EVENTS)
                var maxServerVersion = Long.MIN_VALUE
                var minServerVersion = Long.MAX_VALUE
                for (i in 0 until events.length()) {
                    val o = events[i]
                    if (o is JSONObject) {
                        val jo = o
                        if (jo.has(AllConstants.SERVER_VERSION)) {
                            val serverVersion = jo.getLong(AllConstants.SERVER_VERSION)
                            if (serverVersion > maxServerVersion) {
                                maxServerVersion = serverVersion
                            }
                            if (serverVersion < minServerVersion) {
                                minServerVersion = serverVersion
                            }
                        }
                    }
                }
                return Pair.create(minServerVersion, maxServerVersion)
            }
        } catch (e: java.lang.Exception) {
            Timber.e(e)
        }
        return Pair.create(0L, 0L)
    }

    fun fetchNumberOfEvents(jsonObject: JSONObject?): Int {
        var count = -1
        val NO_OF_EVENTS = "no_of_events"
        try {
            if (jsonObject != null && jsonObject.has(NO_OF_EVENTS)) {
                count = jsonObject.getInt(NO_OF_EVENTS)
            }
        } catch (e: JSONException) {
            Timber.e(e)
        }
        return count
    }

    fun sendSyncProgressBroadcast(eventCount: Int) {
        totalRecordsCount += totalRecords.toInt()
        fetchedRecords = fetchedRecords + eventCount
        val syncProgress = SyncProgress()
        syncProgress.syncEntity = SyncEntity.EVENTS
        syncProgress.totalRecords = totalRecords
        syncProgress.percentageSynced =
            Utils.calculatePercentage(totalRecordsCount.toLong(), fetchedRecords.toLong())
        val intent = Intent()
        intent.action = AllConstants.SyncProgressConstants.ACTION_SYNC_PROGRESS
        intent.putExtra(AllConstants.SyncProgressConstants.SYNC_PROGRESS_DATA, syncProgress)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun getEventPullLimit(): Int {
        return EVENT_PULL_LIMIT
    }

    private fun getFormattedBaseUrl(): String {
        var baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL()
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"))
        }
        return baseUrl
    }

    private fun getEventBatchSize(): Int {
        return EVENT_PUSH_LIMIT
    }

    companion object {
        const val SYNC_URL = "/rest/event/sync"
        const val EVENT_PULL_LIMIT = 250
        const val EVENT_PUSH_LIMIT = 50
        private const val ADD_URL = "rest/event/add"
        private const val FAILED_CLIENTS = "failed_clients"
        private const val FAILED_EVENTS = "failed_events"
    }
}