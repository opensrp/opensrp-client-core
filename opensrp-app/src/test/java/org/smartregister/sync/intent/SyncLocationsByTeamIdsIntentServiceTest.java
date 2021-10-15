package org.smartregister.sync.intent;

import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.shadows.ShadowLocationServiceHelper;
import org.smartregister.sync.helper.LocationServiceHelper;

/**
 * Created by Vincent Karuri on 12/01/2021
 */

public class SyncLocationsByTeamIdsIntentServiceTest extends BaseUnitTest {

    @Config(shadows = {ShadowLocationServiceHelper.class})
    @Test
    public void testOnHandleIntentShouldSyncLocations() throws Exception {
        LocationServiceHelper locationServiceHelper = Mockito.mock(LocationServiceHelper.class);
        ShadowLocationServiceHelper.setInstance(locationServiceHelper);
        SyncLocationsByTeamIdsIntentWorker syncLocationsByTeamIdsIntentService = new SyncLocationsByTeamIdsIntentWorker();
        syncLocationsByTeamIdsIntentService.onRunWork();
        Mockito.verify(locationServiceHelper).fetchOpenMrsLocationsByTeamIds();
    }
}