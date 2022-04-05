package org.smartregister.repository;


import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.Client;
import org.smartregister.domain.ClientRelationship;
import org.smartregister.sync.ClientData;
import org.smartregister.util.JsonFormUtils;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 7/14/20.
 */
public class ClientRelationshipRepositoryTest extends BaseRobolectricUnitTest {

    @Mock
    private SQLiteDatabase database;

    @Test
    public void testCreateTableShouldCreateTableAndIndex() {
        ClientRelationshipRepository.createTable(database);
        verify(database).execSQL(ClientRelationshipRepository.CREATE_TABLE);
        verify(database).execSQL(ClientRelationshipRepository.CREATE_BASE_ENTITY_ID_INDEX);
    }

    @Test
    public void findClientByRelationshipShouldReturnClients() throws JSONException {
        ClientRelationshipRepository repository = spy(new ClientRelationshipRepository());
        when(repository.getReadableDatabase()).thenReturn(database);
        when(database.rawQuery(anyString(), any())).thenReturn(getClients());
        List<Client> clients = repository.findClientByRelationship("family", "12323");
        assertEquals(1, clients.size());
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", clients.get(0).getBaseEntityId());
    }

    private Cursor getClients() throws JSONException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{EventClientRepository.client_column.json.name()});
        JSONArray jsonObject = new JSONArray(ClientData.clientJsonArray);
        matrixCursor.addRow(new Object[]{jsonObject.getJSONObject(0)});
        return matrixCursor;
    }

    @Test
    public void testRawQueryShouldReturnClientDetails() throws JSONException {
        ClientRelationshipRepository repository = spy(ClientRelationshipRepository.class);
        Cursor cursor = spy(getClients());
        when(database.rawQuery(anyString(), any()))
                .thenReturn(cursor);
        String jsonFieldColumn = "json";
        String query = String.format("SELECT %s FROM client_relationship;", jsonFieldColumn);

        List<HashMap<String, String>> result = repository.rawQuery(database, query);
        assertEquals(1, result.size());
        Client client = JsonFormUtils.gson.fromJson(result.get(0).get(jsonFieldColumn), Client.class);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", client.getBaseEntityId());
        verify(cursor).close();
    }

    @Test
    public void testSaveRelationShipWithVariableArgsShouldInsertAll() {
        ClientRelationship clientRelationship1 = mock(ClientRelationship.class);
        ClientRelationship clientRelationship2 = mock(ClientRelationship.class);
        SQLiteStatement sqLiteStatement = mock(SQLiteStatement.class);
        when(database.compileStatement(anyString())).thenReturn(sqLiteStatement);
        ClientRelationshipRepository repository = spy(ClientRelationshipRepository.class);
        when(repository.getWritableDatabase()).thenReturn(database);

        repository.saveRelationship(clientRelationship1, clientRelationship2);
        verify(sqLiteStatement, times(2)).executeInsert();
        verify(sqLiteStatement).close();
    }


}
