package org.smartregister.repository;

import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class StructureRepository extends LocationRepository {

    private static final String LOCATION_TABLE = "structure";

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + LOCATION_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    public StructureRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public List<Location> getAllLocations() {
        throw new UnsupportedOperationException("getAllLocations not supported for Structures");
    }

}
