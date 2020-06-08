package org.smartregister.view.activity;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.customshadows.AndroidTreeViewShadow;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;
import org.smartregister.shadows.AlarmManagerShadow;
import org.smartregister.shadows.PendingIntentShadow;
import org.smartregister.shadows.ShadowContext;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.mock.LoginActivityMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;

/**
 * Created by kaderchowdhury on 11/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
@Config(shadows = {ShadowContext.class, FontTextViewShadow.class, AndroidTreeViewShadow.class, PendingIntentShadow.class, AlarmManagerShadow.class})
public class LoginActivityTest extends BaseUnitTest {

    private ActivityController<LoginActivityMock> controller;

    @Mock
    private android.content.Context applicationContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private InputMethodManager inputManager;

    @Mock
    private AlarmManager alarmManager;

    @InjectMocks
    private LoginActivityMock activity;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        LoginActivityMock.inputManager = inputManager;
//        ShadowSystemClock shadowClock = new ShadowSystemClock();
//        shadowClock.setCurrentTimeMillis(142436987);

        DrishtiSyncScheduler.setReceiverClass(LoginActivityMock.class);

//        Context context = CoreLibrary.getInstance().context().updateApplicationContext(activity.getApplicationContext());
//        this.context_ = context;
        Mockito.doReturn(applicationContext).when(context_).applicationContext();
        Mockito.doReturn(context_).when(context_).updateApplicationContext(any(android.content.Context.class));
        Mockito.doReturn(userService).when(context_).userService();
        Mockito.doReturn(alarmManager).when(applicationContext).getSystemService(android.content.Context.ALARM_SERVICE);
        Mockito.doReturn("admin").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(true).when(inputManager).hideSoftInputFromWindow(isNull(IBinder.class), anyInt());
        Intent intent = new Intent(RuntimeEnvironment.application, LoginActivityMock.class);
        controller = Robolectric.buildActivity(LoginActivityMock.class, intent);
        controller.create()
                .start()
                .visible()
                .resume();
        activity = controller.get();

        Whitebox.setInternalState(activity, "context", context_);
    }


    @Test
    public void assertActivityNotNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void localLoginTest() {
        Mockito.doReturn(true).when(userService).hasARegisteredUser();
        Mockito.doReturn(true).when(userService).isValidLocalLogin(anyString(), any(char[].class));
        Mockito.doReturn(allSharedPreferences).when(context_).allSharedPreferences();

        EditText username = activity.findViewById(R.id.login_userNameText);
        EditText password = activity.findViewById(R.id.login_passwordText);

        username.setText("admin");
        password.setText("password");

        Button login_button = activity.findViewById(R.id.login_loginButton);
        login_button.performClick();

        Mockito.verify(userService, Mockito.atLeastOnce()).localLogin(anyString(), any(char[].class));

    }

    @After
    public void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }

        System.gc();
    }

}
