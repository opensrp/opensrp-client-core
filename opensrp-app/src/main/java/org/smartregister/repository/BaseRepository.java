package org.smartregister.repository;

import net.sqlcipher.database.SQLiteDatabase;

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
}
