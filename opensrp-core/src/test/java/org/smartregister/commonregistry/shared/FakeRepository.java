package org.smartregister.commonregistry.shared;

import android.content.Context;

import net.sqlcipher.DatabaseErrorHandler;

import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.Session;

import java.lang.reflect.Field;

/**
 * Created by raihan on 10/24/17.
 */

public class FakeRepository extends Repository {
    public FakeRepository(Context context, Session session, DatabaseErrorHandler errorHandler, DrishtiRepository... repositories) {
        super(context, session, errorHandler, repositories);
    }

    public FakeRepository(Context context, Session session, CommonFtsObject commonFtsObject, DatabaseErrorHandler errorHandler, DrishtiRepository... repositories) {
        super(context, session, commonFtsObject, errorHandler, repositories);
    }

    public FakeRepository(Context context, String dbName, int version, Session session, CommonFtsObject commonFtsObject, DatabaseErrorHandler errorHandler, DrishtiRepository... repositories) {
        super(context, dbName, version, session, commonFtsObject, errorHandler, repositories);
    }

    public void setRepositories(DrishtiRepository... repositoriestoAssign) throws Exception {
        Field field = FakeRepository.class.getDeclaredField("repositories");
        field.setAccessible(true);
        field.set(this, repositoriestoAssign);
//        Object value = field.get(this);
        field.setAccessible(false);
    }
}
