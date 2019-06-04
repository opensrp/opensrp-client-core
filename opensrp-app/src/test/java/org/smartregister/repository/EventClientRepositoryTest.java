package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.Column;
import org.smartregister.sync.ClientData;
import org.smartregister.sync.intent.P2pProcessRecordsService;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by onaio on 29/08/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class})
public class EventClientRepositoryTest extends BaseUnitTest {

    @InjectMocks
    private EventClientRepository eventClientRepository;
    private String baseEntityId = "baseEntityId";
    private String syncStatus = "syncStatus";
    private String eventType = "eventType";
    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        eventClientRepository = new EventClientRepository(repository);
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() {
        Assert.assertNotNull(new EventClientRepository(repository));
    }

    @Test
    public void batchInsertClientsReturnsNotNull() throws Exception {
        Assert.assertTrue(eventClientRepository.batchInsertClients(new JSONArray(ClientData.clientJsonArray)));
    }

    @Test
    public void batchInsertEventsReturnsNotNull() throws Exception {
        Assert.assertTrue(eventClientRepository.batchInsertEvents(new JSONArray(ClientData.eventJsonArray), 0l));
    }

    @Test
    public void getEventsByServerVersionsReturnsNotNull() throws Exception {
        String query = "SELECT json FROM event WHERE serverVersion > 0 AND serverVersion <= 0 ORDER BY serverVersion";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(0l, 0l));

    }

    @Test
    public void getEventsBySyncDateAndSyncStatusReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(new Date(), "unsynced"));

    }

    @Test
    public void getEventsBySyncDateReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(new Date()));

    }

    @Test
    public void fetchEventClientsByEventVersion() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.fetchEventClientsByEventTypes(Collections.singletonList("Registration")));
    }

    @Test
    public void testFetchEventClients() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.fetchEventClients(Collections.singletonList("123")));
    }

    @Test
    public void testFetchEventClientsByMinMaxTimestamp() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.fetchEventClients(10000l, 20000l));
    }

    @Test
    public void getUnsyncedEventsReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getUnSyncedEvents(100));

    }

    @Test
    public void getEventsByBaseEntityIdReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityId(baseEntityId));

    }

    @Test
    public void getEventsByEventIdReturnsNotNull() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByEventId("EventId"));

    }

    @Test
    public void testGetEventsByBaseEntityIdAndEventTypeReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityIdAndEventType(baseEntityId, eventType));

    }

    @Test
    public void testGetEventsByBaseEntityIdsAndSyncStatusReturnsNotNull() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEventCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityIdsAndSyncStatus(syncStatus, Arrays.asList(baseEntityId)));

    }

    public MatrixCursor getEventCursor() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json", "timestamp"});
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            matrixCursor.addRow(new String[]{eventArray.getJSONObject(i).toString(), "1985-07-24T00:00:00.000Z"});
        }
        return matrixCursor;
    }

    @Test
    public void getEventsByFormSubmissionIdReturnsNotNull() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json"});
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            matrixCursor.addRow(new String[]{eventArray.getJSONObject(i).toString()});
        }
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(eventClientRepository.getEventsByFormSubmissionId("FormSubmissionID"));

    }

    @Test
    public void markEventsAsSyncedCallsUpdateAsMAnyTimesAsEventsAndClientsPassedIntoMethod() throws Exception {

        HashMap<String, Object> syncedObjects = new HashMap<String, Object>();
        syncedObjects.put("clients", getClientList());
        syncedObjects.put("events", getEventList());
        eventClientRepository.markEventsAsSynced(syncedObjects);
        Mockito.verify(sqliteDatabase, Mockito.times(getEventList().size() + getClientList().size())).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void markAllAsUnSyncedCallsUpdate2timesFor2Objects() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});
        Mockito.when(sqliteDatabase.rawQuery("select baseEntityId,syncStatus from client", null)).thenReturn(matrixCursor);
        Mockito.when(sqliteDatabase.rawQuery("select baseEntityId,syncStatus from event", null)).thenReturn(matrixCursor);
        eventClientRepository.markAllAsUnSynced();
        Mockito.verify(sqliteDatabase, Mockito.times(2)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void addorUpdateClientCallsInsert1timeForNewClients() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
//        matrixCursor.addRow(new String []{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addorUpdateClient(baseEntityId, getClientList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));

    }

    @Test
    public void addorUpdateClientCallsUpdate1timeForOldClients() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addorUpdateClient(baseEntityId, getClientList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    public ArrayList<JSONObject> getClientList() throws Exception {
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for (int i = 0; i < clientArray.length(); i++) {
            clientList.add(clientArray.getJSONObject(i));
        }
        return clientList;
    }

    @Test
    public void addorUpdateEventCallsInsert1timeForNewEvents() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addEvent(baseEntityId, getEventList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));

    }

    @Test
    public void addorUpdateEventCallsUpdate1timeForOldEvents() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{baseEntityId, syncStatus});

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        eventClientRepository.addEvent(baseEntityId, getEventList().get(0));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    public ArrayList<JSONObject> getEventList() throws Exception {
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for (int i = 0; i < eventArray.length(); i++) {
            eventList.add(eventArray.getJSONObject(i));
        }
        return eventList;
    }

    @Test
    public void deleteClientCallsDelete1time() {
        eventClientRepository.deleteClient(baseEntityId);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteEventCallsDelete1time() {
        eventClientRepository.deleteEventsByBaseEntityId(baseEntityId, "eventType");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void fetchEventClientsByRowId() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"rowid", "json"});
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        for (int i = 0; i < eventArray.length(); i++) {
            matrixCursor.addRow(new String[]{(i+1) + "", eventArray.getJSONObject(i).toString()});
        }

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(Object[].class))).thenReturn(matrixCursor);
        P2pProcessRecordsService.EventClientQueryResult eventClientQueryResult = eventClientRepository.fetchEventClientsByRowId(0);

        Assert.assertEquals(eventArray.length(), eventClientQueryResult.getMaxRowId());
    }

    @Test
    public void assertConvertToJson() {
        Assert.assertNull(eventClientRepository.convertToJson(null));
    }

    public MatrixCursor getCursorSyncStatus() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{"{\"json\":\"data\"}", syncStatus});
        return matrixCursor;
    }

    @Test
    public void assertGetUnValidatedEventFormSubmissionIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedEventFormSubmissionIds(1));
    }


    @Test
    public void assertGetUnValidatedClientBaseEntityIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedClientBaseEntityIds(1));
    }


    @Test
    public void assertcreateTableCallsExecSql() {
        int count = 1; //first one to create table, rest for create index
        for (Column cc : EventClientRepository.client_column.values()) {
            if (cc.column().index()) {
                count++;
            }
        }
        EventClientRepository.createTable(sqliteDatabase, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        Mockito.verify(sqliteDatabase, Mockito.times(count)).execSQL(Mockito.anyString());
    }

}