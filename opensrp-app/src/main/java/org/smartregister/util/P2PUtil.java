package org.smartregister.util;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import static org.smartregister.AllConstants.ROWID;

public class P2PUtil {
    private static final String TAG = P2PUtil.class.getCanonicalName();
    private static final String ID = "_id";


    public static int getMaxRowId(@NonNull String table, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT max(" + ROWID + ") AS max_row_id FROM " + table, null);
        int rowId = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                rowId = cursor.getInt(cursor.getColumnIndex("max_row_id"));
            }

            cursor.close();
        }

        return rowId;
    }

    public static Boolean checkIfExistsById(String table, String formSubmissionId, SQLiteDatabase sqLiteDatabase) {
        Cursor mCursor = null;
        try {
            String query = "SELECT "
                    + ID
                    + " FROM "
                    + table
                    + " WHERE "
                    + ID
                    + " =?";
            mCursor = sqLiteDatabase.rawQuery(query, new String[]{formSubmissionId});
            if (mCursor != null && mCursor.moveToFirst()) {

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }
}
