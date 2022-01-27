package org.smartregister.job;

import android.content.Context;
import android.content.Intent;

import com.evernote.android.job.Job;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseRobolectricUnitTest;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 01-12-2020.
 */
public class PlanIntentServiceJobTest extends BaseRobolectricUnitTest {

    @Mock
    private Context context;

    @Test
    public void onRunJobShouldStartServiceAndReturnSuccess() {
        PlanIntentServiceJob planIntentServiceJob = Mockito.spy(new PlanIntentServiceJob());
        Mockito.doReturn(context).when(planIntentServiceJob).getApplicationContext();

        // Assert the return value & execute method under test
        Assert.assertEquals(Job.Result.SUCCESS, planIntentServiceJob.onRunJob(null));

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(context).startService(intentArgumentCaptor.capture());

        // Assert the service started
        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertEquals("org.smartregister.sync.intent.PlanIntentService", intent.getComponent().getClassName());
    }
}