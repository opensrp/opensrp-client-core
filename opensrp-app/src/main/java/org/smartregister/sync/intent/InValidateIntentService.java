package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Response;
import org.smartregister.domain.db.Event;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import static org.smartregister.util.JsonFormUtils.gson;
/**
 * Created by keyman on 11/10/2017.
 */
public class InValidateIntentService extends BaseSyncIntentService {

    private Context context;
    private HTTPAgent httpAgent;
    private static final int FETCH_LIMIT = 50;
    private static final String VALIDATE_SYNC_PATH = "rest/event/invalid";
    public static final String ACTION_INVALIDATION = "INVALID_SYNC";
    public static final String EXTRA_INVALIDATION = "EXTRA_INVALID_SYNC";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_NOTHING = "nothing";
    public static final String STATUS_SUCCESS = "success";

    public InValidateIntentService() {
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
        Log.v("INVALID_REQ","invalid called :"+new DateTime(System.currentTimeMillis()));
        Utils.appendLog("SYNC_URL","InValidateIntentService called"+new DateTime(System.currentTimeMillis()));
        try {
            super.onHandleIntent(intent);
            int fetchLimit = FETCH_LIMIT;
            EventClientRepository db = CoreLibrary.getInstance().context().getEventClientRepository();

            List<JSONObject> clients = db.getUnValidatedClients(fetchLimit);
            List<JSONObject> events = db.getUnValidatedEvents(fetchLimit);
            Log.v("INVALID_REQ","InValidateIntentService>clients.size():"+clients.size()+":event size:"+events.size());
            if(clients.size() == 0 && events.size() == 0){
                broadcastStatus(STATUS_NOTHING);
                return;
            }
            if(events.size()>0){
                for(JSONObject eventObj: events){
                    Event evt = gson.fromJson(eventObj.toString(),Event.class);
                    String baseEntityId = evt.getBaseEntityId();
                    Log.v("INVALID_REQ","event base entity id:baseEntityId:"+baseEntityId);
                    JSONObject jsonObject = db.getClientByBaseEntityId(baseEntityId);
                    clients.add(jsonObject);
                }

            }
            Utils.appendLog("SYNC_URL","InValidateIntentService>clients.size():"+clients.size()+":event size:"+events.size());

            JSONObject request = new JSONObject();
            if(clients.size()>0){
                request.put(AllConstants.KEY.CLIENTS,clients);
            }
            if(events.size()>0){
                request.put(AllConstants.KEY.EVENTS,events);
            }

            String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
            if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
            }

            String jsonPayload = request.toString();
            Response<String> response = httpAgent.postWithJsonResponse(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            VALIDATE_SYNC_PATH),
                    jsonPayload);
            if (response.isFailure() || response.isTimeoutError() || StringUtils.isBlank(response.payload())) {
                broadcastStatus(STATUS_FAILED);
                return;
            }
            Utils.appendLog("SYNC_URL","InValidateIntentService>response:"+response.payload());
            Log.v("INVALID_REQ","InValidateIntentService>response:"+response.payload());


            JSONObject results = new JSONObject(response.payload());
            if (results.has(AllConstants.KEY.CLIENTS)) {
                JSONArray validClient = results.getJSONArray(AllConstants.KEY.CLIENTS);

                for (int i = 0; i < validClient.length(); i++) {
                    String validClientString = validClient.getString(i);
                    db.markClientValidationStatus(validClientString, true);
                }
            }

            if (results.has(AllConstants.KEY.EVENTS)) {
                JSONArray validEvents = results.getJSONArray(AllConstants.KEY.EVENTS);
                for (int i = 0; i < validEvents.length(); i++) {
                    String validEventId = validEvents.getString(i);
                    db.markEventValidationStatus(validEventId, true);
                }

            }
           broadcastStatus(STATUS_SUCCESS);

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            Utils.appendLog("SYNC_URL","InValidateIntentService>exception:"+e.getMessage());

        }
    }
    private void broadcastStatus(String message){
        try{
            Intent broadcastIntent = new Intent("INVALID_SYNC");
            broadcastIntent.putExtra("EXTRA_INVALID_SYNC", message);
            sendBroadcast(broadcastIntent);
        }catch (Exception e){

        }

    }


}
