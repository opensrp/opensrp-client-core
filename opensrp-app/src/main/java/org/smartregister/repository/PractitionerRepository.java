package org.smartregister.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.Practitioner;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PractitionerRepository extends BaseRepository {

    protected static final String ID = "id";
    protected static final String IDENTIFIER = "identifier";
    protected static final String IS_ACTIVE = "is_active";
    protected static final String NAME = "name";
    protected static final String USER_ID = "user_id";
    protected static final String USERNAME = "username";

    private static final String PRACTITIONER_TABLE = "practitioner";

    private static final String CREATE_PRACTITIONER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + PRACTITIONER_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    IDENTIFIER + " VARCHAR NOT NULL," +
                    IS_ACTIVE + " INTEGER NOT NULL," +
                    NAME + " VARCHAR NOT NULL," +
                    USER_ID + " VARCHAR NOT NULL," +
                    USERNAME + " VARCHAR NOT NULL)";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_PRACTITIONER_TABLE);
    }

    public void addOrUpdate(Practitioner practitioner) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, practitioner.getIdentifier());
        contentValues.put(IDENTIFIER, practitioner.getIdentifier());
        contentValues.put(IS_ACTIVE, practitioner.getActive());
        contentValues.put(NAME, practitioner.getName());
        contentValues.put(USER_ID, practitioner.getUserId());
        contentValues.put(USERNAME, practitioner.getUsername());
        getWritableDatabase().replace(PRACTITIONER_TABLE, null, contentValues);

    }

    protected Practitioner readCursor(@NonNull Cursor cursor) {
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier(cursor.getString(cursor.getColumnIndex(IDENTIFIER)));
        practitioner.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        practitioner.setActive(cursor.getInt(cursor.getColumnIndex(IS_ACTIVE)) > 0);
        practitioner.setUserId(cursor.getString(cursor.getColumnIndex(USER_ID)));
        practitioner.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));

        return practitioner;
    }


    public List<Practitioner> getAllPractitioners() {
        List<Practitioner> practitionerList = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PRACTITIONER_TABLE,
                null)) {
            while (cursor.moveToNext()) {
                practitionerList.add(readCursor(cursor));
            }
        }
        return practitionerList;
    }

    public Practitioner getPractitionerByIdentifier(String identifier) {
        Practitioner practitioner = null;
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + PRACTITIONER_TABLE +
                " WHERE " + IDENTIFIER + " =?", new String[]{identifier})) {
            while (cursor.moveToNext()) {
                practitioner = readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return practitioner;
    }

}
