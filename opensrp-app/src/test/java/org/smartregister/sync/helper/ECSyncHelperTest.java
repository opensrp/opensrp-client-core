package org.smartregister.sync.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ndegwamartin on 07/09/2018.
 */
@PrepareForTest(CoreLibrary.class)
public class ECSyncHelperTest extends BaseUnitTest {

    private ECSyncHelper syncHelper;
    private static final String EVENT_CLIENT_REPOSITORY = "eventClientRepository";

    private static final long DUMMY_LONG = 1000l;

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        syncHelper = new ECSyncHelper(RuntimeEnvironment.application, eventClientRepository);
        Whitebox.setInternalState(syncHelper, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        Whitebox.setInternalState(syncHelper, allSharedPreferences, allSharedPreferences);
    }

    @Test
    public void testSaveAllClientsAndEventsInvokesBatchSaveWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("clients", clientsArray);
        object.put("events", eventsArray);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(object);

        verify(syncHelperSpy).batchSave(eventsArray, clientsArray);
        assertTrue(result);

        result = syncHelperSpy.saveAllClientsAndEvents(object);
        assertTrue(result);
    }

    @Test
    public void testSaveAllClientsAndEventsShouldReturnFalseForNullParam() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(null);
        assertFalse(result);
    }

    @Test
    public void testAllEventClientsInvokesRepositoryFetchEventClientsWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        List<EventClient> result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        assertNull(result);

        verify(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    public void testBatchSaveInvokesBatchInsertClientsWithCorrectParams() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        JSONObject object = new JSONObject();

        JSONArray clientsArray = new JSONArray();
        clientsArray.put("Some Client");

        JSONArray eventsArray = new JSONArray();
        eventsArray.put("Some Event");

        object.put("clients", clientsArray);
        object.put("events", eventsArray);

        syncHelperSpy.batchSave(eventsArray, clientsArray);

        verify(eventClientRepository).batchInsertClients(clientsArray);
    }

    @Test
    public void testUpdateLastSyncTimeStamp() {
        long expectedTimeStamp = 1604387699l;
        syncHelper.updateLastSyncTimeStamp(expectedTimeStamp);
        verify(allSharedPreferences).saveLastSyncDate(expectedTimeStamp);
    }

    @Test
    public void testGetLastCheckTimeStamp() {
        syncHelper.getLastCheckTimeStamp();
        verify(allSharedPreferences).fetchLastCheckTimeStamp();
    }

    @Test
    public void testGetEventsByLastSyncDateAndSyncStatus() {
        Date lastSyncDate = new Date();
        String syncStatus = BaseRepository.TYPE_Synced;
        Event event = new Event();
        event.setBaseEntityId("baseEntityid1");
        EventClient eventClient = new EventClient(event);
        when(eventClientRepository.fetchEventClients(lastSyncDate, syncStatus)).thenReturn(Collections.singletonList(eventClient));
        List<EventClient> actualevents = syncHelper.getEvents(lastSyncDate, syncStatus);
        assertEquals(1, actualevents.size());
        assertEquals("baseEntityid1", actualevents.get(0).getEvent().getBaseEntityId());
    }

    @Test
    public void testGetEventsByLastSyncDateAndSyncStatusReturnsEmptyArrayWhenExceptionIsThrown() {
        Date lastSyncDate = new Date();
        String syncStatus = BaseRepository.TYPE_Synced;

        doThrow(new NullPointerException()).when(eventClientRepository).fetchEventClients(lastSyncDate, syncStatus);
        List<EventClient> actualevents = syncHelper.getEvents(lastSyncDate, syncStatus);
        assertNotNull(actualevents);
        assertEquals(0, actualevents.size());
    }


    @Test
    public void testGetEventsByFormSubmissionIds() {
        String formsubmissionid = "formsubmissionid1";
        List<String> formSubmissionIds = Collections.singletonList(formsubmissionid);
        String baseEntityId = "baseEntityid1";
        Event event = new Event();
        event.setBaseEntityId(baseEntityId);
        EventClient eventClient = new EventClient(event);
        when(eventClientRepository.fetchEventClients(formSubmissionIds)).thenReturn(Collections.singletonList(eventClient));
        List<EventClient> actualevents = syncHelper.getEvents(formSubmissionIds);
        assertEquals(1, actualevents.size());
        assertEquals(baseEntityId, actualevents.get(0).getEvent().getBaseEntityId());
    }

    @Test
    public void testGetEventsByFormSubmissionIdsReturnsEmptyArrayWhenExceptionIsThrown() {
        String formsubmissionid = "formsubmissionid1";
        List<String> formSubmissionIds = Collections.singletonList(formsubmissionid);
        doThrow(new NullPointerException()).when(eventClientRepository).fetchEventClients(formSubmissionIds);
        List<EventClient> actualevents = syncHelper.getEvents(formSubmissionIds);
        assertNotNull(actualevents);
        assertEquals(0, actualevents.size());
    }


}
