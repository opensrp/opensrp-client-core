package org.smartregister.repository;

import net.sqlcipher.database.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
}
