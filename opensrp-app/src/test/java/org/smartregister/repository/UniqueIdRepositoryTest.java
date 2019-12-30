package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.DrishtiApplication;

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

        Mockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(testUsername);

        CoreLibrary.init(context);

        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);

        uniqueIdRepository = new UniqueIdRepository();

        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodCorrectlyWithNoHyphen() {
        String openMrsId = "329893823";

        uniqueIdRepository.close(openMrsId);
        Mockito.verify(sqLiteDatabase, Mockito.times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodCorrectlyWithHyphen() {
        String openMrsId = "32989382-3";

        uniqueIdRepository.close(openMrsId);
        Mockito.verify(sqLiteDatabase, Mockito.times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodOnceIfRowUpdated() {

        String openMrsId = "3298938-2";

        Mockito.doReturn(1).when(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        uniqueIdRepository.close(openMrsId);
        Mockito.verify(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        Assert.assertNotNull(stringArgumentCaptor.getValue());
        Assert.assertEquals("unique_ids", stringArgumentCaptor.getAllValues().get(2));
        Assert.assertEquals(openMrsId, argsCaptor.getValue()[0]);

        ContentValues values = contentValuesArgumentCaptor.getValue();
        Assert.assertEquals(testUsername, values.getAsString("used_by"));


    }

}
