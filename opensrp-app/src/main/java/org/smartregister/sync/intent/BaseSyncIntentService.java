package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.service.HTTPAgent;

/**
 * Created by Vincent Karuri on 26/08/2019
 */
public class BaseSyncIntentService extends IntentService {

    public BaseSyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CoreLibrary coreLibrary =  CoreLibrary.getInstance();
        HTTPAgent httpAgent = coreLibrary.context().httpAgent();
        httpAgent.setConnectTimeout(coreLibrary.getSyncConfiguration().getConnectTimeout());
        httpAgent.setReadTimeout(coreLibrary.getSyncConfiguration().getReadTimeout());
    }
}
