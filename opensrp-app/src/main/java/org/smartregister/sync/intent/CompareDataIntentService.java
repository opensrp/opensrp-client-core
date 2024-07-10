package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Response;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by keyman on 11/10/2017.
 */
public class CompareDataIntentService extends BaseSyncIntentService {

    private Context context;
    private HTTPAgent httpAgent;
    private static final int FETCH_LIMIT = 200;
    private static final String COMPARE_SYNC_PATH = "/rest/validate/data";

    public CompareDataIntentService() {
        super("CompareDataIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int eventFetchLimit = 0;
        int clientFetchLimit = 0;
        try {
            super.onHandleIntent(intent);
            EventClientRepository db = CoreLibrary.getInstance().context().getEventClientRepository();
            int totalClient = db.getAllClientCount();
            int totalEvent = db.getAllEventCount();
            while(eventFetchLimit<=totalEvent){
                    List<String> clientIds = db.getAllClients(FETCH_LIMIT,clientFetchLimit);
                    List<String> eventIds = db.getAllEventsFormSubmissionId(FETCH_LIMIT,eventFetchLimit);
                    if(clientIds.size() == 0 && eventIds.size() == 0){
                        broadcastStatus("No data found to Sync");
                        return;
                    }
                    clientFetchLimit = clientFetchLimit + clientIds.size();
                    eventFetchLimit = eventFetchLimit + eventIds.size();
                    JSONObject request = request(clientIds, eventIds);
                    if (request == null) {
                        broadcastStatus("No data found to Sync");
                        return;
                    }

                    String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
                    if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
                    }

                    String jsonPayload = request.toString();
                    Response<String> response = httpAgent.postWithJsonResponse(
                            MessageFormat.format("{0}/{1}",
                                    baseUrl,
                                    COMPARE_SYNC_PATH),
                            jsonPayload);
                    if (response.isFailure() || response.isTimeoutError() || StringUtils.isBlank(response.payload())) {
                        Log.e(getClass().getName(), "Validation sync failed.");
                        broadcastStatus("Fail to Sync");
                        return;
                    }

                    //JSONObject results = new JSONObject(response.payload());
                    Log.v("COMPARE_DATE",">>results:"+response.payload());

            }

            broadcastStatus("Sync successfully");

        } catch (Exception e) {
            Log.v("COMPARE_DATE","exception");
            broadcastStatus("exception at response ");
        }
    }

    private JSONObject request(List<String> clientIds, List<String> eventIds) {
        try {

            JSONArray clientIdArray = null;
            if (!clientIds.isEmpty()) {
                clientIdArray = new JSONArray(clientIds);
            }

            JSONArray eventIdArray = null;
            if (!eventIds.isEmpty()) {
                eventIdArray = new JSONArray(eventIds);
            }

            if (clientIdArray != null || eventIdArray != null) {
                JSONObject request = new JSONObject();
                if (clientIdArray != null) {
                    request.put(AllConstants.KEY.CLIENTS, clientIdArray);
                }

                if (eventIdArray != null) {
                    request.put(AllConstants.KEY.EVENTS, eventIdArray);
                }

                return request;

            }

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;
    }
    private void broadcastStatus(String message){
        Intent broadcastIntent = new Intent("COMPARE_DATA");
        broadcastIntent.putExtra("EXTRA_COMPARE_DATA", message);
        sendBroadcast(broadcastIntent);
    }

}
