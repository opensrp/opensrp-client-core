package org.smartregister.presenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by Vincent Karuri on 10/03/2020
 */
public class BaseLoginPresenterTest extends BaseRobolectricUnitTest {

    BaseLoginContract.Presenter presenter;

    private BaseLoginActivityImpl activity;

    @Before
    public void setUp() {

        activity = Robolectric.buildActivity(BaseLoginActivityImpl.class).create().get();
        presenter = Mockito.spy(activity.getBaseLoginPresenter());
    }

    @Before
    public void tearDown() {
        activity.finish();
    }

    @Test
    public void testAttemptLoginShouldFailForUnauthorizedApp() {
        BaseLoginActivityImpl activitySpy = Mockito.spy(activity);
        Mockito.doReturn(false).when(activitySpy).isAppVersionAllowed();
        Mockito.doReturn(activitySpy).when(presenter).getLoginView();

        presenter.attemptLogin("", "".toCharArray());

        verify(activitySpy).showErrorDialog(any());
    }

    public static class BaseLoginActivityImpl extends BaseLoginActivity {

        @Override
        protected int getContentView() {
            return R.layout.activity_login;
        }

        @Override
        protected void initializePresenter() {
            mLoginPresenter = new TestLoginPresenter();

        }

        @Override
        public void goToHome(boolean isRemote) {
            //Do nothing
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar); //we need this here
            super.onCreate(savedInstanceState);
        }

        public BaseLoginContract.Presenter getBaseLoginPresenter() {
            return mLoginPresenter;
        }
    }
}
