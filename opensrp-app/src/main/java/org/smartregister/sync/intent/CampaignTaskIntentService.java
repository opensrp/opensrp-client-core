package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.NetworkUtils;

import java.util.Date;

public class CampaignTaskIntentService extends IntentService {

    final static String CAMPAIGN_SYNC_URL = "/rest/campaign";
    protected final static int CAMPAIGN_SYNC_LIMIT = 250;
    private Context context;
    private HTTPAgent httpAgent;


    public CampaignTaskIntentService() {
        super("SyncCampaignTaskService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        handleSync();
    }

    private void complete(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);

        sendBroadcast(intent);

        ECSyncHelper ecSyncUpdater = ECSyncHelper.getInstance(context);
        ecSyncUpdater.updateLastCheckTimeStamp(new Date().getTime());
    }

    protected void handleSync() {
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);

        doSync();
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        sendBroadcast(intent);
    }

    private void doSync() {
        if (!NetworkUtils.isNetworkAvailable()) {
            complete(FetchStatus.noConnection);
            return;
        }

        try {
            pullECFromServer();

        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
            complete(FetchStatus.fetchedFailed);
        }
    }

    private void pullECFromServer() {
        fetchRetry(0);
    }


    private synchronized void fetchRetry(final int count) {
        try {
            SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
            if (StringUtils.isBlank(configs.getSyncFilterParam()) || StringUtils.isBlank(configs.getSyncFilterValue())) {
                complete(FetchStatus.fetchedFailed);
                return;
            }

            final ECSyncHelper ecSyncUpdater = ECSyncHelper.getInstance(context);
            String baseUrl = CoreLibrary.getInstance().context().
                    configuration().dristhiBaseURL();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
            }

            Long lastSyncDatetime = ecSyncUpdater.getLastSyncTimeStamp();
            Log.i(SyncIntentService.class.getName(), "LAST SYNC DT :" + new DateTime(lastSyncDatetime));

            String url = baseUrl + CAMPAIGN_SYNC_URL + "?" + configs.getSyncFilterParam() + "=" + configs.getSyncFilterValue() + "&serverVersion=" + lastSyncDatetime + "&limit=" + SyncIntentService.EVENT_PULL_LIMIT;
            Log.i(SyncIntentService.class.getName(), "URL: " + url);

            if (httpAgent == null) {
                complete(FetchStatus.fetchedFailed);
            }

            Response resp = httpAgent.fetch(url);
            if (resp.isFailure()) {
                fetchFailed(count);
            }

            JSONObject jsonObject = new JSONObject((String) resp.payload());

            int eCount = fetchNumberOfEvents(jsonObject);
            Log.i(getClass().getName(), "Parse Network Event Count: " + eCount);

        } catch (Exception e) {
            Log.e(getClass().getName(), "Fetch Retry Exception: " + e.getMessage(), e.getCause());
            fetchFailed(count);
        }
    }

    public void fetchFailed(int count) {
        if (count < CoreLibrary.getInstance().getSyncConfiguration().getSyncMaxRetries()) {
            int newCount = count + 1;
            fetchRetry(newCount);
        } else {
            complete(FetchStatus.fetchedFailed);
        }
    }

    private int fetchNumberOfEvents(JSONObject jsonObject) {
        int count = -1;
        final String NO_OF_EVENTS = "no_of_events";
        try {
            if (jsonObject != null && jsonObject.has(NO_OF_EVENTS)) {
                count = jsonObject.getInt(NO_OF_EVENTS);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        return count;
    }


}
