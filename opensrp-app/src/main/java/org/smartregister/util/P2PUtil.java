package org.smartregister.util;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

import timber.log.Timber;

import static org.smartregister.AllConstants.ROWID;

public class P2PUtil {
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
            Timber.e(e, "EXCEPTION %s", e.toString());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }

    public static Gson gsonDateTime() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {
                    @Override
                    public JsonElement serialize(DateTime json, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(ISODateTimeFormat.dateTime().print(json));
                    }
                })
                .registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
                    @Override
                    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(json.getAsString());
                        return dt;
                    }
                })
                .create();
    }
}
