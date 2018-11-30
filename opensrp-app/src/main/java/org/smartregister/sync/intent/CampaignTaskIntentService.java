package org.smartregister.sync.intent;

/**
 * Created by ndegwamartin on 09/04/2018.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Campaign;
import org.smartregister.domain.Response;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.CampaignServiceHelper;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.ArrayList;
import java.util.List;

public class CampaignTaskIntentService extends IntentService {
    public static final String CAMPAIGN_URL = "/rest/campaign/";
    private static final String TAG = CampaignTaskIntentService.class.getCanonicalName();
    private CampaignRepository campaignRepository;


    String campaignJson = "{\"identifier\":\"IRS_2018_S1\",\"title\":\"2019 IRS Season 1\",\"description\":\"This is the 2010 IRS Spray Campaign for Zambia for the first spray season dated 1 Jan 2019 - 31 Mar 2019.\",\"status\":\"In Progress\",\"executionPeriod\":{\"start\":\"2019-01-01\",\"end\":\"2019-03-31\"},\"authoredOn\":\"2018-10-01T0900\",\"lastModified\":\"2018-10-01T0900\",\"owner\":\"jdoe\",\"serverVersion\":0}";

    protected CampaignServiceHelper campaignServiceHelper;

    public CampaignTaskIntentService() {
        super("PullUniqueOpenMRSUniqueIdsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("shimba ", "HERE");

            Campaign campaign = campaignServiceHelper.testSerialize(campaignJson);

//            JSONArray ids = fetchCampaigns();
//            parseCampaignsFromServer(ids);

//            Log.i("shimba ", );
            Log.i("shimba ", campaign.getTitle());


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected List<Campaign> parseCampaignsFromServer(JSONArray campaignsFromServer){
        List<Campaign> campaigns = new ArrayList<>();


//        Campaign campaign;
//        for(int i=0; i< campaignsFromServer.length(); i++){
            //                String campaignJson = campaignsFromServer.getJSONObject(i).toString();
//        Campaign campaign = gson.fromJson(campaignJson, Campaign.class);
//            campaigns.add(campaign);

//        }

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
    public void onCreate() {
        super.onCreate();
        Context context = CoreLibrary.getInstance().context();
        campaignServiceHelper = new CampaignServiceHelper(context.configuration().dristhiBaseURL(), context.getHttpAgent());
    }


}
