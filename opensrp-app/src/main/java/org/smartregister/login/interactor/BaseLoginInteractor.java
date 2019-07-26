package org.smartregister.login.interactor;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.R;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.event.Listener;
import org.smartregister.job.P2pServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.task.RemoteLoginTask;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;
import org.smartregister.sync.helper.ServerSettingsHelper;
import org.smartregister.util.NetworkUtils;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public abstract class BaseLoginInteractor implements BaseLoginContract.Interactor {

    private BaseLoginContract.Presenter mLoginPresenter;

    private static final int MINIMUM_JOB_FLEX_VALUE = 5;

    private RemoteLoginTask remoteLoginTask;

    private static final String TAG = BaseLoginInteractor.class.getCanonicalName();

    public BaseLoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        this.mLoginPresenter = loginPresenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mLoginPresenter = null;
        }
    }

    @Override
    public void login(WeakReference<BaseLoginContract.View> view, String userName, String password) {
        loginWithLocalFlag(view, !getSharedPreferences().fetchForceRemoteLogin()
                && userName.equalsIgnoreCase(getSharedPreferences().fetchRegisteredANM()), userName, password);
    }

    public void loginWithLocalFlag(WeakReference<BaseLoginContract.View> view, boolean localLogin, String userName, String password) {

        getLoginView().hideKeyboard();
        getLoginView().enableLoginButton(false);
        if (localLogin) {
            localLogin(view, userName, password);
        } else {
            remoteLogin(userName, password);
        }

        Log.i(getClass().getName(), "Login result finished " + DateTime.now().toString());
    }

    private void localLogin(WeakReference<BaseLoginContract.View> view, String userName, String password) {
        getLoginView().enableLoginButton(true);
        boolean isAuthenticated = getUserService().isUserInValidGroup(userName, password);
        if (!isAuthenticated) {

            getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unauthorized));

        } else if (isAuthenticated && (!AllConstants.TIME_CHECK || TimeStatus.OK.equals(getUserService().validateStoredServerTimeZone()))) {

            navigateToHomePage(userName, password);

        } else {
            loginWithLocalFlag(view, false, userName, password);
        }
    }

    private void navigateToHomePage(String userName, String password) {

        getUserService().localLogin(userName, password);
        getLoginView().goToHome(false);

        CoreLibrary.getInstance().initP2pLibrary(userName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());

                scheduleJobsImmediately();

                Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }

    private void remoteLogin(final String userName, final String password) {

        try {
            if (getSharedPreferences().fetchBaseURL("").isEmpty() && StringUtils.isNotBlank(this.getApplicationContext().getString(R.string.opensrp_url))) {
                getSharedPreferences().savePreference("DRISHTI_BASE_URL", getApplicationContext().getString(R.string.opensrp_url));
            }
            if (!getSharedPreferences().fetchBaseURL("").isEmpty()) {
                tryRemoteLogin(userName, password, new Listener<LoginResponse>() {

                    public void onEvent(LoginResponse loginResponse) {
                        getLoginView().enableLoginButton(true);
                        if (loginResponse == LoginResponse.SUCCESS) {
                            if (getUserService().isUserInPioneerGroup(userName)) {
                                TimeStatus timeStatus = getUserService().validateDeviceTime(
                                        loginResponse.payload(), AllConstants.MAX_SERVER_TIME_DIFFERENCE
                                );
                                if (!AllConstants.TIME_CHECK || timeStatus.equals(TimeStatus.OK)) {

                                    remoteLoginWith(userName, password, loginResponse);

                                } else {
                                    if (timeStatus.equals(TimeStatus.TIMEZONE_MISMATCH)) {
                                        TimeZone serverTimeZone = UserService.getServerTimeZone(loginResponse.payload());

                                        getLoginView().showErrorDialog(getApplicationContext().getString(timeStatus.getMessage(),
                                                serverTimeZone.getDisplayName()));
                                    } else {
                                        getLoginView().showErrorDialog(getApplicationContext().getString(timeStatus.getMessage()));
                                    }
                                }
                            } else {
                                // Valid user from wrong group trying to log in
                                getLoginView().showErrorDialog(getApplicationContext().getString(R.string.unauthorized_group));
                            }
                        } else {
                            if (loginResponse == null) {
                                getLoginView().showErrorDialog("Sorry, your loginWithLocalFlag failed. Please try again");
                            } else {
                                if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                                    getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.no_internet_connectivity));
                                } else if (loginResponse == UNKNOWN_RESPONSE) {
                                    getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unknown_response));
                                } else if (loginResponse == UNAUTHORIZED) {
                                    getLoginView().showErrorDialog(getApplicationContext().getResources().getString(R.string.unauthorized));
                                } else {
                                    getLoginView().showErrorDialog(loginResponse.message());
                                }
                            }
                        }
                    }
                });
            } else {
                getLoginView().enableLoginButton(true);
                getLoginView().showErrorDialog("OpenSRP Base URL is missing. Please add it in Setting and try again");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

            getLoginView().showErrorDialog("Error occurred trying to loginWithLocalFlag in. Please try again...");
        }
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLogincheck) {
        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
            remoteLoginTask.cancel(true);
        }
        remoteLoginTask = new RemoteLoginTask(getLoginView(), userName, password, afterLogincheck);
        remoteLoginTask.execute();
    }

    private void remoteLoginWith(String userName, String password, LoginResponse loginResponse) {
        getUserService().remoteLogin(userName, password, loginResponse.payload());

        processServerSettings(loginResponse);

        scheduleJobsPeriodically();
        scheduleJobsImmediately();

        CoreLibrary.getInstance().initP2pLibrary(userName);

        getLoginView().goToHome(true);
    }

    public Context getApplicationContext() {
        return mLoginPresenter.getLoginView().getActivityContext();
    }

    public AllSharedPreferences getSharedPreferences() {
        return mLoginPresenter.getOpenSRPContext().allSharedPreferences();
    }

    public BaseLoginContract.View getLoginView() {
        return mLoginPresenter.getLoginView();
    }

    public UserService getUserService() {
        return mLoginPresenter.getOpenSRPContext().userService();
    }

    /**
     * Add all the metnods that should be scheduled remotely
     */
    protected abstract void scheduleJobsPeriodically();

    /**
     * Sync and pull unique ids are scheduleds by default.
     * Call super if you override this method.
     */
    protected void scheduleJobsImmediately() {
        P2POptions p2POptions = CoreLibrary.getInstance().getP2POptions();
        if (p2POptions != null && p2POptions.isEnableP2PLibrary()) {
            // Finish processing any unprocessed sync records here
            P2pServiceJob.scheduleJobImmediately(P2pServiceJob.TAG);
        }

        if (NetworkUtils.isNetworkAvailable()) {
            PullUniqueIdsServiceJob.scheduleJobImmediately(PullUniqueIdsServiceJob.TAG);
            SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        }
    }

    protected long getFlexValue(int value) {
        int minutes = MINIMUM_JOB_FLEX_VALUE;

        if (value > MINIMUM_JOB_FLEX_VALUE) {

            minutes = (int) Math.ceil(value / 3);
        }

        return minutes < MINIMUM_JOB_FLEX_VALUE ? MINIMUM_JOB_FLEX_VALUE : minutes;
    }

    //Always call super.processServerSettings( ) if you ever Override this
    protected void processServerSettings(LoginResponse loginResponse) {
        JSONObject data = loginResponse.getRawData();

        if (data != null) {
            try {

                JSONArray settings = data.has(AllConstants.PREF_KEY.SETTINGS) ? data.getJSONArray(AllConstants.PREF_KEY.SETTINGS) : null;

                if (settings != null && settings.length() > 0) {
                    ServerSettingsHelper.saveSetting(settings);
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());

            }
        }
    }
}