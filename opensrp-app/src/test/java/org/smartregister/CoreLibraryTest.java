package org.smartregister;

import android.preference.PreferenceManager;

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
}