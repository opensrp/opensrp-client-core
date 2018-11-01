package org.smartregister.login.interactor;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.event.Listener;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.login.task.RemoteLoginTask;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;
import org.smartregister.sync.helper.CharacteristicsHelper;
import org.smartregister.util.NetworkUtils;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.TimeZone;

import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;

/**
 * Created by ndegwamartin on 26/06/2018.
 */
public abstract class BaseLoginInteractor implements BaseLoginContract.Interactor {

    private BaseLoginContract.Presenter mLoginPresenter;
    private RemoteLoginTask remoteLoginTask;

    private static final int MINIMUM_JOB_FLEX_VALUE = 1;

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
        loginWithLocalFlag(view, !getSharedPreferences().fetchForceRemoteLogin(), userName, password);
    }

    protected void loginWithLocalFlag(WeakReference<BaseLoginContract.View> view, boolean localLogin, String userName, String password) {

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
        if (getUserService().isUserInValidGroup(userName, password)
                && (!AllConstants.TIME_CHECK || TimeStatus.OK.equals(getUserService().validateStoredServerTimeZone()))) {
            localLoginWith(userName, password);
        } else {
            loginWithLocalFlag(view, false, userName, password);
        }
    }

    private void localLoginWith(String userName, String password) {

        getUserService().localLogin(userName, password);
        getLoginView().goToHome(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());
                if (NetworkUtils.isNetworkAvailable()) {
                    SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
                }
                Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }


    protected abstract void remoteLogin(final String userName, final String password);
    
    public abstract Context getApplicationContext();

    public AllSharedPreferences getSharedPreferences() {
        return mLoginPresenter.getOpenSRPContext().allSharedPreferences();
    }

    public BaseLoginContract.View getLoginView() {
        return mLoginPresenter.getLoginView();
    }

    public UserService getUserService() {
        return mLoginPresenter.getOpenSRPContext().userService();
    }

    protected abstract void scheduleJobs();


}
