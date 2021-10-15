package org.smartregister.job;

import org.smartregister.sync.intent.SyncTaskIntentWorker;

public class SyncTaskServiceWorkRequest extends BaseWorkRequest {

    public static final String TAG = "SyncTaskServiceJob";


    private Class<? extends SyncTaskIntentWorker> serviceClass;

    public SyncTaskServiceWorkRequest(Class<? extends SyncTaskIntentWorker> serviceClass) {
        this.serviceClass = serviceClass;
    }

}
