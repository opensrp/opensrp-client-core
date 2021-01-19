package org.smartregister;

import android.accounts.Account;
import android.preference.PreferenceManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;
import org.smartregister.shadows.ShadowAppDatabase;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-05-31
 */

@Config(shadows = {ShadowAppDatabase.class})
public class CoreLibraryTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private P2PAuthorizationService p2PAuthorizationService;

    @Mock
    private ReceiverTransferDao receiverTransferDao;

    @Mock
    private SenderTransferDao senderTransferDao;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void initP2pLibrary() {
        String expectedUsername = "nurse1";
        String expectedTeamIdPassword = "908980dslkjfljsdlf";

        Mockito.doReturn(RuntimeEnvironment.application)
                .when(context)
                .applicationContext();

        AllSharedPreferences allSharedPreferences
                = new AllSharedPreferences(
                PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext())
        );

        allSharedPreferences.updateANMUserName(expectedUsername);
        allSharedPreferences.saveDefaultTeamId(expectedUsername, expectedTeamIdPassword);


        P2PLibrary.Options p2POptions = new P2PLibrary.Options(context.applicationContext(), expectedTeamIdPassword, expectedUsername, p2PAuthorizationService, receiverTransferDao, senderTransferDao);
        P2PLibrary.init(p2POptions);
        P2PLibrary p2PLibrary = P2PLibrary.getInstance();

        assertEquals(expectedUsername, p2PLibrary.getUsername());
    }

    @Test(expected = IllegalStateException.class)
    public void getInstanceShouldThrowException() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);

        CoreLibrary.getInstance();
    }

    @Test
    public void checkPlatformMigrationsShouldLogoutUserWhenUserIsLoggedIn() {
        UserService userService = Mockito.spy(CoreLibrary.getInstance().context().userService());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "userService", userService);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().userService().getAllSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context().userService(), "allSharedPreferences", allSharedPreferences);

        // Mock calls
        Mockito.doReturn("demo").when(allSharedPreferences).fetchPioneerUser();
        Mockito.doReturn(0).when(allSharedPreferences).getDBEncryptionVersion();

        ReflectionHelpers.callInstanceMethod(CoreLibrary.getInstance(), "checkPlatformMigrations");

        Mockito.verify(userService).logoutSession();
        Mockito.verify(userService).forceRemoteLogin(Mockito.nullable(String.class));
        Mockito.verify(allSharedPreferences).migratePassphrase();
    }

    @Test
    public void resetShouldDoNothingWhenContextPassedIsNull() {
        CoreLibrary coreLibrary = CoreLibrary.getInstance();

        CoreLibrary.reset(null, null);

        assertEquals(coreLibrary, CoreLibrary.getInstance());
    }

    @Test
    public void resetShouldCreateNewCoreLibraryInstanceWhenGivenContextAndSyncConfiguration() {
        CoreLibrary coreLibrary = CoreLibrary.getInstance();
        Context context = CoreLibrary.getInstance().context();
        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);

        CoreLibrary.reset(context, syncConfiguration);

        Assert.assertNotEquals(coreLibrary, CoreLibrary.getInstance());
    }

    @Test
    public void setEcClientFieldsFileShouldUpdateField() {
        String oldValue = ReflectionHelpers.getField(CoreLibrary.getInstance(), "ecClientFieldsFile");

        String newValue = "ec_client_fields_new.json";
        CoreLibrary.getInstance().setEcClientFieldsFile(newValue);

        Assert.assertNotEquals(oldValue, newValue);
        assertEquals(newValue, ReflectionHelpers.getField(CoreLibrary.getInstance(), "ecClientFieldsFile"));
    }

    @Test
    public void onAccountsUpdatedShouldLogoutUserWhenGivenAccountsNotContainingCurrentLoggedInUser() {
        Account[] accounts = new Account[1];
        Account account = new Account("demo1", "org.smartregister.core");
        accounts[0] = account;

        UserService userService = Mockito.spy(CoreLibrary.getInstance().context().userService());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "userService", userService);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        // Mock calls to class methods
        Mockito.doReturn(1).when(allSharedPreferences).getDBEncryptionVersion();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();

        // Call the actual method
        CoreLibrary.getInstance().onAccountsUpdated(accounts);

        // Verify the logout methods
        Mockito.verify(userService).logoutSession();
        Mockito.verify(userService).forceRemoteLogin(Mockito.nullable(String.class));
    }

    @Test
    public void onAccountsUpdatedShouldDoNothingWhenGivenAccountsContainCurrentLoggedInUser() {
        Account[] accounts = new Account[1];
        Account account = new Account("demo", "org.smartregister.core");
        accounts[0] = account;

        UserService userService = Mockito.spy(CoreLibrary.getInstance().context().userService());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "userService", userService);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        // Mock calls to class methods
        Mockito.doReturn(1).when(allSharedPreferences).getDBEncryptionVersion();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();

        // Call the actual method
        CoreLibrary.getInstance().onAccountsUpdated(accounts);

        // Verify the logout methods
        Mockito.verify(userService, Mockito.times(0)).logoutSession();
        Mockito.verify(userService, Mockito.times(0)).forceRemoteLogin(Mockito.nullable(String.class));
    }

    @Test
    public void constructorShouldInitialisePathEvaluatorLibrary() {
        Assert.assertNull(ReflectionHelpers.getStaticField(PathEvaluatorLibrary.class, "instance"));

        TestSyncConfiguration testSyncConfiguration = Mockito.spy(new TestSyncConfiguration());
        Mockito.doReturn(true).when(testSyncConfiguration).runPlanEvaluationOnClientProcessing();
        new CoreLibrary(Context.getInstance(), testSyncConfiguration, null);

        Assert.assertNotNull(ReflectionHelpers.getStaticField(PathEvaluatorLibrary.class, "instance"));
        Assert.assertNotNull(PathEvaluatorLibrary.getInstance().getLocationProvider().getLocationDao());
        Assert.assertNotNull(PathEvaluatorLibrary.getInstance().getClientProvider().getClientDao());
        Assert.assertNotNull(PathEvaluatorLibrary.getInstance().getTaskProvider().getTaskDao());
        Assert.assertNotNull(PathEvaluatorLibrary.getInstance().getEventProvider().getEventDao());
    }
}