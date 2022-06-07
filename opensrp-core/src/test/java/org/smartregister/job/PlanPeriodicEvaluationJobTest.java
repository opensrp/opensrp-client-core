package org.smartregister.job;

import android.content.Context;
import android.content.Intent;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.Job;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 28-09-2021.
 */
public class PlanPeriodicEvaluationJobTest extends BaseRobolectricUnitTest {

    @Mock
    private Context context;

    @Test
    public void onRunDailyJobShouldStartServiceAndReturnSuccess() {
        PlanPeriodicEvaluationJob planIntentServiceJob = Mockito.spy(new PlanPeriodicEvaluationJob());
        ReflectionHelpers.setField(planIntentServiceJob, "mContextReference", new WeakReference<Context>(context));
        String action = "{\"identifier\":\"5df40280-ea48-4b08-ad9e-4cb937c31110\",\"prefix\":1,\"title\":\"Create PNC Supervisor Follow-up Tasks\",\"description\":\"Create Follow up tasks when PNC is not completed in 48 hours\",\"code\":\"PNC Task Follow Up\",\"timingPeriod\":{\"start\":\"2020-06-04\",\"end\":\"2025-10-01\"},\"reason\":\"Routine\",\"goalId\":\"PNC-Task-Follow-Up\",\"subjectCodableConcept\":{\"text\":\"Global.Task\"},\"trigger\":[{\"type\":\"named-event\",\"name\":\"plan-activation\"},{\"type\":\"periodic\",\"timingTiming\":{\"event\":[\"2020-03-04\"],\"repeat\":{\"frequency\":1,\"periodUnit\":\"d\",\"timeOfDay\":[\"04:00:00\"]}}}],\"condition\":[{\"kind\":\"applicability\",\"expression\":{\"expression\":\"Task.code.text.value.startsWith('PNC Day') and Task.code.text.value.endsWith('Visit') and Task.status.value = 'ready' and Task.authoredOn < today() - 2 'days'\"}}],\"definitionUri\":\"\",\"type\":\"create\"}";
        String planId = UUID.randomUUID().toString();
        String actionCode = "PNC Task Follow Up";
        String actionIdentifier = "5df40280-ea48-4b08-ad9e-4cb937c31110";

        Job.Params params = Mockito.mock(Job.Params.class);
        PersistableBundleCompat persistableBundleCompat = new PersistableBundleCompat();
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.ACTION, action);
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.ACTION_CODE, actionCode);
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.ACTION_IDENTIFIER, actionIdentifier);
        persistableBundleCompat.putString(AllConstants.INTENT_KEY.PLAN_ID, planId);

        Mockito.doReturn(persistableBundleCompat).when(params).getExtras();

        // Assert the return value & execute method under test
        Assert.assertEquals(DailyJob.DailyJobResult.SUCCESS, planIntentServiceJob.onRunDailyJob(params));

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(context).startService(intentArgumentCaptor.capture());

        // Assert the service started
        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertEquals("org.smartregister.sync.intent.PlanPeriodicPlanEvaluationService", intent.getComponent().getClassName());
        Assert.assertEquals(action, intent.getStringExtra(AllConstants.INTENT_KEY.ACTION));
        Assert.assertEquals(actionIdentifier, intent.getStringExtra(AllConstants.INTENT_KEY.ACTION_IDENTIFIER));
        Assert.assertEquals(actionCode, intent.getStringExtra(AllConstants.INTENT_KEY.ACTION_CODE));
        Assert.assertEquals(planId, intent.getStringExtra(AllConstants.INTENT_KEY.PLAN_ID));
    }

    @Test
    public void isPlanPeriodEvaluationJobShouldReturnFalseWhenWrongTagSent() {
        Assert.assertFalse(PlanPeriodicEvaluationJob.isPlanPeriodEvaluationJob(PlanIntentServiceJob.TAG));
    }

    @Test
    public void isPlanPeriodEvaluationJobShouldReturnTrueWhenScheduleAdhocTagGiven() {
        Assert.assertTrue(PlanPeriodicEvaluationJob.isPlanPeriodEvaluationJob(PlanPeriodicEvaluationJob.SCHEDULE_ADHOC_TAG));
    }

    @Test
    public void isPlanPeriodEvaluationJobShouldReturnTrueWhenTagStartsWithPrefixString() {
        Assert.assertTrue(PlanPeriodicEvaluationJob.isPlanPeriodEvaluationJob(PlanPeriodicEvaluationJob.PREFIX_TAG + "-actionCode-actionIdentifier"));
    }

    @Test
    public void generateJobTagShouldReturnTagStartingWithPrefixAndContainingActionIdentifierAndCode() {
        String actionIdentifier = "5df40280-ea48-4b08-ad9e-4cb937c31110";
        String actionCode = "PNC Task Follow Up";

        String jobTag = PlanPeriodicEvaluationJob.generateJobTag(actionIdentifier, actionCode);

        Assert.assertTrue(jobTag.startsWith(PlanPeriodicEvaluationJob.PREFIX_TAG));
        Assert.assertTrue(jobTag.contains(actionIdentifier));
        Assert.assertTrue(jobTag.contains(actionCode));
    }
}