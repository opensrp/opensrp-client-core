package org.smartregister;

import android.preference.PreferenceManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.configuration.ActivityStarter;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.FormActivity;

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

    @Test
    public void canGetCurrentDefaultModuleConfiguration() {
        Context context = Context.getInstance();
        CoreLibrary.init(context);
        ModuleConfiguration customLibraryConfiguration = new ModuleConfiguration.Builder("ONA Library",
                null, null, ActivityStarter.class) // Null values since we're not really interested in setting the actual values/classes
                .setModuleMetadata(new ModuleMetadata("opd_registration"
                        , "ec_client"
                        , "Opd Registration"
                        , "UPDATE OPD REGISTRATION",
                        null,
                        "custom-family",
                        FormActivity.class, BaseProfileActivity.class, false, ""
                ))
                .build();

        CoreLibrary.getInstance()
                .addModuleConfiguration(true, "custom-family", customLibraryConfiguration);
        Assert.assertEquals("custom-family", CoreLibrary.getInstance().getCurrentModuleName());
        Assert.assertEquals("ONA Library", CoreLibrary.getInstance().getCurrentModuleConfiguration().getRegisterTitle());
    }

}