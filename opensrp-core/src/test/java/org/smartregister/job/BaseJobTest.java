package org.smartregister.job;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.evernote.android.job.JobRequest;
import com.evernote.android.job.ShadowJobManager;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 10-11-2020.
 */
public class BaseJobTest extends BaseRobolectricUnitTest {

    @Test
    public void scheduleJobShouldAddJobToJobManagerAndRescheduleWhenLessThan15Minutes() {
        ArgumentCaptor<JobRequest> jobRequestArgumentCaptor = ArgumentCaptor.forClass(JobRequest.class);

        BaseJob.scheduleJob("my-job", TimeUnit.MINUTES.toMillis(10), TimeUnit.MINUTES.toMillis(5));

        Mockito.verify(ShadowJobManager.mockJobManager).schedule(jobRequestArgumentCaptor.capture());

        JobRequest jobRequest = jobRequestArgumentCaptor.getValue();
        Assert.assertTrue(jobRequest.getExtras()
                .getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false));
    }

    @Test
    public void getApplicationContextShouldReturnSameContextInstance() {
        BaseJob baseJob = Mockito.mock(BaseJob.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(baseJob, "mContextReference", new WeakReference<Context>(ApplicationProvider.getApplicationContext()));
        Assert.assertEquals(ApplicationProvider.getApplicationContext(), baseJob.getApplicationContext());
    }

    @Test
    public void startIntentServiceInvokesStartServiceWithCorrectParam() {
        BaseJob baseJob = Mockito.mock(BaseJob.class, Mockito.CALLS_REAL_METHODS);
        Context context = Mockito.mock(Context.class);

        Mockito.doReturn(context).when(baseJob).getApplicationContext();

        baseJob.startIntentService(Mockito.mock(Intent.class));

        Mockito.verify(context).startService(ArgumentMatchers.any(Intent.class));
    }
}