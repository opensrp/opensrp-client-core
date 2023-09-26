package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.json.JSONObject
import org.smartregister.CoreLibrary
import org.smartregister.exception.NoHttpResponseException
import org.smartregister.repository.UniqueIdRepository
import org.smartregister.service.HTTPAgent
import org.smartregister.sync.intent.PullUniqueIdsIntentService
import timber.log.Timber

class PullUniqueIdsWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    override fun getTitle(): String  = "Fetching OpenSRP IDs"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running...")
        return try {
            val openSrpContext = CoreLibrary.getInstance()
            val configs = openSrpContext.syncConfiguration
            val uniqueIdRepo = openSrpContext.context().uniqueIdRepository

            val numberToGenerate: Int  =
                if (uniqueIdRepo.countUnUsedIds() == 0L) { // first time pull no ids at all
                    configs!!.uniqueIdInitialBatchSize
                } else if (uniqueIdRepo.countUnUsedIds() <= 250) { //maintain a minimum of 250 else skip this pull
                    configs!!.uniqueIdBatchSize
                } else {
                    return Result.failure()
                }

            val ids: JSONObject = fetchOpenMRSIds(openSrpContext.context().httpAgent, configs.uniqueIdSource, numberToGenerate)
            if (ids.has(IDENTIFIERS)) {
                parseResponse(uniqueIdRepo, ids)
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

    @Throws(Exception::class)
    private fun fetchOpenMRSIds(httpAgent: HTTPAgent, source: Int, numberToGenerate: Int): JSONObject {
        var baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL()
        val endString = "/"

        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString))
        }

        val url = String.format("%s%s?source=%s&numberToGenerate=%s", baseUrl, ID_URL, source, numberToGenerate)

        Timber.i("URL: %s", url)

        val resp = httpAgent.fetch(url)
        if (resp.isFailure) {
            throw NoHttpResponseException(String.format("%s not returned data", ID_URL))
        }

        return JSONObject(resp.payload() as String)
    }

    @Throws(Exception::class)
    private fun parseResponse(uniqueIdRepo: UniqueIdRepository, idsFromOMRS: JSONObject) {
        val jsonArray = idsFromOMRS.getJSONArray(IDENTIFIERS)

        if (jsonArray.length() > 0) {
            val ids: MutableList<String> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                ids.add(jsonArray.getString(i))
            }
            uniqueIdRepo.bulkInsertOpenmrsIds(ids)
        }
    }

    companion object {
        const val TAG = "PullUniqueIdsWorker"

        const val ID_URL = "/uniqueids/get"

        const val IDENTIFIERS = "identifiers"
    }
}