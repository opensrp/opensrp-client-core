package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.LocationTag;
import org.smartregister.util.PropertiesConverter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ilakozejumanne on 01/29/20.
 */
public class LocationTagRepository extends BaseRepository {

    protected static final String NAME = "name";
    protected static final String LOCATION_ID = "location_id";
    protected static final String LOCATION_TAG_TABLE = "location_tag";
    protected static final String[] COLUMNS = new String[]{NAME,LOCATION_ID};
    private static final String CREATE_LOCATION_TAG_TABLE =
            "CREATE TABLE " + LOCATION_TAG_TABLE + " (" +
                    NAME + " VARCHAR NOT NULL, " +
                    LOCATION_ID + " VARCHAR NOT NULL, " +
                    "PRIMARY KEY ("+NAME+", "+LOCATION_ID+")) ";
    private static final String CREATE_LOCATION_TAG_NAME_INDEX = "CREATE INDEX "
            + LOCATION_TAG_TABLE + "_" + NAME + "_ind ON " + LOCATION_TAG_TABLE + "(" + NAME + ")";
    protected static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TAG_TABLE);
        database.execSQL(CREATE_LOCATION_TAG_NAME_INDEX);
    }

    protected String getLocationTagTableName() {
        return LOCATION_TAG_TABLE;
    }

    public void addOrUpdate(LocationTag locationTag) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, locationTag.getName());
        contentValues.put(LOCATION_ID, locationTag.getLocationId());
        getWritableDatabase().replace(getLocationTagTableName(), null, contentValues);

    }

    public List<LocationTag> getAllLocationTags() {
        Cursor cursor = null;
        List<LocationTag> locationsTags = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTagTableName(), null);
            while (cursor.moveToNext()) {
                locationsTags.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationsTags;
    }

    public List<LocationTag> getLocationTagByLocationId(String id) {
        List<LocationTag> locationsTags = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTagTableName() +
                " WHERE " + LOCATION_ID + " =?", new String[]{id})) {
            while (cursor.moveToNext()) {
                locationsTags.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return locationsTags;

    }

    /**
     * Get a List of location tags that match the list of ids provided
     *
     * @param ids list of location ids
     * @return the list of locations that match the params provided
     */
    public List<LocationTag> getLocationTagsByLocationIds(List<String> ids) {
        return getLocationTagsByLocationIds(ids, true);
    }

    /**
     * Get a List of location tags that either match or don't match the list of ids provided
     * depending on value of the inclusive flag
     *
     * @param ids       list of location tag ids
     * @param inclusive flag that determines whether the list of locations returned
     *                  should include the locations whose ids match the params provided
     *                  or exclude them
     * @return
     */
    public List<LocationTag> getLocationTagsByLocationIds(List<String> ids, Boolean inclusive) {
        Cursor cursor = null;
        List<LocationTag> locationTags = new ArrayList<>();
        int idCount = ids != null ? ids.size() : 0;
        String[] idsArray = ids != null ? ids.toArray(new String[0]) : null;

        String operator = inclusive != null && inclusive ? "IN" : "NOT IN";

        String selectSql = String.format("SELECT * FROM " + getLocationTagTableName() +
                " WHERE " + LOCATION_ID + " " + operator + " (%s)", insertPlaceholdersForInClause(idCount));

        try {
            cursor = getReadableDatabase().rawQuery(selectSql, idsArray);
            while (cursor.moveToNext()) {
                locationTags.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationTags;

    }

    protected LocationTag readCursor(Cursor cursor) {
        LocationTag locationTag = new LocationTag();
        locationTag.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        locationTag.setLocationId(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));
        return locationTag;
    }

}
