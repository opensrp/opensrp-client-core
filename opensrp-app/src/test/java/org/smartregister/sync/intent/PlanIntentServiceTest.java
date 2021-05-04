package org.smartregister.sync.intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.PlanIntentServiceHelper;

import static org.junit.Assert.*;

/**
 * Created by Vincent Karuri on 04/05/2021
 */
public class PlanIntentServiceTest extends BaseUnitTest {

    private PlanIntentService planIntentService;

    @Before
    public void setUp() throws Exception {
        planIntentService = new PlanIntentService();
    }

    @Test
    public void testOnHandleIntentShouldSyncPlans() throws Exception {
        PlanIntentServiceHelper planIntentServiceHelper = Mockito.mock(PlanIntentServiceHelper.class);
        Whitebox.setInternalState(PlanIntentServiceHelper.class, "instance", planIntentServiceHelper);
        Whitebox.invokeMethod(planIntentService, "onHandleIntent", null);
        Mockito.verify(planIntentServiceHelper).syncPlans();
        PlanIntentServiceHelper.destroyInstance();
    }
}