package org.smartregister.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.SyncUtils;
import org.smartregister.view.contract.BaseLoginContract;

import static org.robolectric.util.ReflectionHelpers.ClassParameter.from;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 23-06-2020.
 */
public class BaseLoginActivityTest extends BaseRobolectricUnitTest {

    private BaseLoginActivity baseLoginActivity;

    private ActivityController<BaseLoginActivityImpl> controller;

    @BeforeClass
    public static void resetCoreLibrary() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Intent intent = new Intent(RuntimeEnvironment.application, BaseLoginActivityImpl.class);
        controller = Robolectric.buildActivity(BaseLoginActivityImpl.class);
        BaseLoginActivityImpl spyActivity = Mockito.spy((BaseLoginActivityImpl) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);

        Mockito.doReturn(RuntimeEnvironment.application.getPackageManager()).when(spyActivity).getPackageManager();

        controller.create()
                .start()
                .resume();
        baseLoginActivity = Mockito.spy(controller.get());
    }

    @Test
    public void onCreateShouldCallSetupOperations() {
        // Setup again for
        controller = Robolectric.buildActivity(BaseLoginActivityImpl.class);
        ActionBar actionBar = Mockito.mock(ActionBar.class);

        BaseLoginActivityImpl spyActivity = Mockito.spy((BaseLoginActivityImpl) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);

        Mockito.doReturn(RuntimeEnvironment.application.getPackageManager()).when(spyActivity).getPackageManager();
        Mockito.doReturn(actionBar).when(spyActivity).getSupportActionBar();
        baseLoginActivity = controller.get();


        ReflectionHelpers.callInstanceMethod(Activity.class, baseLoginActivity, "performCreate", from(Bundle.class, null));

        Mockito.verify(actionBar).setDisplayShowHomeEnabled(Mockito.eq(false));
        Mockito.verify(actionBar).setDisplayShowTitleEnabled(Mockito.eq(false));
        Mockito.verify(actionBar).setBackgroundDrawable(Mockito.any(Drawable.class));

        Mockito.verify(baseLoginActivity.mLoginPresenter).setLanguage();
        Mockito.verify(baseLoginActivity).initializePresenter();
        Mockito.verify(baseLoginActivity.mLoginPresenter).positionViews();
        Mockito.verify(baseLoginActivity).renderBuildInfo();
        Assert.assertNotNull((SyncUtils) ReflectionHelpers.getField(baseLoginActivity, "syncUtils"));
        Assert.assertNotNull((ProgressDialog) ReflectionHelpers.getField(baseLoginActivity, "progressDialog"));
    }

    @Test
    public void onClickLoginShouldCallAttemptLogin() {
        View view = Mockito.mock(View.class);
        Mockito.doReturn(R.id.login_login_btn).when(view).getId();

        baseLoginActivity.onClick(view);
        Mockito.verify(baseLoginActivity.mLoginPresenter).attemptLogin(Mockito.anyString(), Mockito.anyString());
    }


    @Test
    public void onEditorActionShouldShouldCallAttemptLoginAndReturnTrueWhenActionIsDone() {
        View view = Mockito.mock(View.class);
        Mockito.doReturn(R.id.login_login_btn).when(view).getId();

        Assert.assertTrue(baseLoginActivity.onEditorAction(null, EditorInfo.IME_ACTION_DONE, null));
        Mockito.verify(baseLoginActivity.mLoginPresenter).attemptLogin(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void enableLoginButtonShouldMakeLoginBtnClickable() {
        boolean isClickable = false;
        baseLoginActivity.enableLoginButton(isClickable);
        Button btn = ReflectionHelpers.getField(baseLoginActivity, "loginButton");
        Assert.assertFalse(btn.isClickable());
    }

    @Test
    public void setUsernameErrorShouldCallSetErrorAndShowErrorDialog() {
        baseLoginActivity.setUsernameError(R.string.error_invalid_username);

        EditText usernameEt = ReflectionHelpers.getField(baseLoginActivity, "userNameEditText");

        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.error_invalid_username), usernameEt.getError());
        Mockito.verify(baseLoginActivity).showErrorDialog(Mockito.eq(RuntimeEnvironment.application.getString(R.string.unauthorized)));
    }


    static class BaseLoginActivityImpl extends BaseLoginActivity {

        @Override
        protected int getContentView() {
            return R.layout.activity_login;
        }

        @Override
        protected void initializePresenter() {
            mLoginPresenter = Mockito.mock(BaseLoginContract.Presenter.class);

        }

        @Override
        public void goToHome(boolean isRemote) {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            setTheme(R.style.AppTheme); //we need this here
            super.onCreate(savedInstanceState);
        }
    }

}