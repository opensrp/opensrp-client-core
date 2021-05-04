package org.smartregister.sync.intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.LocationServiceHelper;

/**
 * Created by Vincent Karuri on 04/05/2021
 */
public class SyncAllLocationsIntentServiceTest extends BaseUnitTest {

    private SyncAllLocationsIntentService syncAllLocationsIntentService;

    @Before
    public void setUp() {
        syncAllLocationsIntentService = new SyncAllLocationsIntentService();
    }

    @Test
    public void testOnHandleIntentShouldSyncAllLocations() throws Exception {
        LocationServiceHelper locationServiceHelper = Mockito.mock(LocationServiceHelper.class);
        Whitebox.setInternalState(LocationServiceHelper.class, "instance", locationServiceHelper);
        Whitebox.invokeMethod(syncAllLocationsIntentService, "onHandleIntent", null);
        Mockito.verify(locationServiceHelper).fetchAllLocations();
        LocationServiceHelper.destroyInstance();
    }
}