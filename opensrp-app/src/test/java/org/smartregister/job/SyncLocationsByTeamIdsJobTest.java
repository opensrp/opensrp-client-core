package org.smartregister.job;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;

public class SyncLocationsByTeamIdsJobTest extends BaseUnitTest {

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

        SyncLocationsByTeamIdsJob syncLocationsByTeamIdsJob = new SyncLocationsByTeamIdsJob();
        SyncLocationsByTeamIdsJob syncLocationsByTeamIdsJobSpy = Mockito.spy(syncLocationsByTeamIdsJob);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        Mockito.doReturn(context).when(syncLocationsByTeamIdsJobSpy).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));

        syncLocationsByTeamIdsJobSpy.onRunJob(null);

        Mockito.verify(context).startService(intent.capture());

        Assert.assertEquals("org.smartregister.sync.intent.SyncLocationsByTeamIdsIntentService", intent.getValue().getComponent().getClassName());

    }
}
