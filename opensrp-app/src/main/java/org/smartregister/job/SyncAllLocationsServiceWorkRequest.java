package org.smartregister.job;

import org.smartregister.sync.intent.SyncAllLocationsIntentWorker;

public class SyncAllLocationsServiceWorkRequest extends BaseWorkRequest {

    public static final String TAG = "SyncAllLocationsServiceJob";

    private Class<? extends SyncAllLocationsIntentWorker> serviceClass;

    public SyncAllLocationsServiceWorkRequest() {
        this(SyncAllLocationsIntentWorker.class);
    }

    public SyncAllLocationsServiceWorkRequest(Class<? extends SyncAllLocationsIntentWorker> serviceClass) {
        this.serviceClass = serviceClass;
    }
}
