package org.smartregister.repository;

import android.content.ContentValues;
import android.database.SQLException;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by ndegwamartin on 2019-12-02.
 */
public class UniqueIdRepositoryTest extends BaseUnitTest {
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

    private UniqueIdRepository uniqueIdRepository;

    private static final String testUsername = "testUser1";

    @Mock
    Context context;

    @Mock
    AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn(testUsername);

        CoreLibrary.init(context);

        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);

        uniqueIdRepository = new UniqueIdRepository();

        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodCorrectlyWithNoHyphen() {
        String openMrsId = "329893823";

        uniqueIdRepository.close(openMrsId);
        verify(sqLiteDatabase, times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodCorrectlyWithHyphen() {
        String openMrsId = "32989382-3";

        uniqueIdRepository.close(openMrsId);
        verify(sqLiteDatabase, times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodOnceIfRowUpdated() {

        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        String openMrsId = "3298938-2";

        doReturn(1).when(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        uniqueIdRepository.close(openMrsId);
        verify(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        Assert.assertNotNull(stringArgumentCaptor.getValue());
        assertEquals("unique_ids", stringArgumentCaptor.getAllValues().get(2));
        assertEquals(openMrsId, argsCaptor.getValue()[0]);

        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals(testUsername, values.getAsString("used_by"));


    }

    @Test
    public void testBulkInsertOpenMrsIds() {

        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        String openMrsId = "3298938-2";
        List<String> openMrsIds = Collections.singletonList(openMrsId);

        uniqueIdRepository.bulkInsertOpenmrsIds(openMrsIds);

        verify(sqLiteDatabase).beginTransaction();
        verify(sqLiteDatabase).setTransactionSuccessful();
        verify(sqLiteDatabase).endTransaction();
        verify(sqLiteDatabase).insert(stringArgumentCaptor.capture(), eq(null), contentValuesArgumentCaptor.capture());

        assertEquals("unique_ids", stringArgumentCaptor.getValue());
        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals(openMrsId, values.getAsString("openmrs_id"));
        assertEquals("not_used", values.getAsString("status"));
        assertEquals(testUsername, values.getAsString("synced_by"));
        assertNotNull(values.getAsString("created_at"));

    }

    @Test
    public void testBulkInsertOpenMrsIdsWithEmptyParamList() {

        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        uniqueIdRepository.bulkInsertOpenmrsIds(null);

        verifyNoMoreInteractions(sqLiteDatabase);

    }

    @Test
    public void testBulkInsertOpenMrsIdsWithExceptionThrown() {

        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        String openMrsId = "3298938-2";
        List<String> openMrsIds = Collections.singletonList(openMrsId);
        uniqueIdRepository = spy(uniqueIdRepository);
        doThrow(new SQLException()).when(sqLiteDatabase).insert(any(), any(), any());

        uniqueIdRepository.bulkInsertOpenmrsIds(openMrsIds);

        verify(sqLiteDatabase).beginTransaction();
        verify(sqLiteDatabase, never()).setTransactionSuccessful();
        verify(sqLiteDatabase).endTransaction();
        verify(sqLiteDatabase).insert(stringArgumentCaptor.capture(), eq(null), contentValuesArgumentCaptor.capture());

        assertEquals("unique_ids", stringArgumentCaptor.getValue());
        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals(openMrsId, values.getAsString("openmrs_id"));
        assertEquals("not_used", values.getAsString("status"));
        assertEquals(testUsername, values.getAsString("synced_by"));
        assertNotNull(values.getAsString("created_at"));
    }

}
