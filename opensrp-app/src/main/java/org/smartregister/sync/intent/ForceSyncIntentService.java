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
import java.util.List;

/**
 * Created by keyman on 11/10/2017.
 */
public class ForceSyncIntentService extends BaseSyncIntentService {

    private Context context;
    private HTTPAgent httpAgent;
    private static final int FETCH_LIMIT = 100;
    private static final String VALIDATE_SYNC_PATH = "rest/event/invalid";
    public static final String ACTION_SYNC = "FORCE_SYNC";
    public static final String EXTRA_SYNC = "EXTRA_FORCE_SYNC";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_NOTHING = "nothing";
    public static final String STATUS_SUCCESS = "success";
    public ForceSyncIntentService() {
        super("ForceSyncIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            super.onHandleIntent(intent);
            int fetchLimit = FETCH_LIMIT;
            EventClientRepository db = CoreLibrary.getInstance().context().getEventClientRepository();

            List<JSONObject> clients = db.getUnSyncedClientForceSync(fetchLimit);
            List<JSONObject> events = db.getUnSyncedEventsForceSync(fetchLimit);
            if(clients.size() == 0 && events.size() == 0){
                broadcastStatus(STATUS_NOTHING);
                return;
            }

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
            if (response.isFailure() || StringUtils.isBlank(response.payload())) {
                broadcastStatus(STATUS_FAILED);
                return;
            }

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
        }
    }
    private void broadcastStatus(String message){
        Intent broadcastIntent = new Intent(ACTION_SYNC);
        broadcastIntent.putExtra(EXTRA_SYNC, message);
        sendBroadcast(broadcastIntent);
    }


}
