package org.smartregister.multitenant.check;

import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class PreResetSync extends SyncIntentService {

    protected void performSync() {
        handleSync();
    }

    @Override
    protected void pullECFromServer() {
        // Do not pull from EC Server
    }
}
