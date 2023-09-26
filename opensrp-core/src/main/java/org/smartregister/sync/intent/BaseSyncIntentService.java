package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.service.HTTPAgent;

import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Vincent Karuri on 26/08/2019
 */

@Deprecated
public class BaseSyncIntentService extends IntentService {

    public BaseSyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CoreLibrary coreLibrary = CoreLibrary.getInstance();
        HTTPAgent httpAgent = coreLibrary.context().httpAgent();
        httpAgent.setConnectTimeout(coreLibrary.getSyncConfiguration().getConnectTimeout());
        httpAgent.setReadTimeout(coreLibrary.getSyncConfiguration().getReadTimeout());
    }

}
