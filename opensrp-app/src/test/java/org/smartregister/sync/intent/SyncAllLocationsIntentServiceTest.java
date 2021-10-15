package org.smartregister.sync.intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.LocationServiceHelper;

/**
 * Created by Vincent Karuri on 08/06/2021
 */
public class SyncAllLocationsIntentServiceTest extends BaseUnitTest {

    private SyncAllLocationsIntentWorker syncAllLocationsIntentService;

    @Before
    public void setUp() throws Exception {
        syncAllLocationsIntentService = new SyncAllLocationsIntentWorker();
    }

    @Test
    public void onHandleIntent() throws Exception {
        LocationServiceHelper locationServiceHelper = Mockito.mock(LocationServiceHelper.class);
        Whitebox.setInternalState(LocationServiceHelper.class, "instance", locationServiceHelper);

            syncAllLocationsIntentService.onRunWork();
        Mockito.verify(locationServiceHelper).fetchAllLocations();
        Whitebox.setInternalState(LocationServiceHelper.class, "instance", (LocationServiceHelper) null);
    }
}