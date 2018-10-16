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
import org.smartregister.login.contract.LoginContract;
import org.smartregister.login.interactor.LoginInteractor;
import org.smartregister.login.model.LoginModel;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getCanonicalName();
    private WeakReference<LoginContract.View> mLoginView;
    private LoginContract.Interactor mLoginInteractor;
    private LoginContract.Model mLoginModel;

    public LoginPresenter(LoginContract.View loginView) {
        mLoginView = new WeakReference<>(loginView);
        mLoginInteractor = new LoginInteractor(this);
        mLoginModel = new LoginModel();
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
    public LoginContract.View getLoginView() {
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


    protected void canvasGlobalLayoutListenerProcessor(ScrollView canvasSV, ViewTreeObserver.OnGlobalLayoutListener layoutListener) {
        final RelativeLayout canvasRL = getLoginView().getActivityContext().findViewById(R.id.login_layout);
        final LinearLayout logoCanvasLL = getLoginView().getActivityContext().findViewById(R.id.bottom_section);
        final LinearLayout credentialsCanvasLL = getLoginView().getActivityContext().findViewById(R.id.middle_section);

        canvasSV.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);

        int windowHeight = canvasSV.getHeight();
        int topMargin = (windowHeight / 2)
                - (credentialsCanvasLL.getHeight() / 2)
                - logoCanvasLL.getHeight();
        topMargin = topMargin / 2;

        RelativeLayout.LayoutParams logoCanvasLP = (RelativeLayout.LayoutParams) logoCanvasLL.getLayoutParams();
        logoCanvasLP.setMargins(0, topMargin, 0, 0);
        logoCanvasLL.setLayoutParams(logoCanvasLP);

        canvasRL.setMinimumHeight(windowHeight);
    }

    @Override
    public void processViewCustomizations() {
        try {
            String jsonString = getJsonViewFromPreference(Constants.VIEW_CONFIGURATION_PREFIX + Constants.CONFIGURATION.LOGIN);
            if (jsonString == null) {
                return;
            }

            ViewConfiguration loginView = AncApplication.getJsonSpecHelper().getConfigurableView(jsonString);
            LoginConfiguration metadata = (LoginConfiguration) loginView.getMetadata();
            LoginConfiguration.Background background = metadata.getBackground();

            CheckBox showPasswordCheckBox = getLoginView().getActivityContext().findViewById(R.id.login_show_password_checkbox);
            TextView showPasswordTextView = getLoginView().getActivityContext().findViewById(R.id.login_show_password_text_view);
            if (!metadata.getShowPasswordCheckbox()) {
                showPasswordCheckBox.setVisibility(View.GONE);
                showPasswordTextView.setVisibility(View.GONE);
            } else {
                showPasswordCheckBox.setVisibility(View.VISIBLE);
                showPasswordTextView.setVisibility(View.VISIBLE);
            }

            if (background.getOrientation() != null && background.getStartColor() != null && background.getEndColor() != null) {
                View loginLayout = getLoginView().getActivityContext().findViewById(R.id.login_layout);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setOrientation(
                        GradientDrawable.Orientation.valueOf(background.getOrientation()));
                gradientDrawable.setColors(new int[]{Color.parseColor(background.getStartColor()),
                        Color.parseColor(background.getEndColor())});
                loginLayout.setBackground(gradientDrawable);
            }

            if (metadata.getLogoUrl() != null) {
                ImageView imageView = getLoginView().getActivityContext().findViewById(R.id.login_logo);
                ImageLoaderRequest.getInstance(getLoginView().getActivityContext()).getImageLoader()
                        .get(metadata.getLogoUrl(), ImageLoader.getImageListener(imageView,
                                R.drawable.ic_who_logo, R.drawable.ic_who_logo)).getBitmap();
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

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
    public boolean isSiteCharacteristicsSet() {

        try {
            Setting setting = AncApplication.getInstance().getContext().allSettings().getSetting(Constants.PREF_KEY.SITE_CHARACTERISTICS);

            JSONArray settingArray = setting != null ? new JSONArray(setting.getValue()) : null;

            if (settingArray != null && settingArray.length() > 0) {

                JSONObject settingObject = settingArray.getJSONObject(0);// get first setting to test
                return !settingObject.isNull(Constants.KEY.VALUE);

            }
        } catch (JSONException e) {
            return false;
        }

        return false;
    }

    //for testing only, setter methods instead?
    public void setLoginInteractor(LoginContract.Interactor mLoginInteractor) {
        this.mLoginInteractor = mLoginInteractor;
    }

    public void setLoginModel(LoginContract.Model mLoginModel) {
        this.mLoginModel = mLoginModel;

    }

    protected String getJsonViewFromPreference(String viewKey) {
        return Utils.getPreference(getLoginView().getActivityContext(), viewKey, null);
    }
}
