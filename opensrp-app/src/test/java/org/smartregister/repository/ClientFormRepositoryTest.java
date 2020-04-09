package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.ClientForm;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.repository.ClientFormRepository.ACTIVE;
import static org.smartregister.repository.ClientFormRepository.CLIENT_FORM_TABLE;
import static org.smartregister.repository.ClientFormRepository.CREATED_AT;
import static org.smartregister.repository.ClientFormRepository.IDENTIFIER;
import static org.smartregister.repository.ClientFormRepository.VERSION;

/**
 * Created by ilakozejumanne on 2020-04-09.
 */

public class ClientFormRepositoryTest extends BaseUnitTest {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ClientFormRepository clientFormRepository;
    @Mock
    private Repository repository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<String[]> argsCaptor;
    private String clientFormJson = "{\"id\":\"1\",\"version\":\"1.1.0\",\"identifier\":\"en/child/enrollment.json\",\"module\":\"child\",\"json\":\"{}\",\"jurisdiction\":\"\",\"label\":\"Child Enrollment Form\",\"isNew\":true,\"active\":true,\"createdAt\":\"2020-04-09 00:00:00\"}";

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        clientFormRepository = new ClientFormRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        ClientForm clientForm = gson.fromJson(clientFormJson, ClientForm.class);
        clientFormRepository.addOrUpdate(clientForm);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(CLIENT_FORM_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(10, contentValues.size());

        assertEquals("1", contentValues.getAsString(ClientFormRepository.ID));
        assertEquals("1.1.0", contentValues.getAsString(VERSION));
        assertEquals("en/child/enrollment.json", contentValues.getAsString(IDENTIFIER));
        assertEquals(true, contentValues.getAsBoolean(ACTIVE));
        assertEquals(true, contentValues.getAsBoolean(ClientFormRepository.IS_NEW));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowException() {
        ClientForm clientForm = new ClientForm();
        clientFormRepository.addOrUpdate(clientForm);
    }

    @Test
    public void testGetActiveClientFormByIdentifier() {
        String identifier = "en/child/enrollment.json";
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " =? AND " + ACTIVE + " = 1", new String[]{identifier})).thenReturn(getCursor());
        ClientForm clientForm = clientFormRepository.getActiveClientFormByIdentifier(identifier);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " =? AND " + ACTIVE + " = 1", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals(identifier, argsCaptor.getValue()[0]);

        assertTrue(clientForm.isActive());

    }

    @Test
    public void testGetClientFormByIdentifier() {
        String identifier = "en/child/enrollment.json";
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " =? ORDER BY " + CREATED_AT + " DESC", new String[]{identifier})).thenReturn(getCursor());
        List<ClientForm> clientFormList = clientFormRepository.getClientFormByIdentifier(identifier);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " =? ORDER BY " + CREATED_AT + " DESC", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals(identifier, argsCaptor.getValue()[0]);

        ClientForm clientForm = clientFormList.get(0);
        assertEquals(identifier, clientForm.getIdentifier());

    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(ClientFormRepository.COLUMNS);
        ClientForm clientForm = gson.fromJson(clientFormJson, ClientForm.class);
        cursor.addRow(new Object[]{clientForm.getId(), clientForm.getVersion(), clientForm.getIdentifier(), clientForm.getModule(), clientForm.getJson(), clientForm.getJurisdiction(), clientForm.getLabel(), clientForm.isNew() ? 1 : 0, clientForm.isActive() ? 1 : 0, clientForm.getCreatedAt()});
        return cursor;
    }

}
