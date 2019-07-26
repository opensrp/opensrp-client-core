package org.smartregister.repository;

import android.content.ContentValues;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.repository.helper.MappingHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class StructureRepository extends LocationRepository {

    protected static String STRUCTURE_TABLE = "structure";
    private static final String SYNC_STATUS = "sync_status";

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + STRUCTURE_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    NAME + " VARCHAR , " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    LATITUDE + " FLOAT , " +
                    LONGITUDE + " FLOAT , " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    private static final String CREATE_LOCATION_PARENT_INDEX = "CREATE INDEX "
            + STRUCTURE_TABLE + "_" + PARENT_ID + "_ind ON " + STRUCTURE_TABLE + "(" + PARENT_ID + ")";

    private MappingHelper helper;

    public StructureRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TABLE);
        database.execSQL(CREATE_LOCATION_PARENT_INDEX);
    }

    @Override
    public List<Location> getAllLocations() {
        throw new UnsupportedOperationException("getAllLocations not supported for Structures");
    }

    @Override
    protected String getLocationTableName() {
        return STRUCTURE_TABLE;
    }

    public List<Location> getAllUnsynchedCreatedStructures() {
        Cursor cursor = null;
        List<Location> structures = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT *  FROM %s WHERE %s =?", STRUCTURE_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Created});
            while (cursor.moveToNext()) {
                structures.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return structures;
    }

    public void markStructuresAsSynced(String structureId) {
        try {
            ContentValues values = new ContentValues();
            values.put(ID, structureId);
            values.put(SYNC_STATUS, BaseRepository.TYPE_Synced);

            getWritableDatabase().update(STRUCTURE_TABLE, values, ID + " = ?", new String[]{structureId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOrUpdate(Location location) {
        if (StringUtils.isBlank(location.getId()))
            throw new IllegalArgumentException("id not provided");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, location.getId());
        contentValues.put(UUID, location.getProperties().getUid());
        contentValues.put(PARENT_ID, location.getProperties().getParentId());
        contentValues.put(NAME, location.getProperties().getName());
        contentValues.put(SYNC_STATUS, location.getSyncStatus());
        contentValues.put(GEOJSON, gson.toJson(location));
        if (location.getGeometry().getType().equals(Geometry.GeometryType.POINT)) {
            contentValues.put(LONGITUDE, location.getGeometry().getCoordinates().get(0).getAsFloat());
            contentValues.put(LATITUDE, location.getGeometry().getCoordinates().get(1).getAsFloat());
        } else if (helper != null) {
            android.location.Location center = helper.getCenter(gson.toJson(location.getGeometry()));
            contentValues.put(LATITUDE, center.getLatitude());
            contentValues.put(LONGITUDE, center.getLongitude());
        }
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);

    }

    public void setHelper(MappingHelper helper) {
        this.helper = helper;
    }
}
