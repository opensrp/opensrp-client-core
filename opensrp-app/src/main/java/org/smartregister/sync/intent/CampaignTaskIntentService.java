package org.smartregister.sync.intent;

/**
 * Created by ndegwamartin on 09/04/2018.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.http.NoHttpResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;

public class CampaignTaskIntentService extends IntentService {
    public static final String CAMPAIGN_URL = "/rest/campaign/";
    private static final String TAG = CampaignTaskIntentService.class.getCanonicalName();


    public CampaignTaskIntentService() {
        super("PullUniqueOpenMRSUniqueIdsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("shimba ", "HERE");

            JSONArray ids = fetchCampaigns();

            Log.i("shimba ", ids.toString());

//            if (ids != null && ids.has(IDENTIFIERS)) {
////                parseResponse(ids);
//            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
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


}
