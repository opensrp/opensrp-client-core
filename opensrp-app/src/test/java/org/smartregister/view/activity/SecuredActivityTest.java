package org.smartregister.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.R;
import org.smartregister.TestApplication;
import org.smartregister.broadcastreceivers.OpenSRPClientBroadCastReceiver;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;
import org.smartregister.event.Event;
import org.smartregister.event.Listener;
import org.smartregister.util.Session;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static org.robolectric.util.ReflectionHelpers.ClassParameter.from;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 14-07-2020.
 */
@Config(application = SecuredActivityTest.TestP2pApplication.class)
public class SecuredActivityTest  extends BaseRobolectricUnitTest {

    private SecuredActivity securedActivity;

    private ActivityController<SecuredActivityImpl> controller;

    @BeforeClass
    public static void resetCoreLibrary() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Before
    public void setUp() {
        Context.bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        CommonRepositoryInformationHolder bt = new CommonRepositoryInformationHolder("BINDTYPENAME", new String[]{"A", "B"});
        Context.bindtypes.add(bt);

        // Make sure the user is logged in
        Session session = ReflectionHelpers.getField(CoreLibrary.getInstance().context().userService(), "session");
        session.setPassword("");
        session.start(360 * 60 * 1000);

        org.mockito.MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(SecuredActivityImpl.class);
        SecuredActivityImpl spyActivity = Mockito.spy((SecuredActivityImpl) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);

        Mockito.doReturn(RuntimeEnvironment.application.getPackageManager()).when(spyActivity).getPackageManager();

        controller.create()
                .start()
                .resume();
        securedActivity = Mockito.spy(controller.get());
    }

    @Test
    public void onCreateShouldCallOnCreationAndAddLogoutListener() {
        List<WeakReference<Listener<Boolean>>> listeners =  ReflectionHelpers.getField(Event.ON_LOGOUT, "listeners");
        listeners.clear();

        controller = Robolectric.buildActivity(SecuredActivityImpl.class);
        SecuredActivityImpl spyActivity = Mockito.spy((SecuredActivityImpl) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);
        securedActivity = controller.get();
        ReflectionHelpers.callInstanceMethod(Activity.class, securedActivity, "performCreate", from(Bundle.class, null));

        Mockito.verify(securedActivity).onCreation();
        listeners =  ReflectionHelpers.getField(Event.ON_LOGOUT, "listeners");
        Assert.assertEquals(1, listeners.size());
    }

    @Test
    public void onResume() {
    }

    @Test
    public void onOptionsItemSelectedShouldLogoutUserWhenLogoutIsClicked() {
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Mockito.doReturn(securedActivity.MENU_ITEM_LOGOUT).when(menuItem).getItemId();

        TestP2pApplication testP2pApplication = Mockito.spy((TestP2pApplication) securedActivity.getApplication());
        Mockito.doReturn(testP2pApplication).when(securedActivity).getApplication();

        Assert.assertFalse(securedActivity.onOptionsItemSelected(menuItem));

        Mockito.verify(testP2pApplication).logoutCurrentUser();
    }


    @Test
    public void onOptionsItemSelectedShouldSwitchLanguageWhenSwitchLangItemIsClicked() {
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Mockito.doReturn(R.id.switchLanguageMenuItem).when(menuItem).getItemId();

        Assert.assertEquals("en", CoreLibrary.getInstance().context().userService().getAllSharedPreferences().fetchLanguagePreference());

        Assert.assertFalse(securedActivity.onOptionsItemSelected(menuItem));

        Assert.assertEquals("kn", CoreLibrary.getInstance().context().userService().getAllSharedPreferences().fetchLanguagePreference());
        String toastMsg = ShadowToast.getTextOfLatestToast();
        Assert.assertEquals("Language preference set to Kannada.", toastMsg);
    }

    @Test
    public void onPauseShouldUnregisterReceivers() {
        ShadowLocalBroadcastManager shadowLocalBroadcastManager = Shadow.extract(LocalBroadcastManager.getInstance(RuntimeEnvironment.application));
        Assert.assertEquals(1, shadowLocalBroadcastManager.getRegisteredBroadcastReceivers().size());

        securedActivity.onPause();

        Assert.assertNull(ReflectionHelpers.getField(securedActivity, "openSRPClientBroadCastReceiver"));
        Mockito.verify(securedActivity).removeProcessingInProgressSnackbar();
        Mockito.verify(securedActivity).unregisterReceiver(Mockito.any(OpenSRPClientBroadCastReceiver.class));

        Assert.assertEquals(0, shadowLocalBroadcastManager.getRegisteredBroadcastReceivers().size());
    }

    static class SecuredActivityImpl extends SecuredActivity {

        @Override
        protected void onCreation() {
            setTheme(R.style.AppTheme); //we need this here
            setContentView(R.layout.activity_login);

        }

        @Override
        protected void onResumption() {

        }
    }

    static class TestP2pApplication extends TestApplication {

        @Override
        public void onCreate() {
            mInstance = this;
            context = Context.getInstance();
            context.updateApplicationContext(getApplicationContext());

            CoreLibrary.init(context, null, 1588062490000l, new P2POptions(true));

            setTheme(R.style.Theme_AppCompat_NoActionBar); //or just R.style.Theme_AppCompat
        }
    }
}