package org.smartregister.task;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.login.interactor.TestExecutorService;

import java.util.concurrent.Executors;

/**
 * Created by Vincent Karuri on 30/03/2021
 */
public class SaveTeamLocationsTaskTest extends BaseUnitTest {
    @Mock
    private LocationHelper locationHelper;

    @Test
    public void testDoInBackgroundShouldSaveTeamLocations() {

        try (MockedStatic<LocationHelper> locationHelperMockedStatic = Mockito.mockStatic(LocationHelper.class);
             MockedStatic<Executors> executor = Mockito.mockStatic(Executors.class)) {
            executor.when(Executors::newSingleThreadExecutor).thenReturn(new TestExecutorService());

            locationHelperMockedStatic.when(LocationHelper::getInstance).thenReturn(locationHelper);

            new SaveTeamLocationsTask().execute();

            Mockito.verify(locationHelper).locationIdsFromHierarchy();
        }
    }
}