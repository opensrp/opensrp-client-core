package org.smartregister.sync.intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-08-2020.
 */
public class DocumentConfigurationIntentServiceTest extends BaseRobolectricUnitTest {

    private DocumentConfigurationIntentService documentConfigurationIntentService;

    @Before
    public void setUp() throws Exception {
        documentConfigurationIntentService = Robolectric.buildIntentService(DocumentConfigurationIntentService.class)
                .create()
                .get();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onStartCommandShouldInstantiateVariables() {
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "httpAgent"));
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "manifestRepository"));
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "clientFormRepository"));
        Assert.assertNull(ReflectionHelpers.getField(documentConfigurationIntentService, "configuration"));

        documentConfigurationIntentService.onStartCommand(null, 0, 900);


        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "httpAgent"));
        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "manifestRepository"));
        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "clientFormRepository"));
        Assert.assertNotNull(ReflectionHelpers.getField(documentConfigurationIntentService, "configuration"));
    }

    @Test
    public void onHandleIntent() {
    }
}