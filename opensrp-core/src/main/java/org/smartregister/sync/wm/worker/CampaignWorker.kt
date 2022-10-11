package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.smartregister.AllConstants
import org.smartregister.CoreLibrary
import org.smartregister.domain.Campaign
import org.smartregister.domain.FetchStatus
import org.smartregister.exception.NoHttpResponseException
import org.smartregister.service.HTTPAgent
import org.smartregister.util.DateTimeTypeConverter
import org.smartregister.util.DateTypeConverter
import org.smartregister.util.Utils
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class CampaignWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    private val notificationDelegate = WorkerNotificationDelegate(context, TAG)

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")

        val opensrpContext = CoreLibrary.getInstance().context()
        val baseUrl = opensrpContext.configuration().dristhiBaseURL()
        val allSharedPreferences = opensrpContext.allSharedPreferences()
        val campaignRepository = opensrpContext.campaignRepository
        val httpAgent = opensrpContext.httpAgent
        return try {
            val campaignsResponse = fetchCampaigns(httpAgent, baseUrl)
            val allowedCampaigns = allSharedPreferences.getPreference(AllConstants.CAMPAIGNS).split(",")
            val campaigns = gson.fromJson<List<Campaign>>(
                campaignsResponse,
                object : TypeToken<List<Campaign?>?>() {}.type
            )
            val errors = mutableListOf<Throwable>()
            campaigns.filter { it.identifier != null && it.identifier in allowedCampaigns }
                .forEach {
                    runCatching {  campaignRepository.addOrUpdate(it)}.onFailure { e -> errors.add(e) }
                }
            if (errors.isNotEmpty()) throw Exception(errors.random())

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

    fun getUrl(baseUrl: String): String {
        val endString = "/"
        return "${if (baseUrl.endsWith(endString)) baseUrl.substring(0, baseUrl.lastIndexOf(endString)) else baseUrl}$CAMPAIGN_URL"
    }

    @Throws(NoHttpResponseException::class)
    fun fetchCampaigns(httpAgent: HTTPAgent?, baseUrl: String): String {
        if (httpAgent == null) {
            applicationContext.sendBroadcast(Utils.completeSync(FetchStatus.noConnection))
            throw IllegalArgumentException("$CAMPAIGN_URL http agent is null")
        }
        val resp= httpAgent.fetch(getUrl(baseUrl))
        if (resp.isFailure) {
            applicationContext.sendBroadcast(Utils.completeSync(FetchStatus.nothingFetched))
            throw NoHttpResponseException("$CAMPAIGN_URL not returned data")
        }
        return resp.payload().toString()
    }

    companion object{
        const val CAMPAIGN_URL = "/rest/campaign/"
        const val TAG = "CampaignWorker"
        val gson: Gson = GsonBuilder().registerTypeAdapter(
            DateTime::class.java,
            DateTimeTypeConverter("yyyy-MM-dd'T'HHmm")
        )
            .registerTypeAdapter(LocalDate::class.java, DateTypeConverter()).create()
    }
}