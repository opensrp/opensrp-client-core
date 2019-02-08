package org.smartregister.repository;

import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.db.Column;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by keyman on 09/03/2017.
 */
public class BaseRepository {
    private static final String TAG = BaseRepository.class.getCanonicalName();

    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";
    public static String TYPE_Valid = "Valid";
    public static String TYPE_InValid = "Invalid";
    public static String TYPE_Task_Unprocessed = "task_unprocessed";
    public static String TYPE_Created = "Created";
    protected static final String ORDER_BY = " order by ";

    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String COLLATE_NOCASE = " COLLATE NOCASE ";

    private Repository repository;

    public BaseRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    protected String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    public SQLiteDatabase getWritableDatabase() {
        return this.repository.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return this.repository.getReadableDatabase();
    }

    interface BaseTable {
        Column[] columns();

        String name();
    }

    public ArrayList<HashMap<String, String>> rawQuery(SQLiteDatabase db, String query) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    maplist.add(map);
                } while (cursor.moveToNext());
            }

            return maplist;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

}
