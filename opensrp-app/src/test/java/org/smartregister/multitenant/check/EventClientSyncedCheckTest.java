package org.smartregister.multitenant.check;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.view.activity.DrishtiApplication;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-04-2020.
 */
public class EventClientSyncedCheckTest extends BaseRobolectricUnitTest {

    private EventClientSyncedCheck eventClientSyncedCheck;

    @Before
    public void setUp() throws Exception {
        eventClientSyncedCheck = Mockito.spy(new EventClientSyncedCheck());
    }

    @Test
    public void isCheckOkShouldCallIsEventsClientSynced() {
        Mockito.doReturn(false).when(eventClientSyncedCheck).isEventsClientSynced(Mockito.eq(DrishtiApplication.getInstance()));
        eventClientSyncedCheck.isCheckOk(DrishtiApplication.getInstance());

        Mockito.verify(eventClientSyncedCheck).isEventsClientSynced(Mockito.eq(DrishtiApplication.getInstance()));
    }

    @Test
    public void isEventsClientSynced() {
        EventClientRepository eventClientRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getEventClientRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "eventClientRepository", eventClientRepository);

        Mockito.doReturn(0).when(eventClientRepository).getUnSyncedEventsCount();

        assertTrue(eventClientSyncedCheck.isEventsClientSynced(DrishtiApplication.getInstance()));
        Mockito.verify(eventClientRepository).getUnSyncedEventsCount();
    }
}