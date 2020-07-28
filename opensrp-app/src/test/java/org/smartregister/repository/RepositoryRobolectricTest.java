package org.smartregister.repository;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.util.Session;

import java.io.File;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 28-07-2020.
 */
public class RepositoryRobolectricTest extends BaseRobolectricUnitTest {


    @Test
    public void constructor1ShouldCallUpdateMasterRepository() {
        Session session = Mockito.mock(Session.class);
        Mockito.doReturn(AllConstants.DATABASE_NAME).when(session).repositoryName();

        DrishtiRepository drishtiRepository1 = Mockito.mock(DrishtiRepository.class);
        DrishtiRepository drishtiRepository2 = Mockito.mock(DrishtiRepository.class);

        // Mock fetching the database path
        Context context = Mockito.spy(RuntimeEnvironment.application);
        Mockito.doReturn(Mockito.mock(File.class)).when(context).getDatabasePath("drishti.db");

        // Execute the method under test
        Repository repository = new Repository(context, session, drishtiRepository1, drishtiRepository2);


        // Verify
        Mockito.verify(drishtiRepository1).updateMasterRepository(repository);
        Mockito.verify(drishtiRepository2).updateMasterRepository(repository);
    }

    @Test
    public void constructor2ShouldCallUpdateMasterRepository() {
        DrishtiRepository drishtiRepository1 = Mockito.mock(DrishtiRepository.class);
        DrishtiRepository drishtiRepository2 = Mockito.mock(DrishtiRepository.class);

        // Mock fetching the database path
        Context context = Mockito.spy(RuntimeEnvironment.application);
        Mockito.doReturn(Mockito.mock(File.class)).when(context).getDatabasePath("drishti.db");

        // Execute the method under test
        Repository repository = new Repository(context, AllConstants.DATABASE_NAME, 1, Mockito.mock(Session.class), Mockito.mock(CommonFtsObject.class), drishtiRepository1, drishtiRepository2);


        // Verify
        Mockito.verify(drishtiRepository1).updateMasterRepository(repository);
        Mockito.verify(drishtiRepository2).updateMasterRepository(repository);
    }
}
