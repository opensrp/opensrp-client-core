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
import org.smartregister.sync.intent.DocumentConfigurationIntentService;

/**
 * Created by cozej4 on 2020-04-16.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationServiceJobTest extends BaseUnitTest {

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

        DocumentConfigurationServiceJob documentConfigurationServiceJob = new DocumentConfigurationServiceJob(DocumentConfigurationIntentService.class);
        DocumentConfigurationServiceJob documentConfigurationServiceJobSpy = Mockito.spy(documentConfigurationServiceJob);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        Mockito.doReturn(context).when(documentConfigurationServiceJobSpy).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));

        documentConfigurationServiceJobSpy.onRunJob(null);

        Mockito.verify(context).startService(intent.capture());

        Assert.assertEquals("org.smartregister.sync.intent.DocumentConfigurationIntentService", intent.getValue().getComponent().getClassName());
    }
}