package org.smartregister.job;

import android.content.Context;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.smartregister.AllConstants;

import java.util.concurrent.TimeUnit;

/**
 * Created by ndegwamartin on 05/09/2018.
 */
public abstract class BaseJob extends Job {

    private static final String TAG = BaseJob.class.getCanonicalName();

    public static void scheduleJob(String jobTag, int intervalMins, int flexMins) {
        if (JobManager.instance().getAllJobRequestsForTag(jobTag).isEmpty()) {
            boolean toReschedule = intervalMins < 15;
            //evernote doesn't allow less than 15 mins periodic schedule, keep flag ref for workaround

            PersistableBundleCompat extras = new PersistableBundleCompat();
            extras.putBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, toReschedule);

            JobRequest.Builder jobRequest = new JobRequest.Builder(jobTag).setExtras(extras);
            jobRequest.setPeriodic(TimeUnit.MINUTES.toMillis(intervalMins), TimeUnit.MINUTES.toMillis(flexMins));

            try {
                int jobId = jobRequest.build().schedule();
                Log.d(TAG, "Scheduling job with name " + jobTag + " : JOB ID " + jobId + " periodically every " + intervalMins + " minutes and flex value of " + flexMins + " minutes");
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Skipping schedule for job with name " + jobTag + " : Already Exists!");
        }
    }

    /**
     * For jobs that need to be started immediately
     */
    public static void scheduleJobImmediately(String jobTag) {

        int jobId = new JobRequest.Builder(jobTag)
                .startNow()
                .build()
                .schedule();

        Log.d(TAG, "Scheduling job with name " + jobTag + " immediately with JOB ID " + jobId);

    }

    @Override
    protected void onReschedule(int newJobId) {

        Log.d(TAG, "Rescheduling job with name " + this.getParams().getTag() + " JOB ID " + newJobId);
    }

    //Custom app context method to aid unit testing
    public Context getApplicationContext() {
        return super.getContext();
    }
}
