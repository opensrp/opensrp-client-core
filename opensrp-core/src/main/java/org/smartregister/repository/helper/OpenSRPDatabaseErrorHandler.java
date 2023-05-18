package org.smartregister.repository.helper;

import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 18/07/2020.
 */
public class OpenSRPDatabaseErrorHandler implements DatabaseErrorHandler {

    /**
     * defines the default method to be invoked when database corruption is detected.
     * @param dbObj the {@link SQLiteDatabase} object representing the database on which corruption
     * is detected.
     */
    public void onCorruption(SQLiteDatabase dbObj) {
        Timber.e("Corruption reported by sqlite on database, deleting: %s", dbObj.getPath());

        if (dbObj.isOpen()) {
            Timber.e( "Database object for corrupted database is already open, closing");

            try {
                dbObj.close();
            } catch (Exception e) {
                /* ignored */
                Timber.e(e, "Exception closing Database object for corrupted database, ignored");
            }
        }

        deleteDatabaseFile(dbObj.getPath());
    }

    private void deleteDatabaseFile(String fileName) {
        if (fileName == null || fileName.equalsIgnoreCase(":memory:") || fileName.trim().length() == 0) {
            Timber.e("Cannot delete database. Provided filename is not valid: %s", fileName);
            return;
        }
        Timber.e( "deleting the database file: %s", fileName);
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(fileName).delete();
        } catch (Exception e) {
            /* print warning and ignore exception */
            Timber.w(e, "delete failed");
        }
    }
}
