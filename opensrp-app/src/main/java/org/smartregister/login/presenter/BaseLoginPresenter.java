package org.smartregister.login.presenter;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.domain.Setting;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.login.model.BaseLoginModel;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public abstract class BaseLoginPresenter implements BaseLoginContract.Presenter {

    private static final String TAG = BaseLoginPresenter.class.getCanonicalName();
    private WeakReference<BaseLoginContract.View> mLoginView;
    private BaseLoginContract.Interactor mLoginInteractor;
    private BaseLoginContract.Model mLoginModel;

    public BaseLoginPresenter(BaseLoginContract.View loginView) {
        mLoginView = new WeakReference<>(loginView);
        mLoginModel = new BaseLoginModel();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        mLoginView = null;//set to null on destroy
        // Inform interactor
        mLoginInteractor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mLoginInteractor = null;
            mLoginModel = null;
        }

    }

    @Override
    public void attemptLogin(String username, String password) {
        // Reset errors.
        getLoginView().resetUsernameError();
        getLoginView().resetPaswordError();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!mLoginModel.isPasswordValid(password)) {
            getLoginView().setPasswordError(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid username
        if (mLoginModel.isEmptyUsername(username)) {
            getLoginView().setUsernameError(R.string.error_field_required);
            cancel = true;
            getLoginView().enableLoginButton(true);
        }

        if (!cancel) {
            mLoginInteractor.login(mLoginView, username, password);

        }
    }

    @Override
    public BaseLoginContract.View getLoginView() {
        if (mLoginView != null) {
            return mLoginView.get();
        } else {
            return null;
        }
    }

    @Override
    public boolean isUserLoggedOut() {
        return mLoginModel.isUserLoggedOut();
    }

    @Override
    public void positionViews() {
        final ScrollView canvasSV = getLoginView().getActivityContext().findViewById(R.id.canvasSV);
        canvasSV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                canvasGlobalLayoutListenerProcessor(canvasSV, this);
            }

        });
    }



    protected abstract void canvasGlobalLayoutListenerProcessor(ScrollView canvasSV, ViewTreeObserver.OnGlobalLayoutListener layoutListener);

    @Override
    public abstract void processViewCustomizations();

    @Override
    public void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(mLoginModel.getOpenSRPContext()
                .applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        Resources resources = mLoginModel.getOpenSRPContext().applicationContext().getResources();

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(preferredLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    @Override
    public Context getOpenSRPContext() {
        return mLoginModel.getOpenSRPContext();
    }

    @Override
    public abstract boolean isSiteCharacteristicsSet();

    //for testing only, setter methods instead?
    public void setLoginInteractor(BaseLoginContract.Interactor mLoginInteractor) {
        this.mLoginInteractor = mLoginInteractor;
    }

    public void setLoginModel(BaseLoginContract.Model mLoginModel) {
        this.mLoginModel = mLoginModel;

    }

    protected abstract String getJsonViewFromPreference(String viewKey);
}
