package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.CoreLibrary;

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
        coreLibrary.context().getHttpAgent().setConnectTimeout(coreLibrary.getSyncConfiguration().getConnectTimeout());
        coreLibrary.context().getHttpAgent().setReadTimeout(coreLibrary.getSyncConfiguration().getReadTimeout());
    }
}
