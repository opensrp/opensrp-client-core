package org.smartregister.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.view.activity.DrishtiApplication;

public abstract class DrishtiRepository {

    protected Repository masterRepository = DrishtiApplication.getInstance().getRepository();

    public void updateMasterRepository(Repository repository) {
        if (repository != null) {
            this.masterRepository = repository;
        }
    }

    abstract protected void onCreate(SQLiteDatabase database);
}
