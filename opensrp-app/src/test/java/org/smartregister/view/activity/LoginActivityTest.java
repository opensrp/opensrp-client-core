package org.smartregister.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;

/**
 * Created by kaderchowdhury on 11/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
public class LoginActivityTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<LoginActivity> controller;

    @InjectMocks
    private LoginActivity activity;

    @Mock
    private org.smartregister.Context context_;
    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        Intent intent = new Intent(RuntimeEnvironment.application, LoginActivity.class);
        controller = Robolectric.buildActivity(LoginActivity.class, intent);
        activity = controller.get();
        Context context = CoreLibrary.getInstance().context().updateApplicationContext(activity.getApplicationContext());
        this.context_ = context;
        controller.start();
        controller.create();

    }

    @Test
    public void assertLoginTest() {
        EditText username = (EditText)activity.findViewById(R.id.login_userNameText);
        EditText password = (EditText)activity.findViewById(R.id.login_passwordText);
        username.setText("admin");
        password.setText("password");
//        Button login_button = (Button)activity.findViewById(R.id.login_loginButton);
     //   login_button.performClick();
        Assert.assertNotNull(activity);
    }

}
