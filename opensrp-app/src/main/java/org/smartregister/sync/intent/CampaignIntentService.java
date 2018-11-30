package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Campaign;
import org.smartregister.domain.Response;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.smartregister.AllConstants.REVEAL_CAMPAIGNS;

public class CampaignIntentService extends IntentService {
    public static final String CAMPAIGN_URL = "/rest/campaign/";
    private static final String TAG = CampaignIntentService.class.getCanonicalName();
    private CampaignRepository campaignRepository;
    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .serializeNulls().create();
    AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    public CampaignIntentService() {
        super("FetchCampaigns");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncCampaigns();
    }

    protected void syncCampaigns() {
        try {
            JSONArray campaignsResponse = fetchCampaigns();
            List<String> allowedCampaigns = Arrays.asList(allSharedPreferences.getRevealCampaignsOperationalArea(REVEAL_CAMPAIGNS).split(","));
            for (Campaign campaign : parseCampaignsFromServer(campaignsResponse)) {
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

    protected List<Campaign> parseCampaignsFromServer(JSONArray campaignsFromServer) {
        List<Campaign> campaigns = new ArrayList<>();
        for (int i = 0; i < campaignsFromServer.length(); i++) {
            try {
                campaigns.add(gson.fromJson(campaignsFromServer.getJSONObject(i).toString(), Campaign.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return campaigns;
    }

    private JSONArray fetchCampaigns() throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + CAMPAIGN_URL;

        if (httpAgent == null) {
            throw new IllegalArgumentException(CAMPAIGN_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            throw new NoHttpResponseException(CAMPAIGN_URL + " not returned data");
        }

        return new JSONArray((String) resp.payload());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        campaignRepository = CoreLibrary.getInstance().context().getCampaignRepository();
        return super.onStartCommand(intent, flags, startId);
    }


}
