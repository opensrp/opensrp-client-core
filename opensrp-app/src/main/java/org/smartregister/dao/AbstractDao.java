package org.smartregister.dao;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class AbstractDao {

    private static SimpleDateFormat DOB_DATE_FORMAT;
    private static SimpleDateFormat NATIVE_FORMS_DATE_FORMAT;

    public static SimpleDateFormat getDobDateFormat() {
        if (DOB_DATE_FORMAT == null)
            DOB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return DOB_DATE_FORMAT;
    }

    public static SimpleDateFormat getNativeFormsDateFormat() {
        if (NATIVE_FORMS_DATE_FORMAT == null)
            NATIVE_FORMS_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        return NATIVE_FORMS_DATE_FORMAT;
    }

    protected static void updateDB(String sql) {
        try {
            SQLiteDatabase db = DrishtiApplication.getInstance().getRepository().getWritableDatabase();
            db.rawExecSQL(sql);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Returns a mapped pojo by reading the sqlite adapter
     * handles iteration and cursor disposable
     *
     * @param query
     * @param dataMap
     * @param <T>
     * @return
     */
    protected static <T> List<T> readData(String query, DataMap<T> dataMap) {
        SQLiteDatabase db = DrishtiApplication.getInstance().getRepository().getReadableDatabase();
        return readData(query, dataMap, db);
    }

    protected static <T> List<T> readData(String query, DataMap<T> dataMap, SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            List<T> list = new ArrayList<>();
            cursor = db.rawQuery(query, new String[]{});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(dataMap.readCursor(cursor));
                cursor.moveToNext();
            }
            return list;
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Reads the results of a raw query and returns a list object representing all the table
     * rows and a map representing the columns
     *
     * @param query
     * @return
     */
    public static List<Map<String, String>> readData(String query, String[] selectionArgs) {
        List<Map<String, String>> list = new ArrayList<>();
        Cursor cursor = DrishtiApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query, selectionArgs);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Map<String, String> res = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                res.put(cursor.getColumnName(i), getCursorValue(cursor, i));
            }
            list.add(res);
            cursor.moveToNext();
        }
        return list;
    }

    @Nullable
    protected static String getCursorValue(Cursor c, int column_index) {
        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getString(column_index);
    }

    @Nullable
    protected static String getCursorValue(Cursor c, String column_name) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getString(column_index);
    }

    protected static String getCursorValue(Cursor c, String column_name, String defaultValue) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return defaultValue;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? defaultValue : c.getString(column_index);
    }

    @Nullable
    protected static Long getCursorLongValue(Cursor c, String column_name) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getLong(column_index);
    }

    @Nullable
    protected static Integer getCursorIntValue(Cursor c, String column_name) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getInt(column_index);
    }

    protected static Integer getCursorIntValue(Cursor c, String column_name, int defaultValue) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? defaultValue : c.getInt(column_index);
    }

    @Nullable
    protected static Date getCursorValueAsDate(Cursor c, String column_name, SimpleDateFormat formatter) {
        String value = getCursorValue(c, column_name);
        if (StringUtils.isBlank(value))
            return null;

        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    @Nullable
    protected static Date getCursorValueAsDate(Cursor c, String column_name) {
        String value = getCursorValue(c, column_name);
        if (StringUtils.isBlank(value))
            return null;

        return new Date(Long.parseLong(value));
    }

    public interface DataMap<T> {
        T readCursor(Cursor cursor);
    }
}
