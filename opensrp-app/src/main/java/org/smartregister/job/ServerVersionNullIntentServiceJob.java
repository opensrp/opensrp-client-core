//package org.smartregister.job;
//
//import android.content.Intent;
//import android.support.annotation.NonNull;
//
//import org.smartregister.AllConstants;
//import org.smartregister.sync.intent.InValidateIntentService;
//import org.smartregister.sync.intent.ServerVersionNullIntentService;
//
//
//public class ServerVersionNullIntentServiceJob extends BaseJob {
//
//    public static final String TAG = "ServerVersionNullIntentServiceJob";
//
//    @NonNull
//    @Override
//    protected Result onRunJob(@NonNull Params params) {
//        Intent intent = new Intent(getApplicationContext(), ServerVersionNullIntentService.class);
//        getApplicationContext().startService(intent);
//        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
//    }
//}
