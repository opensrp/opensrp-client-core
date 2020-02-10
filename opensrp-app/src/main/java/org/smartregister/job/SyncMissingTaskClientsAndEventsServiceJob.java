package org.smartregister.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SyncClientEventsPerTaskIntentService;

/**
 * Created by cozej4 on 2020-02-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class SyncMissingTaskClientsAndEventsServiceJob extends BaseJob {

    public static final String TAG = SyncMissingTaskClientsAndEventsServiceJob.class.getSimpleName();

    private Class<? extends SyncClientEventsPerTaskIntentService> serviceClass;

    public SyncMissingTaskClientsAndEventsServiceJob(Class<? extends SyncClientEventsPerTaskIntentService> serviceClass) {
        this.serviceClass = serviceClass;
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), serviceClass);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }


}
