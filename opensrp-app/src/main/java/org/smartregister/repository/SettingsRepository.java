package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

public class SettingsRepository extends DrishtiRepository {
    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_KEY_COLUMN = "key";
    public static final String SETTINGS_VALUE_COLUMN = "value";
    static final String SETTINGS_SQL = "CREATE TABLE settings(key VARCHAR PRIMARY KEY, value BLOB)";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(SETTINGS_SQL);
    }

    public void updateSetting(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public void updateBLOB(String key, byte[] value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public String querySetting(String key, String defaultValue) {
        Cursor cursor = null;
        String value = defaultValue;
        try {
            SQLiteDatabase database = masterRepository.getReadableDatabase();
            cursor = database.query(SETTINGS_TABLE_NAME, new String[]{SETTINGS_VALUE_COLUMN},
                    SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }

    public byte[] queryBLOB(String key) {
        byte[] value = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase database = masterRepository.getReadableDatabase();
            cursor = database.query(SETTINGS_TABLE_NAME, new String[]{SETTINGS_VALUE_COLUMN},
                    SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                value = cursor.getBlob(0);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }

    private void replace(ContentValues values) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.replace(SETTINGS_TABLE_NAME, null, values);
    }
}
