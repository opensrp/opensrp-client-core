package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class SettingsRepositoryTest extends BaseUnitTest {

    SettingsRepository settingsRepository;
    public static final String SETTINGS_KEY_COLUMN = "key";
    public static final String SETTINGS_VALUE_COLUMN = "value";

    @Mock
    SQLiteDatabase sqLiteDatabase;
    @Mock
    Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        settingsRepository = new SettingsRepository();
        settingsRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(settingsRepository);
    }

    @Test
    public void assertOnCreate() {
        settingsRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertupdateSetting() {
        settingsRepository.updateSetting("KEY", "VALUE");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertupdateBLOB() {
        settingsRepository.updateBLOB("", new byte[]{});
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertquerySetting() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN});
        matrixCursor.addRow(new String[]{"KEY", "VALUE"});
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.anyString())).thenReturn(matrixCursor);
        Assert.assertEquals(settingsRepository.querySetting("", ""), "KEY");
    }

    @Test
    public void assertqueryBlob() {
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.anyString())).thenReturn(getCursor());
        Assert.assertEquals(settingsRepository.queryBLOB(""), null);
    }

    public MatrixCursor getCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN});
        matrixCursor.addRow(new Object[]{new byte[]{}, new byte[]{}});
        return matrixCursor;
    }
}
