package org.smartregister.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.sync.intent.SyncPractitionersByIdAndRoleIntentService;

import timber.log.Timber;

public class SyncPractitionersByIdAndRoleJob extends BaseJob {
    public static final String TAG = "SyncPractitionersByIdAndRoleJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Timber.v("%s started", TAG);
        getApplicationContext().startService(new Intent(getApplicationContext(), SyncPractitionersByIdAndRoleIntentService.class));
        return params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
