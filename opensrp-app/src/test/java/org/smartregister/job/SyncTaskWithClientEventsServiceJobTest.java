package org.smartregister.job;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.intent.SyncClientEventsPerTaskIntentService;

import static org.junit.Assert.assertEquals;

public class SyncTaskWithClientEventsServiceJobTest extends BaseUnitTest {
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

        SyncTaskWithClientEventsServiceJob syncTaskWithClientEventsServiceJob = new SyncTaskWithClientEventsServiceJob(SyncClientEventsPerTaskIntentService.class);
        SyncTaskWithClientEventsServiceJob syncTaskWithClientEventsServiceSpy = Mockito.spy(syncTaskWithClientEventsServiceJob);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        Mockito.doReturn(context).when(syncTaskWithClientEventsServiceSpy).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));
        syncTaskWithClientEventsServiceSpy.onRunJob(null);

        Mockito.verify(context).startService(intent.capture());

        assertEquals("org.smartregister.sync.intent.SyncClientEventsPerTaskIntentService", intent.getValue().getComponent().getClassName());
    }
}