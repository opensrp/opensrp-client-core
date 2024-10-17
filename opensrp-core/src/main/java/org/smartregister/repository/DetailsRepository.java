package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by koros on 4/19/16.
 */
public class DetailsRepository extends DrishtiRepository {

    private static final String SQL =
            "CREATE virtual table ec_details using fts4 " + "" + "(base_entity_id"
                    + " VARCHAR, key VARCHAR, value VARCHAR, event_date datetime)";
    private static final String TABLE_NAME = "ec_details";
    private static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
    private static final String KEY_COLUMN = "key";
    private static final String VALUE_COLUMN = "value";
    private static final String EVENT_DATE_COLUMN = "event_date";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL);
    }

    public void add(String baseEntityId, String key, String value, Long timestamp) {
        SQLiteDatabase  database = masterRepository().getReadableDatabase();
//        long start = System.currentTimeMillis();
        Boolean exists = getIdForDetailsIfExists(baseEntityId, key, value);
//        Timber.d("check if details exist's took %s, ", System.currentTimeMillis() - start);
        if (exists == null) { // Value has not changed, no need to update
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BASE_ENTITY_ID_COLUMN, baseEntityId);
        values.put(KEY_COLUMN, key);
        values.put(VALUE_COLUMN, value);
        values.put(EVENT_DATE_COLUMN, timestamp);

            if (exists) {
                long startUpdate = System.currentTimeMillis();
                int updated = database.update(TABLE_NAME, values,
                        BASE_ENTITY_ID_COLUMN + " = ? AND " + KEY_COLUMN + " MATCH ? ",
                        new String[]{baseEntityId, key});
//            Timber.d("updating details for %S took %s, ",  TABLE_NAME, System.currentTimeMillis() - startUpdate);
                //Log.i(getClass().getName(), "Detail Row Updated: " + String.valueOf(updated));
            } else {
                long insertStart = System.currentTimeMillis();
                long rowId = database.insert(TABLE_NAME, null, values);
//            Timber.d("insert into details %s table took %s, ", TABLE_NAME,  System.currentTimeMillis() - insertStart);
            //Log.i(getClass().getName(), "Details Row Inserted : " + String.valueOf(rowId));
        }
    }

    public void batchInsertDetails(Map<String, String> values, long timestamp) {
        SQLiteDatabase database = null;
        SQLiteStatement insertStatement = null;
        SQLiteStatement updateStatement = null;

        try {
            database = masterRepository().getWritableDatabase();
            // Start transaction
            database.beginTransaction();

            // Prepare the SQL for inserts and updates
            String insertSQL = "INSERT INTO " + TABLE_NAME + " (" +
                    BASE_ENTITY_ID_COLUMN + ", " + KEY_COLUMN + ", " + VALUE_COLUMN + ", " + EVENT_DATE_COLUMN +
                    ") VALUES (?, ?, ?, ?)";

            String updateSQL = "UPDATE " + TABLE_NAME + " SET " + VALUE_COLUMN + " = ?, " +
                    EVENT_DATE_COLUMN + " = ? WHERE " + BASE_ENTITY_ID_COLUMN + " = ? AND " + KEY_COLUMN + " = ?";

            insertStatement = database.compileStatement(insertSQL);
            updateStatement = database.compileStatement(updateSQL);

            String baseEntityId = values.get(BASE_ENTITY_ID_COLUMN);


            for (String key : values.keySet()) {
                String val = values.get(key);
                if(val == null ) continue;
                Boolean exists = getIdForDetailsIfExists(baseEntityId, key, val);

                if (exists == null) { // Value has not changed, no need to update
                    continue;
                }

                if (exists) {
                    // Bind values for update
                    updateStatement.bindString(1, val); // Bind VALUE_COLUMN
                    updateStatement.bindLong(2, timestamp); // Bind EVENT_DATE_COLUMN
                    updateStatement.bindString(3, baseEntityId); // Bind BASE_ENTITY_ID_COLUMN
                    updateStatement.bindString(4, key); // Bind KEY_COLUMN

                    // Execute the update
                    updateStatement.execute();
                    updateStatement.clearBindings();
                } else {
                    // Bind values for insert
                    insertStatement.bindString(1, baseEntityId); // Bind BASE_ENTITY_ID_COLUMN
                    insertStatement.bindString(2, key); // Bind KEY_COLUMN
                    insertStatement.bindString(3, val); // Bind VALUE_COLUMN
                    insertStatement.bindLong(4, timestamp); // Bind EVENT_DATE_COLUMN

                    // Execute the insert
                    insertStatement.executeInsert();
                    insertStatement.clearBindings();
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            // End the transaction
            if (database != null) {
                database.endTransaction();
            }
            // Close the prepared statements
            if (insertStatement != null) {
                insertStatement.close();
            }
            if (updateStatement != null) {
                updateStatement.close();
            }
        }
    }

    private Boolean getIdForDetailsIfExists(String baseEntityId, String key, String value) {
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = masterRepository().getWritableDatabase();
            String query = "SELECT " + VALUE_COLUMN + " FROM " + TABLE_NAME + " WHERE "
                    + BASE_ENTITY_ID_COLUMN + " = ? AND " + KEY_COLUMN + " MATCH ? ";
            mCursor = db.rawQuery(query, new String[]{baseEntityId, key});
            if (mCursor != null && mCursor.moveToFirst()) {
                if (value != null) {
                    String currentValue = mCursor.getString(mCursor.getColumnIndex(VALUE_COLUMN));
                    if (value.equals(currentValue)) { // Value has not changed, no need to update
                        return null;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }

    public Map<String, String> getAllDetailsForClient(String baseEntityId) {
        Cursor cursor = null;
        Map<String, String> clientDetails = new HashMap<String, String>();
        try {
            SQLiteDatabase db = masterRepository().getReadableDatabase();
            String query =
                    "SELECT * FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID_COLUMN + " =?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(cursor.getColumnIndex(KEY_COLUMN));
                    String value = cursor.getString(cursor.getColumnIndex(VALUE_COLUMN));
                    clientDetails.put(key, value);
                } while (cursor.moveToNext());
            }
            return clientDetails;
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return clientDetails;
    }

    public Map<String, String> updateDetails(CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> details = getAllDetailsForClient(commonPersonObjectClient.entityId());
        details.putAll(commonPersonObjectClient.getColumnmaps());

        if (commonPersonObjectClient.getDetails() != null) {
            commonPersonObjectClient.getDetails().putAll(details);
        } else {
            commonPersonObjectClient.setDetails(details);
        }
        return details;
    }

    public Map<String, String> updateDetails(CommonPersonObject commonPersonObject) {
        Map<String, String> details = getAllDetailsForClient(commonPersonObject.getCaseId());
        details.putAll(commonPersonObject.getColumnmaps());

        if (commonPersonObject.getDetails() != null) {
            commonPersonObject.getDetails().putAll(details);
        } else {
            commonPersonObject.setDetails(details);
        }
        return details;
    }

    public boolean deleteDetails(String baseEntityId) {
        try {
            SQLiteDatabase db = masterRepository().getWritableDatabase();
            int afftectedRows = db
                    .delete(TABLE_NAME, BASE_ENTITY_ID_COLUMN + " = ?", new String[]{baseEntityId});
            if (afftectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

}
