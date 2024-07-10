package org.smartregister.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.mock.RepositoryMock;
import org.smartregister.util.Session;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;

/**
 * Created by kaderchowdhury on 19/11/17.
 */
@PrepareForTest({DrishtiApplication.class, SQLiteDatabase.class})
public class RepositoryTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private RepositoryMock repositoryMock;

    @Mock
    private DrishtiApplication drishtiApplication;

    private Repository repository;
    @Mock
    private android.content.Context context;
    @Mock
    private Session session;
    @Mock
    private DrishtiRepository drishtiRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getDir("opensrp", android.content.Context.MODE_PRIVATE)).thenReturn(new File("/"));

    }

    @Test
    public void constructor1() {
//        repositoryMock = new RepositoryMock();
//        repositoryMock.getInstance1();
//        Session session = new Session();
//        session.setRepositoryName(AllConstants.DATABASE_NAME);
//        DrishtiRepository drishtiRepositories[] = {DrishtiRepositoryMock.getDrishtiRepository()};
//        Repository repository = new Repository(ContextMock.getContext(), session, drishtiRepositories);
    }

}
