package org.smartregister.sync.helper;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.ShadowJobManager;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Timing;
import org.smartregister.domain.TimingRepeat;
import org.smartregister.domain.Trigger;
import org.smartregister.job.PlanPeriodicEvaluationJob;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 28-09-2021.
 */
public class PeriodicTriggerEvaluationHelperTest extends BaseRobolectricUnitTest {

    private PeriodicTriggerEvaluationHelper periodicTriggerEvaluationHelper;
    private String planDefinitionString = "{\"identifier\":\"2d12e224-401d-4d23-80fd-7b0f37e56fc1\",\"version\":\"1\",\"name\":\"Goldsmith_Supervisor_Template\",\"status\":\"active\",\"date\":\"2021-03-11\",\"effectivePeriod\":{\"start\":\"2020-03-11\",\"end\":\"2025-03-11\"},\"useContext\":[{\"code\":\"interventionType\",\"valueCodableConcept\":\"Linked-PNC\"},{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"internal\"}],\"jurisdiction\":[{\"code\":\"ac7ba751-35e8-4b46-9e53-3cbaad193697\"}],\"goal\":[{\"id\":\"Day_2_Visit\",\"description\":\"Complete the Day 2 Visit form for each child.\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of children with completed Day 2 Visit form completed.\",\"detail\":{\"detailQuantity\":{\"value\":80,\"comparator\":\"&amp;amp;gt;=\",\"unit\":\"Percent\"}},\"due\":\"2021-10-19\"}]}],\"action\":[{\"identifier\":\"5df40280-ea48-4b08-ad9e-4cb937c31110\",\"prefix\":1,\"title\":\"Create PNC Supervisor Follow-up Tasks\",\"description\":\"Create Follow up tasks when PNC is not completed in 48 hours\",\"code\":\"PNC Task Follow Up\",\"timingPeriod\":{\"start\":\"2020-06-04\",\"end\":\"2025-10-01\"},\"reason\":\"Routine\",\"goalId\":\"PNC-Task-Follow-Up\",\"subjectCodableConcept\":{\"text\":\"Global.Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"periodic\",\"timingTiming\":{\"event\":[\"2020-03-04\"],\"repeat\":{\"frequency\":1,\"periodUnit\":\"d\",\"timeOfDay\":[\"04:00:00\"]}}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"expression\":\"Task.code.text.value.startsWith('PNC Day') and Task.code.text.value.endsWith('Visit') and Task.status.value = 'ready' and Task.authoredOn < today() - 2 'days'\"}}],\"definitionUri\":\"\",\"type\":\"create\"},{\"identifier\":\"19deb463-7728-4bad-b62a-ccc6a7abb286\",\"prefix\":2,\"title\":\"Create ANC Supervisor Follow-up Tasks\",\"description\":\"Create Follow up tasks when an ANC task is not completed in 48 hours\",\"code\":\"ANC Task Follow Up\",\"timingPeriod\":{\"start\":\"2020-06-04\",\"end\":\"2025-10-01\"},\"reason\":\"Routine\",\"goalId\":\"ANC-Task-Follow-Up\",\"subjectCodableConcept\":{\"text\":\"Global.Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"periodic\",\"timingTiming\":{\"event\":[\"2020-03-04\"],\"repeat\":{\"frequency\":1,\"periodUnit\":\"d\",\"timeOfDay\":[\"04:00:00\"]}}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"expression\":\"Task.code.text.value.startsWith('ANC Contact') and Task.status.value = 'ready' and Task.authoredOn < today() - 2 'days'\"}}],\"definitionUri\":\"\",\"type\":\"create\"}],\"experimental\":false,\"serverVersion\":19}";

    @Before
    public void setUp() throws Exception {
        ShadowJobManager.resetJobManagerInstance();
        periodicTriggerEvaluationHelper = Mockito.spy(new PeriodicTriggerEvaluationHelper());
    }

    @Test
    public void reschedulePeriodicPlanEvaluationsShouldCallSchedulActionJobWhenGivenPlanDefinitionList() {
        Gson gson = PlanIntentServiceHelper.gson;
        PlanDefinition planDefinition = gson.fromJson(planDefinitionString, PlanDefinition.class);
        ArrayList<PlanDefinition> planDefinitionsList = new ArrayList();
        planDefinitionsList.add(planDefinition);

        periodicTriggerEvaluationHelper.reschedulePeriodicPlanEvaluations(planDefinitionsList);

        Mockito.verify(periodicTriggerEvaluationHelper, Mockito.times(2)).scheduleActionJob(Mockito.any(), Mockito.any(), Mockito.eq("2d12e224-401d-4d23-80fd-7b0f37e56fc1"));
    }

    @Test
    public void isValidDailyTriggerScheduleShouldReturnTrueWhenRepeatFrequencyIs1AndPeriodUnitIsDay() {
        Timing timing = new Timing();
        TimingRepeat timingRepeat = new TimingRepeat();
        timingRepeat.setFrequency(1);
        timingRepeat.setPeriodUnit(TimingRepeat.DurationCode.d);
        ArrayList<Time> times = new ArrayList<>();
        times.add(new Time(4, 0, 0));
        timingRepeat.setTimeOfDay(times);
        timing.setRepeat(timingRepeat);

        ArrayList<DateTime> eventsList = new ArrayList<>();
        eventsList.add(new DateTime(2020, 3, 4, 4, 23, 45));
        timing.setEvent(eventsList);

        Trigger trigger = new Trigger("periodic", null, null, timing);

        Assert.assertTrue(periodicTriggerEvaluationHelper.isValidDailyTriggerSchedule(trigger));
    }

    @Test
    public void cancelJobsForActionShouldCallJobManagerCancelAllAndReturnJobsCancelled() {
        String actionIdentifier = "5df40280-ea48-4b08-ad9e-4cb937c31110";
        String actionCode = "PNC Task Follow Up";
        String jobTag = PlanPeriodicEvaluationJob.generateJobTag(actionIdentifier, actionCode);

        JobManager jobManager = ShadowJobManager.createMockJobManager();
        Mockito.doReturn(5).when(jobManager).cancelAllForTag(jobTag);

        int cancelledJobs = periodicTriggerEvaluationHelper.cancelJobsForAction(actionIdentifier, actionCode);

        Mockito.verify(jobManager).cancelAllForTag(jobTag);
        Assert.assertEquals(5, cancelledJobs);
    }

    @Test
    public void nowShouldReturnTimeNowWhenTimeNowIsNotSet() {
        DateTime expectedDateTime = DateTime.now();

        DateTime actualTime = periodicTriggerEvaluationHelper.now();

        // Error margin is 5 seconds
        Assert.assertTrue((actualTime.getMillis() - expectedDateTime.getMillis()) < 5000);
    }

    @Test
    public void setNow() {
        DateTime dateTime = new DateTime();
        dateTime.withYear(2000);

        periodicTriggerEvaluationHelper.setNow(dateTime);

        Assert.assertEquals(dateTime, periodicTriggerEvaluationHelper.now());
    }
}