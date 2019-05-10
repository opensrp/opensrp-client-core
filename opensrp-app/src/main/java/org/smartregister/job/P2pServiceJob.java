package org.smartregister.job;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.P2pProcessRecordsService;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/05/2019
 */

public class P2pServiceJob extends BaseJob {

    public static final String TAG = P2pServiceJob.class.getName();
    private Class<? extends P2pProcessRecordsService> serviceClass;

    public P2pServiceJob(Class<? extends P2pProcessRecordsService> serviceClass) {
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
