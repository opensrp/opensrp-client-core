package org.smartregister.sync.helper;

import junit.framework.Assert;

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
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.List;

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

        Mockito.verify(syncHelperSpy).batchSave(eventsArray, clientsArray);
        Assert.assertTrue(result);

        result = syncHelperSpy.saveAllClientsAndEvents(object);
        Assert.assertTrue(result);
    }

    @Test
    public void testSaveAllClientsAndEventsShouldReturnFalseForNullParam() throws Exception {
        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        boolean result = syncHelperSpy.saveAllClientsAndEvents(null);
        Assert.assertFalse(result);
    }

    @Test
    public void testAllEventClientsInvokesRepositoryFetchEventClientsWithCorrectParams() {

        ECSyncHelper syncHelperSpy = Mockito.spy(syncHelper);

        Mockito.doReturn(null).when(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        List<EventClient> result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        Assert.assertNull(result);

        Mockito.verify(eventClientRepository).fetchEventClients(DUMMY_LONG, DUMMY_LONG);

        //On Exception
        EventClientRepository eventClientRepository = null;
        Whitebox.setInternalState(syncHelperSpy, EVENT_CLIENT_REPOSITORY, eventClientRepository);
        result = syncHelperSpy.allEventClients(DUMMY_LONG, DUMMY_LONG);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
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

        Mockito.verify(eventClientRepository).batchInsertClients(clientsArray);
    }

}
