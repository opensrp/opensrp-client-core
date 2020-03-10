package org.smartregister.repository;

import android.content.ContentValues;

import org.junit.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Setting;

import static org.smartregister.repository.SettingsRepository.SETTINGS_KEY_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_SYNC_STATUS_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_TYPE_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_VALUE_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_VERSION_COLUMN;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class SettingsRepositoryTest extends BaseUnitTest {

    SettingsRepository settingsRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;

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
    public void testOnUpgradeExecutesCorrectSQLStatement() {
        settingsRepository.onUpgrade(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(3)).execSQL(Mockito.anyString());
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

    @Test
    public void testUpdateSetting() {
        Setting s = new Setting();
        s.setKey("test"); s.setValue("testValue"); s.setVersion("test"); s.setType("test"); s.setSyncStatus("test");
        settingsRepository.updateSetting(s);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void testQuerySetting() {
        settingsRepository.querySetting("");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query("settings", new String[]{"key", "value", "type", "version", "sync_status"},
                "key = ?", new String[]{""}, null, null, null, "1");
    }

    @Test
    public void testQuerySettingsByType() {
        settingsRepository.querySettingsByType("");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query("settings", new String[]{"key", "value", "type", "version", "sync_status"},
                "type = ?", new String[]{""}, null, null, null, null);
    }

    @Test
    public void testQueryUnsyncedSettings(){
        settingsRepository.queryUnsyncedSettings();
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query("settings", new String[]{"key", "value", "type", "version", "sync_status"},
                "sync_status = ?", new String[]{"PENDING"}, null, null, null, null);
    }

    @Test
    public void testQueryCore() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN, SETTINGS_TYPE_COLUMN, SETTINGS_VERSION_COLUMN, SETTINGS_SYNC_STATUS_COLUMN});
        matrixCursor.addRow(new String[]{"KEY", "VALUE", "TYPE", "COL", "STATE"});
        matrixCursor.moveToFirst();
        Setting s = settingsRepository.queryCore(matrixCursor);
        Assert.assertEquals(s.getKey(), "KEY");
    }
}
