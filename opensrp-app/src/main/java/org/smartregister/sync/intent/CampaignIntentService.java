package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Campaign;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;
import org.smartregister.util.Utils;

import java.util.Arrays;
import java.util.List;

import static org.smartregister.AllConstants.CAMPAIGNS;

public class CampaignIntentService extends IntentService {
    public static final String CAMPAIGN_URL = "/rest/campaign/";
    private static final String TAG = "CampaignIntentService";
    private CampaignRepository campaignRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

    public CampaignIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncCampaigns();
    }

    protected void syncCampaigns() {
        try {
            String campaignsResponse = fetchCampaigns();
            List<String> allowedCampaigns = Arrays.asList(allSharedPreferences.getPreference(CAMPAIGNS).split(","));

            List<Campaign> campaigns = gson.fromJson(campaignsResponse, new TypeToken<List<Campaign>>() {
            }.getType());

            for (Campaign campaign : campaigns) {
                try {
                    if (campaign.getIdentifier() != null && allowedCampaigns.contains(campaign.getIdentifier())) {
                        campaignRepository.addOrUpdate(campaign);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String fetchCampaigns() throws NoHttpResponseException {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        String url = baseUrl + CAMPAIGN_URL;

        if (httpAgent == null) {
            sendBroadcast(Utils.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(CAMPAIGN_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            sendBroadcast(Utils.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(CAMPAIGN_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        campaignRepository = CoreLibrary.getInstance().context().getCampaignRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}
