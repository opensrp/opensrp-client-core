package org.smartregister.job;

import org.smartregister.sync.intent.SyncIntentWorker;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public class SyncServiceWorkRequest extends BaseWorkRequest {

    public static final String TAG = "SyncServiceJob";

    private Class<? extends SyncIntentWorker> serviceClass;

    public SyncServiceWorkRequest(Class<? extends SyncIntentWorker> serviceClass) {
        this.serviceClass = serviceClass;
    }
}
