package org.smartregister.repository.mock;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.Session;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class RepositoryMock {

    public Repository getInstance1(){
        try {
            //Libraryname without .so!!
            System.loadLibrary("sqlcipher");
        }
        catch(UnsatisfiedLinkError ex)
        {
            //Not found
        }
        Session session = new Session();
        session.setRepositoryName(AllConstants.DATABASE_NAME);
        DrishtiRepository drishtiRepositories[] = {DrishtiRepositoryMock.getDrishtiRepository()};
        Repository repository = new Repository(ContextMock.getContext(), session, drishtiRepositories);
        return repository;
    }
}
