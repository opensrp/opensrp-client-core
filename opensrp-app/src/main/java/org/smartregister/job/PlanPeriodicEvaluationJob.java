package org.smartregister.job;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.AllConstants;
import org.smartregister.domain.Action;
import org.smartregister.sync.intent.PlanPeriodicPlanEvaluationService;
import org.smartregister.utils.DateTypeConverter;
import org.smartregister.utils.TaskDateTimeTypeConverter;
import org.smartregister.utils.TimingRepeatTimeTypeConverter;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class PlanPeriodicEvaluationJob extends DailyJob {

    public static final String TAG = "PlanPeriodicEvaluationJob";
    public static final String SCHEDULE_ADHOC_TAG = "PlanPeriodicEvaluationAdhocJob";

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .registerTypeAdapter(Time.class, new TimingRepeatTimeTypeConverter())
            .create();

    public static void scheduleEverydayAt(@NonNull String jobTag, int hour, int minute, @NonNull Action action, String planId) {
        JobRequest.Builder jobRequest = new JobRequest.Builder(jobTag);
        PersistableBundleCompat persistableBundleCompat = new PersistableBundleCompat();

        persistableBundleCompat.putString(AllConstants.INTENT_KEY.ACTION, getActionJson(action));
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.ACTION_CODE, action.getCode());
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.ACTION_IDENTIFIER, action.getIdentifier());
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.PLAN_ID, planId);
        jobRequest.addExtras(persistableBundleCompat);

        long startTime = TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
        schedule(jobRequest, startTime, startTime + TimeUnit.MINUTES.toMillis(45));
    }

    /**
     * For jobs that need to be started immediately
     */
    public static void scheduleJobImmediately() {
        int jobId = startNowOnce(new JobRequest.Builder(SCHEDULE_ADHOC_TAG));
        Timber.d("Scheduling job with name " + SCHEDULE_ADHOC_TAG + " immediately with JOB ID " + jobId);
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull Params params) {
        Intent intent = new Intent(getContext(), PlanPeriodicPlanEvaluationService.class);
        String actionString = params.getExtras().getString(AllConstants.INTENT_KEY.ACTION, null);
        String planId = params.getExtras().getString(AllConstants.INTENT_KEY.PLAN_ID, null);
        String actionIdentifier = params.getExtras().getString(AllConstants.INTENT_KEY.ACTION_IDENTIFIER, null);
        String actionCode = params.getExtras().getString(AllConstants.INTENT_KEY.ACTION_CODE, null);

        if (TextUtils.isEmpty(actionString) || TextUtils.isEmpty(planId)) {
            return DailyJobResult.CANCEL;
        }

        intent.putExtra(AllConstants.INTENT_KEY.ACTION, actionString);
        intent.putExtra(AllConstants.INTENT_KEY.ACTION_IDENTIFIER, actionIdentifier);
        intent.putExtra(AllConstants.INTENT_KEY.ACTION_CODE, actionCode);
        intent.putExtra(AllConstants.INTENT_KEY.PLAN_ID, planId);
        getContext().startService(intent);

        return DailyJobResult.SUCCESS;
    }

    public static String getActionJson(@NonNull Action action) {
        return gson.toJson(action);
    }

}
