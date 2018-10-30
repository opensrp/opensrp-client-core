package org.smartregister.job;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;

/**
 * Created by ndegwamartin on 10/09/2018.
 */
public class PullUniqueIdsServiceJobTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private ComponentName componentName;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnRunJobStartsCorrectService() {

        PullUniqueIdsServiceJob pullUniqueIdsServiceJob = new PullUniqueIdsServiceJob();
        PullUniqueIdsServiceJob pullUniqueIdsServiceJobSpy = Mockito.spy(pullUniqueIdsServiceJob);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        Mockito.doReturn(context).when(pullUniqueIdsServiceJobSpy).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));

        pullUniqueIdsServiceJobSpy.onRunJob(null);

        Mockito.verify(context).startService(intent.capture());

        Assert.assertEquals("org.smartregister.sync.intent.PullUniqueIdsIntentService", intent.getValue().getComponent().getClassName());
    }
}
