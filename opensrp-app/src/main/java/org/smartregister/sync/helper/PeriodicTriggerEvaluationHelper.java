package org.smartregister.sync.helper;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;
import org.smartregister.AllConstants;
import org.smartregister.domain.Action;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Timing;
import org.smartregister.domain.TimingRepeat;
import org.smartregister.domain.Trigger;
import org.smartregister.job.PlanPeriodicEvaluationJob;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.Set;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 11-03-2021.
 */
public class PeriodicTriggerEvaluationHelper {


    private DateTime timeNow;

    public void reschedulePeriodicPlanEvaluations(List<PlanDefinition> plans, PlanIntentServiceHelper planIntentServiceHelper) {
        for (PlanDefinition plan: plans) {
            List<Action> actions = plan.getActions();
            if (actions != null && actions.size() > 0) {
                for (Action action : actions) {
                    Set<Trigger> triggers = action.getTrigger();
                    if (triggers != null) {
                        for (Trigger trigger: triggers) {
                            // This assumes that the action has only one periodic trigger
                            if (triggers != null && trigger.getType() != null
                                    && TriggerType.PERIODIC.value().equals(trigger.getType())
                                    && isValidDailyTriggerSchedule(trigger, planIntentServiceHelper)) {
                                // Check if the jobs for the action have been scheduled
                                // Delete & reschedule the job using the action.code as the job ID
                                Set<JobRequest> jobRequests = JobManager.create(DrishtiApplication.getInstance())
                                        .getAllJobRequestsForTag(PlanPeriodicEvaluationJob.TAG);

                                if (jobRequests != null && jobRequests.size() > 0) {
                                    for (JobRequest jobRequest: jobRequests) {
                                        if (jobRequest.getExtras() != null) {
                                            String actionCode = jobRequest.getExtras().getString(AllConstants.INTENT_KEY.ACTION_CODE, null);
                                            if (actionCode != null && action.getCode().equals(actionCode)) {
                                                JobManager.create(DrishtiApplication.getInstance()).cancel(jobRequest.getJobId());

                                                // Reschedule the job again using the timing
                                                scheduleActionJob(action);
                                            }
                                        }
                                    }
                                }

                                break;
                            }
                        }
                    }
                }
            }

        }
    }

    public void scheduleActionJob(Action action) {
        // TODO: Implement this
        throw new NotImplementedException("This is not implemented");
    }

    public boolean isValidDailyTriggerSchedule(Trigger trigger, PlanIntentServiceHelper planIntentServiceHelper) {
        Timing timing = trigger.getTimingTiming();
        List<DateTime> eventLists = timing.getEvent();

        for (DateTime dateTime: eventLists) {
            if (dateTime.isBefore(now(planIntentServiceHelper))) {
                TimingRepeat repeat = timing.getRepeat();
                if (repeat != null && repeat.getFrequency() == 1 && repeat.getPeriodUnit().equals(TimingRepeat.DurationCode.d)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected DateTime now(PlanIntentServiceHelper planIntentServiceHelper) {
        return timeNow != null ? timeNow : DateTime.now();
    }

    protected void setNow(DateTime timeNow) {
        this.timeNow = timeNow;
    }
}
