package org.smartregister.repository.helper;

import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 18/07/2020.
 */
public class OpenSRPDatabaseErrorHandler implements DatabaseErrorHandler {

    private final String TAG = getClass().getSimpleName();

    /**
     * defines the default method to be invoked when database corruption is detected.
     *
     * @param dbObj the {@link SQLiteDatabase} object representing the database on which corruption
     *              is detected.
     */
    public void onCorruption(SQLiteDatabase dbObj) {
        Timber.e("Corruption reported by sqlite on database, db file path: %s", dbObj.getPath());

        if (dbObj.isOpen()) {
            Timber.e("Database object for corrupted database is already open, closing");

            try {
                dbObj.close();
            } catch (Exception e) {
                Timber.e(e, "Exception closing Database object for corrupted database, ignored");
            }
        }
    }
}
