package org.smartregister.multitenant;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.content.SharedPreferences;
import android.os.Build;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.TestApplication;
import org.smartregister.exception.AppResetException;
import org.smartregister.multitenant.exception.PreResetAppOperationException;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.p2p.model.AppDatabase;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.repository.Repository;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.view.activity.DrishtiApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 14-04-2020.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1, application = TestApplication.class, shadows = {ShadowAppDatabase.class})
public class ResetAppUtilTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ResetAppUtil resetAppUtil;

    @Before
    public void setUp() throws Exception {
        resetAppUtil = Mockito.spy(new ResetAppUtil(DrishtiApplication.getInstance()));
    }

    /*@Ignore
    @Test
    public void startResetProcess() {
        MultiLanguageActivity formActivity = Robolectric.buildActivity(MultiLanguageActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        resetAppUtil.startResetProcess(formActivity);
        Mockito.verify(resetAppUtil).performPreResetChecks();
    }*/

    @Test
    public void performPreResetChecks() {
    }

    @Test
    public void performResetOperations() throws AppResetException {
        Mockito.doNothing().when(resetAppUtil).clearP2PDb();

        resetAppUtil.performResetOperations();
        Mockito.verify(resetAppUtil).clearAllPrivateKeyEntries();
        Mockito.verify(resetAppUtil).clearSqCipherDb();
        Mockito.verify(resetAppUtil).clearP2PDb();
        Mockito.verify(resetAppUtil).clearSharedPreferences();
    }

    @Test
    public void clearP2PDb() {
        P2POptions p2POptions = new P2POptions(true);

        P2PLibrary.Options p2PLibraryOptions = new P2PLibrary.Options(RuntimeEnvironment.application, "team-id", "username", Mockito.mock(P2PAuthorizationService.class), Mockito.mock(ReceiverTransferDao.class), Mockito.mock(SenderTransferDao.class));
        P2PLibrary.init(p2PLibraryOptions);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "p2POptions", p2POptions);

        AppDatabase appDatabase = Mockito.mock(AppDatabase.class);
        SupportSQLiteOpenHelper supportSQLiteOpenHelper = Mockito.mock(SupportSQLiteOpenHelper.class);


        ShadowAppDatabase.setDb(appDatabase);
        Mockito.doReturn(supportSQLiteOpenHelper).when(appDatabase).getOpenHelper();
        Mockito.doReturn("dummy_path").when(supportSQLiteOpenHelper).getDatabaseName();

        resetAppUtil.clearP2PDb();

        Mockito.verify(appDatabase).clearAllTables();
        Mockito.verify(supportSQLiteOpenHelper).getDatabaseName();
    }

    @Test
    public void performPreResetOperations() throws PreResetAppOperationException {
        PreResetAppCheck preResetAppCheck = Mockito.mock(PreResetAppCheck.class);
        resetAppUtil.performPreResetOperations(preResetAppCheck);

        Mockito.verify(preResetAppCheck).performPreResetAppOperations(Mockito.any());
    }

    @Test
    public void clearSharedPreferences() {
        SharedPreferences sharedPreferences = DrishtiApplication.getInstance().getContext().allSharedPreferences().getPreferences();
        sharedPreferences.edit().putString("1", "value");
        sharedPreferences.edit().putString("2", "value2");
        sharedPreferences.edit().putString("3", "value3");
        sharedPreferences.edit().putString("4", "value4");

        resetAppUtil.clearSharedPreferences();

        assertNull(sharedPreferences.getString("1", null));
        assertNull(sharedPreferences.getString("2", null));
        assertNull(sharedPreferences.getString("3", null));
        assertNull(sharedPreferences.getString("4", null));
    }

    @Test
    public void clearSqCipherDb() {
        Repository repository = DrishtiApplication.getInstance().getRepository();

        Mockito.doReturn(true).when(repository).deleteRepository();

        assertTrue(resetAppUtil.clearSqCipherDb());

        Mockito.verify(repository).deleteRepository();
    }
/*
    @Test
    public void clearAllPrivateKeyEntries() throws AppResetException, KeyStoreException {
        String password = "password080";
        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.user = new User("bei", "nice", password, "somesalt");

        DrishtiApplication.getInstance().getContext().userService().saveUserGroup("nice", password, loginResponseData);
        assertEquals(1, DrishtiApplication.getInstance().getContext().userService().getKeyStore().size());

        resetAppUtil.clearAllPrivateKeyEntries();

        assertEquals(0, DrishtiApplication.getInstance().getContext().userService().getKeyStore().size());
    }*/

    @Test
    public void addPreResetAppCheck() {
        PreResetAppCheck appCheck = Mockito.mock(PreResetAppCheck.class);

        assertFalse(resetAppUtil.removePreResetAppCheck(appCheck));
        assertTrue(resetAppUtil.addPreResetAppCheck(appCheck));
        assertTrue(resetAppUtil.removePreResetAppCheck(appCheck));
    }

    @Test
    public void testRemovePreResetAppCheck() {
        String appCheckName = "GIVE_UP";
        PreResetAppCheck appCheck = Mockito.mock(PreResetAppCheck.class);
        Mockito.doReturn(appCheckName).when(appCheck).getUniqueName();

        assertNull(resetAppUtil.removePreResetAppCheck(appCheckName));
        assertTrue(resetAppUtil.addPreResetAppCheck(appCheck));
        assertEquals(appCheck, resetAppUtil.removePreResetAppCheck(appCheckName));
    }

    @Test
    public void showProgressText() {
    }
}