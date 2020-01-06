package org.smartregister.util;

import android.support.v4.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.UUID;

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
    private String tableName = "ec_family";

    @Before
    public void setUp() {
        formTag = new FormTag();
        formTag.locationId = UUID.randomUUID().toString();
        recreateECUtil = new RecreateECUtil();
        Whitebox.setInternalState(recreateECUtil, "clientProcessor", clientProcessor);
    }


    @Test
    public void testCreateEventAndClients() {
        FormTag formTag = new FormTag();
        formTag.locationId = UUID.randomUUID().toString();
        Pair<List<Event>, List<Client>> eventsAndClients = recreateECUtil.createEventAndClients(database, tableName, query, params, "FamilyRegistration", "Family", formTag);

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
}
