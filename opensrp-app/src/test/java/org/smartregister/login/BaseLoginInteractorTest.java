package org.smartregister.login;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.shadows.LoginInteractorShadow;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 8/4/20.
 */

public class BaseLoginInteractorTest extends BaseRobolectricUnitTest {

    @InjectMocks
    private LoginInteractorShadow interactor;

    @Mock
    private BaseLoginContract.Presenter presenter;

    @Mock
    private BaseLoginContract.View view;

    @Mock
    private Context context;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private Activity activity;

    @Before
    public void setUp() {
        when(presenter.getOpenSRPContext()).thenReturn(context);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(presenter.getLoginView()).thenReturn(view);
        activity = Robolectric.buildActivity(AppCompatActivity.class).create().get();
        when(view.getActivityContext()).thenReturn(activity);
    }

    @Test
    public void testOnDestroyShouldSetPresenterNull() {
        assertNotNull(Whitebox.getInternalState(interactor, "mLoginPresenter"));
        interactor.onDestroy(false);
        assertNull(Whitebox.getInternalState(interactor, "mLoginPresenter"));
    }

    @Test
    public void testLocalAttemptsRemoteLoginAndErrorsWithBaseURLIsMissing() {
        when(allSharedPreferences.fetchBaseURL("")).thenReturn("");
        interactor.login(new WeakReference<>(view), "johndoe", "pass");
        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(allSharedPreferences).savePreference("DRISHTI_BASE_URL", activity.getString(R.string.opensrp_url));
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog("OpenSRP Base URL is missing. Please add it in Setting and try again");
    }


}
