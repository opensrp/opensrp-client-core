package org.smartregister.sync.intent;

import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.service.HTTPAgent;

/**
 * Created by Vincent Karuri on 22/09/2020
 */
public class ValidateIntentServiceTest extends BaseUnitTest {

    private ValidateIntentService validateIntentService;

    @Mock
    private HTTPAgent httpAgent;

    @Before
    public void setUp() throws Exception {
        validateIntentService = Robolectric.buildIntentService(ValidateIntentService.class).get();
    }

    @Test
    public void testOnHandleIntent() {
        ReflectionHelpers.setField(validateIntentService, "context", RuntimeEnvironment.application);
        ReflectionHelpers.setField(validateIntentService, "httpAgent", httpAgent);
        validateIntentService.onHandleIntent(new Intent());
    }
}