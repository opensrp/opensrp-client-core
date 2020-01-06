package org.smartregister.util;

import android.support.v4.util.Pair;

import com.google.gson.Gson;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.sync.ClientProcessorForJava;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 1/6/20.
 */
public class RecreateECUtilTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SQLiteDatabase database;

    @Mock
    private ClientProcessorForJava clientProcessor;

    private RecreateECUtil recreateECUtil;
    private FormTag formTag;

    private String[] params = {"a", "b"};
    private String query = "select * from events";
    private String tableName = "ec_family_member";

    @Before
    public void setUp() {
        formTag = new FormTag();
        formTag.locationId = UUID.randomUUID().toString();
        recreateECUtil = new RecreateECUtil();
        Whitebox.setInternalState(recreateECUtil, "clientProcessor", clientProcessor);
    }


    @Test
    public void testCreateEventAndClients() throws IOException {
        when(database.rawQuery(query, params)).thenReturn(getECCursor());
        when(clientProcessor.getColumnMappings(tableName)).thenReturn(getEcConfig());
        Pair<List<Event>, List<Client>> eventsAndClients = recreateECUtil.createEventAndClients(database, tableName, query, params, "FamilyRegistration", "Family", formTag);
        assertNotNull(eventsAndClients);
        assertEquals(3, eventsAndClients.second.size());
    }


    @Test
    public void testCreateEventAndClientsForMissingTable() {
        when(clientProcessor.getColumnMappings(tableName)).thenReturn(null);
        Pair<List<Event>, List<Client>> eventsAndClients = recreateECUtil.createEventAndClients(database, tableName, query, params, "FamilyRegistration", "Family", formTag);
        assertNull(eventsAndClients);
        verifyZeroInteractions(database);
        verify(clientProcessor.getColumnMappings(tableName));
    }


    @Test
    public void testSaveEventAndClientsWithNullEC() {
        recreateECUtil.saveEventAndClients(null, database);
        verifyZeroInteractions(database);
    }

    private MatrixCursor getECCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id", "first_name", "last_name", "age", "date_registered", "vaccinated", "screened"});
        matrixCursor.addRow(new Object[]{"12", "Jane", "Doe", "23", "2019-10-21T07:00:00.000+07:00", "yes", "no"});
        matrixCursor.addRow(new Object[]{"11", "John", "Doe", "33", "2018-09-16T07:00:00.000+07:00", "yes", "no"});
        matrixCursor.addRow(new Object[]{"10", "Alex", "Mark", "23", "2007-01-01T07:00:00.000+07:00", "yes", "no"});
        return matrixCursor;

    }

    private Table getEcConfig() throws IOException {
        String path = "src/test/assets/" + "ec_client_fields.json";
        String config = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        return new Gson().fromJson(config, Table.class);
    }
}
