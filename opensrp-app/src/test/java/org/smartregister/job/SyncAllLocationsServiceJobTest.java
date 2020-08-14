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

import static junit.framework.Assert.assertEquals;

public class SyncAllLocationsServiceJobTest extends BaseUnitTest {

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
        SyncAllLocationsServiceJob syncAllLocationsServiceJob = new SyncAllLocationsServiceJob();
        SyncAllLocationsServiceJob spy = Mockito.spy(syncAllLocationsServiceJob);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        Mockito.doReturn(context).when(spy).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));

        spy.onRunJob(null);
        Mockito.verify(context).startService(intent.capture());

        assertEquals("org.smartregister.sync.intent.SyncAllLocationsIntentService", intent.getValue().getComponent().getClassName());
    }
}
