package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.db.EventClient;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.NetworkUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SyncIntentService extends IntentService {
    private static final String ADD_URL = "/rest/event/add";
    public static final String SYNC_URL = "/rest/event/sync";

    private Context context;
    private HTTPAgent httpAgent;

    private static final String TAG = SyncIntentService.class.getName();

    protected static final int EVENT_PULL_LIMIT = 250;
    protected static final int EVENT_PUSH_LIMIT = 50;

    public SyncIntentService() {
        super("SyncIntentService");
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

    protected void handleSync() {
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);

        doSync();
    }

    private void doSync() {
        if (!NetworkUtils.isNetworkAvailable()) {
            complete(FetchStatus.noConnection);
            return;
        }

        try {
            verifyAuthorization();
            pushToServer();
            pullECFromServer();

        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
            complete(FetchStatus.fetchedFailed);
        }
    }

    private void verifyAuthorization() {
        org.smartregister.Context opensrpContent = CoreLibrary.getInstance().context();
        String baseUrl = opensrpContent.configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }
        final String username = opensrpContent.allSharedPreferences().fetchRegisteredANM();
        final String password = opensrpContent.allSettings().fetchANMPassword();
        baseUrl = baseUrl + "/user-details?anm-id=" + username;
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            final String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);
            int statusCode = urlConnection.getResponseCode();
            urlConnection.disconnect();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                Log.i(TAG, "User not authorized. User access was revoked, will log off user");
                //force remote login
                opensrpContent.userService().forceRemoteLogin();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setPackage(context.getPackageName());
                //retrieve the main/launcher activity defined in the manifest and open it
                List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);
                if (activities.size() == 1) {
                    intent = intent.setClassName(context.getPackageName(), activities.get(0).activityInfo.name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getApplicationContext().startActivity(intent);
                }
                //logoff opensrp session
                opensrpContent.userService().logoutSession();
            } else if (statusCode != HttpStatus.SC_OK) {
                Log.w(TAG, "Error occurred verifying authorization, User will not be logged off");
            } else {
                Log.i(TAG, "User is Authorized");
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
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

            String url = baseUrl + SYNC_URL + "?" + configs.getSyncFilterParam() + "=" + configs.getSyncFilterValue() + "&serverVersion=" + lastSyncDatetime + "&limit=" + SyncIntentService.EVENT_PULL_LIMIT;
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

            if (eCount == 0) {
                complete(FetchStatus.nothingFetched);
            } else if (eCount < 0) {
                fetchFailed(count);
            } else if (eCount > 0) {
                final Pair<Long, Long> serverVersionPair = getMinMaxServerVersions(jsonObject);
                long lastServerVersion = serverVersionPair.second - 1;
                if (eCount < EVENT_PULL_LIMIT) {
                    lastServerVersion = serverVersionPair.second;
                }

                ecSyncUpdater.saveAllClientsAndEvents(jsonObject);
                ecSyncUpdater.updateLastSyncTimeStamp(lastServerVersion);

                processClient(serverVersionPair);

                fetchRetry(0);
            }
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

    private void processClient(Pair<Long, Long> serverVersionPair) {
        try {
            ECSyncHelper ecUpdater = ECSyncHelper.getInstance(context);
            List<EventClient> events = ecUpdater.allEventClients(serverVersionPair.first - 1, serverVersionPair.second);
            getClientProcessor().processClient(events);
            sendSyncStatusBroadcastMessage(FetchStatus.fetched);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Process Client Exception: " + e.getMessage(), e.getCause());
        }
    }

    // PUSH TO SERVER
    private void pushToServer() {
        pushECToServer();
    }

    private void pushECToServer() {
        EventClientRepository db = CoreLibrary.getInstance().context().getEventClientRepository();
        boolean keepSyncing = true;

        while (keepSyncing) {
            try {
                Map<String, Object> pendingEvents = db.getUnSyncedEvents(EVENT_PUSH_LIMIT);

                if (pendingEvents.isEmpty()) {
                    return;
                }

                String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
                if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
                }
                // create request body
                JSONObject request = new JSONObject();
                if (pendingEvents.containsKey(context.getString(R.string.clients_key))) {
                    request.put(context.getString(R.string.clients_key), pendingEvents.get(context.getString(R.string.clients_key)));
                }
                if (pendingEvents.containsKey(context.getString(R.string.events_key))) {
                    request.put(context.getString(R.string.events_key), pendingEvents.get(context.getString(R.string.events_key)));
                }
                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        MessageFormat.format("{0}/{1}",
                                baseUrl,
                                ADD_URL),
                        jsonPayload);
                if (response.isFailure()) {
                    Log.e(getClass().getName(), "Events sync failed.");
                    return;
                }
                db.markEventsAsSynced(pendingEvents);
                Log.i(getClass().getName(), "Events synced successfully.");
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        sendBroadcast(intent);
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

    private Pair<Long, Long> getMinMaxServerVersions(JSONObject jsonObject) {
        final String EVENTS = "events";
        final String SERVER_VERSION = "serverVersion";
        try {
            if (jsonObject != null && jsonObject.has(EVENTS)) {
                JSONArray events = jsonObject.getJSONArray(EVENTS);

                long maxServerVersion = Long.MIN_VALUE;
                long minServerVersion = Long.MAX_VALUE;

                for (int i = 0; i < events.length(); i++) {
                    Object o = events.get(i);
                    if (o instanceof JSONObject) {
                        JSONObject jo = (JSONObject) o;
                        if (jo.has(SERVER_VERSION)) {
                            long serverVersion = jo.getLong(SERVER_VERSION);
                            if (serverVersion > maxServerVersion) {
                                maxServerVersion = serverVersion;
                            }

                            if (serverVersion < minServerVersion) {
                                minServerVersion = serverVersion;
                            }
                        }
                    }
                }
                return Pair.create(minServerVersion, maxServerVersion);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
        return Pair.create(0L, 0L);
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

    protected ClientProcessorForJava getClientProcessor() {
        return ClientProcessorForJava.getInstance(context);
    }

}
