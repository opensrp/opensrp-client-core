package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import org.json.JSONObject
import org.smartregister.AllConstants
import org.smartregister.CoreLibrary
import org.smartregister.R
import org.smartregister.domain.Client
import org.smartregister.domain.Event
import org.smartregister.domain.Response
import org.smartregister.repository.EventClientRepository
import org.smartregister.service.HTTPAgent
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber
import java.text.MessageFormat
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

class ValidateSyncWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {
    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")
        return try {
            val openSrpContext = CoreLibrary.getInstance().context()
            val baseUrl = openSrpContext.configuration().dristhiBaseURL().let {
                val urlSeparator = applicationContext.getString(R.string.url_separator)
                if (it.endsWith(urlSeparator)) it.substring(0, it.lastIndexOf(urlSeparator)) else it
            }
            validateSync(openSrpContext.httpAgent, openSrpContext.eventClientRepository, baseUrl)

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

    private fun validateSync(httpAgent: HTTPAgent, eventClientRepository: EventClientRepository, baseUrl: String, initialFetchLimit: Int = FETCH_LIMIT){
        var fetchLimit = initialFetchLimit
        val clientIds: MutableList<String> =
            eventClientRepository.getUnValidatedClientBaseEntityIds(fetchLimit)
        if (clientIds.isNotEmpty()) {
            fetchLimit -= clientIds.size
        }

        var eventIds: MutableList<String> = ArrayList()
        if (fetchLimit > 0) {
            eventIds = eventClientRepository.getUnValidatedEventFormSubmissionIds(fetchLimit)
        }

        val request: JSONObject = request(clientIds, eventIds) ?: return

        val jsonPayload = request.toString()
        val response: Response<String> = httpAgent.postWithJsonResponse(
            MessageFormat.format(
                "{0}/{1}",
                baseUrl,
                VALIDATE_SYNC_PATH
            ),
            jsonPayload
        )
        if (response.isFailure || StringUtils.isBlank(response.payload())) {
            Timber.e("Validation sync failed.")
            return
        }

        val results = JSONObject(response.payload())

        if (results.has(AllConstants.KEY.CLIENTS)) {
            val inValidClients = results.getJSONArray(AllConstants.KEY.CLIENTS)
            val invalidClientIds: Set<String> = filterArchivedClients(eventClientRepository, extractIds(inValidClients))
            for (id in invalidClientIds) {
                clientIds.remove(id)
                eventClientRepository.markClientValidationStatus(id, false)
            }
            for (clientId in clientIds) {
                eventClientRepository.markClientValidationStatus(clientId, true)
            }
        }

        if (results.has(AllConstants.KEY.EVENTS)) {
            val inValidEvents = results.getJSONArray(AllConstants.KEY.EVENTS)
            val inValidEventIds: Set<String> = filterArchivedEvents(eventClientRepository, extractIds(inValidEvents))
            for (inValidEventId in inValidEventIds) {
                eventIds.remove(inValidEventId)
                eventClientRepository.markEventValidationStatus(inValidEventId, false)
            }
            for (eventId in eventIds) {
                eventClientRepository.markEventValidationStatus(eventId, true)
            }
        }
    }

    private fun extractIds(inValidClients: JSONArray): Set<String> {
        val ids: MutableSet<String> = HashSet()
        for (i in 0 until inValidClients.length()) {
            ids.add(inValidClients.optString(i))
        }
        return ids
    }

    private fun filterArchivedClients(eventClientRepository: EventClientRepository, ids: Set<String>): Set<String> {
        return eventClientRepository.fetchClientByBaseEntityIds(ids)
            .stream()
            .filter(Predicate { c: Client -> c.dateVoided == null })
            .map<String>(Function { obj: Client -> obj.baseEntityId })
            .collect(Collectors.toSet<String>())
    }

    private fun filterArchivedEvents(eventClientRepository: EventClientRepository, ids: Set<String>): Set<String> {
        return eventClientRepository.getEventsByEventIds(ids)
            .stream()
            .filter(Predicate { e: Event -> e.dateVoided == null })
            .map<String>(Function { obj: Event -> obj.eventId })
            .collect(Collectors.toSet<String>())
    }

    private fun request(clientIds: List<String>, eventIds: List<String>): JSONObject? {
        var clientIdArray: JSONArray? = null
        if (clientIds.isNotEmpty()) {
            clientIdArray = JSONArray(clientIds)
        }
        var eventIdArray: JSONArray? = null
        if (eventIds.isNotEmpty()) {
            eventIdArray = JSONArray(eventIds)
        }
        if (clientIdArray != null || eventIdArray != null) {
            val request = JSONObject()
            if (clientIdArray != null) {
                request.put(AllConstants.KEY.CLIENTS, clientIdArray)
            }
            if (eventIdArray != null) {
                request.put(AllConstants.KEY.EVENTS, eventIdArray)
            }
            return request
        }
        return null
    }

    companion object {
        const val TAG = "ValidateSyncWorker"
        private const val FETCH_LIMIT = 100
        private const val VALIDATE_SYNC_PATH = "rest/validate/sync"
    }
}