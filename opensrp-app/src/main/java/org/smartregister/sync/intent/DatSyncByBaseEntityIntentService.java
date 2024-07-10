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
public class DatSyncByBaseEntityIntentService extends BaseSyncIntentService {

    private Context context;
    private HTTPAgent httpAgent;
    private static final String GET_DATA_SYNC_PATH = "/rest/event/missing";
    private static final String POST_DATA_SYNC_PATH = "/rest/event/invalid";

    public DatSyncByBaseEntityIntentService() {
        super("InValidateIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<String> clientIds = new ArrayList<>();
        ArrayList<String> eventsIds = new ArrayList<>();

        try {
            super.onHandleIntent(intent);
            //get baseentity id for client and event from server

            String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
            if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
            }
            String url = baseUrl + GET_DATA_SYNC_PATH;
            Response resp = httpAgent.fetch(url);
            if (resp.isFailure() && !resp.isUrlError() && !resp.isTimeoutError()) {
                broadcastStatus("No id found to Sync");
                return;
            }
            JSONObject results = new JSONObject((String) resp.payload());
            Log.v("MISSING_DATA","results:"+results);
            if (results.has(AllConstants.KEY.CLIENTS)) {
                JSONArray validClient = results.getJSONArray(AllConstants.KEY.CLIENTS);

                for (int i = 0; i < validClient.length(); i++) {
                    String validClientString = validClient.getString(i);
                    Log.v("MISSING_DATA","validClientString:"+validClientString);
                    clientIds.add(validClientString);
                }
            }

            if (results.has(AllConstants.KEY.EVENTS)) {
                JSONArray validEvents = results.getJSONArray(AllConstants.KEY.EVENTS);
                for (int i = 0; i < validEvents.length(); i++) {
                    String validEventId = validEvents.getString(i);
                    Log.v("MISSING_DATA","formsubmissionid:"+validEventId);
                    eventsIds.add(validEventId);
                }

            }
            Log.v("MISSING_DATA","clientIds:"+clientIds.size()+":eventsIds:"+eventsIds.size());
            if(clientIds.size() == 0 && eventsIds.size() == 0){
                broadcastStatus("No id found to Sync");
                return;
            }

            EventClientRepository db = CoreLibrary.getInstance().context().getEventClientRepository();

            List<JSONObject> clients = db.getClientsByBaseEntityIds(clientIds,true);
            List<JSONObject> events = db.getClientsByBaseEntityIds(eventsIds,false);

            JSONObject request = new JSONObject();
            if(clients.size()>0){
                request.put(AllConstants.KEY.CLIENTS,clients);
            }
            if(events.size()>0){
                request.put(AllConstants.KEY.EVENTS,events);
            }


            String jsonPayload = request.toString();
            Log.v("MISSING_DATA","jsonPayload"+jsonPayload);
            Response<String> response = httpAgent.postWithJsonResponse(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            POST_DATA_SYNC_PATH),
                    jsonPayload);
            if (response.isFailure() || response.isTimeoutError() || StringUtils.isBlank(response.payload())) {
                Log.v("DATA_SYNC", "Validation sync failed.");
                broadcastStatus("data Syncing fail");
                return;
            }


           broadcastStatus("Data Sync complete");

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            broadcastStatus("data Syncing fail");
        }
    }
    private void broadcastStatus(String message){
        Intent broadcastIntent = new Intent("DATA_SYNC");
        broadcastIntent.putExtra("EXTRA_DATA_SYNC", message);
        sendBroadcast(broadcastIntent);
    }


}
