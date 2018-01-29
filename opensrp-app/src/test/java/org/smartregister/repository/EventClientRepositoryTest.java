package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
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
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.db.Column;
import org.smartregister.service.AlertService;
import org.smartregister.sync.ClientData;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    @Mock
    private Repository repository;

    @Mock
    private CommonFtsObject commonFtsObject;

    @Mock
    private AlertService alertService;

    @Mock
    private Context context;

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
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        Assert.assertNotNull(new EventClientRepository(repository));
    }

    @Test
    public void batchInsertClientsReturnsNotNull() throws Exception {
        Assert.assertNotNull(eventClientRepository.batchInsertClients(new JSONArray(ClientData.clientJsonArray)));
    }

    @Test
    public void batchInsertEventsReturnsNotNull() throws Exception {
        Assert.assertNotNull(eventClientRepository.batchInsertEvents(new JSONArray(ClientData.eventJsonArray), 0l));
    }

    @Test
    public void getEventsByServerVersionsReturnsNotNull() throws Exception {
        String query = "SELECT json FROM event WHERE serverVersion > 0 AND serverVersion <= 0 ORDER BY serverVersion";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(getEvetCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(0l, 0l));

    }

    @Test
    public void getEventsBySyncDateAndSyncStatusReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEvetCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(new Date(), "unsynced"));

    }

    @Test
    public void getEventsBySyncDateReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEvetCursor());
        Assert.assertNotNull(eventClientRepository.getEvents(new Date()));

    }

    @Test
    public void getUnsyncedEventsReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEvetCursor());
        Assert.assertNotNull(eventClientRepository.getUnSyncedEvents(100));

    }

    @Test
    public void getEventsByBaseEntityIdReturnsNotNull() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEvetCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByBaseEntityId(baseEntityId));

    }

    @Test
    public void getEventsByEventIdReturnsNotNull() throws Exception {

        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getEvetCursor());
        Assert.assertNotNull(eventClientRepository.getEventsByEventId("EventId"));

    }

    public MatrixCursor getEvetCursor() throws Exception {
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
    public void deleteClientCallsDelete1time() throws Exception {
        eventClientRepository.deleteClient(baseEntityId);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteEventCallsDelete1time() throws Exception {
        eventClientRepository.deleteEventsByBaseEntityId(baseEntityId, "eventType");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void assertConvertToJson() throws Exception {
        Assert.assertNull(eventClientRepository.convertToJson(null));
    }

    public MatrixCursor getCursorSyncStatus() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{"{\"json\":\"data\"}", syncStatus});
        return matrixCursor;
    }

    @Test
    public void assertGetUnSyncedReportsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnSyncedReports(1));
    }

    @Test
    public void assertGetUnValidatedEventFormSubmissionIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedEventFormSubmissionIds(1));
    }

    @Test
    public void assertGetUnValidatedReportFormSubmissionIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedReportFormSubmissionIds(1));
    }

    @Test
    public void assertGetUnValidatedClientBaseEntityIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(eventClientRepository.getUnValidatedClientBaseEntityIds(1));
    }

    @Test
    public void assertAddReportCallsDatabaseInsertAndUpdate() throws Exception {
        String jsonReport = "{\"reportType\":\"reportType\", \"formSubmissionId\":\"formSubmissionId\"}";
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        eventClientRepository.addReport(new JSONObject(jsonReport));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(null);
        eventClientRepository.addReport(new JSONObject(jsonReport));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));
    }

    @Test
    public void assertmarkReportsAsSyncedCallsDatabaseUpdate() throws Exception {
        String jsonReport = "{\"reportType\":\"reportType\", \"formSubmissionId\":\"formSubmissionId\"}";
        List<JSONObject> reports = new ArrayList<>();
        reports.add(new JSONObject(jsonReport));
        eventClientRepository.markReportsAsSynced(reports);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));
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