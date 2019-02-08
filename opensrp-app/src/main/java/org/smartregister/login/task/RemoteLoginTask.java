package org.smartregister.login.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.LoginResponse;
import org.smartregister.event.Listener;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class RemoteLoginTask extends AsyncTask<Void, Integer, LoginResponse> {

    private final String TAG = RemoteLoginTask.class.getCanonicalName();

    private BaseLoginContract.View mLoginView;
    private final String mUsername;
    private final String mPassword;

    private final Listener<LoginResponse> afterLoginCheck;

    public RemoteLoginTask(BaseLoginContract.View loginView, String username, String password, Listener<LoginResponse> afterLoginCheck) {
        mLoginView = loginView;
        mUsername = username;
        mPassword = password;
        this.afterLoginCheck = afterLoginCheck;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoginView.showProgress(true);
    }

    @Override
    protected LoginResponse doInBackground(Void... params) {
        LoginResponse loginResponse = getOpenSRPContext().userService().isValidRemoteLogin(mUsername, mPassword);
        if (loginResponse != null && loginResponse.equals(LoginResponse.SUCCESS) && getOpenSRPContext().userService().getGroupId(mUsername) != null && CoreLibrary.getInstance().getSyncConfiguration().isSyncSettings()) {


            publishProgress(R.string.loading_client_settings);

            SyncSettingsServiceHelper syncSettingsServiceHelper = new SyncSettingsServiceHelper(getOpenSRPContext().configuration().dristhiBaseURL(), getOpenSRPContext().getHttpAgent());
            syncSettingsServiceHelper.setUsername(mUsername);
            syncSettingsServiceHelper.setPassword(mPassword);

            try {
                JSONArray settings = syncSettingsServiceHelper.pullSettingsFromServer(Utils.getFilterValue(loginResponse, CoreLibrary.getInstance().getSyncConfiguration().getSyncFilterParam()));

                JSONObject data = new JSONObject();
                data.put(AllConstants.PREF_KEY.SETTINGS, settings);
                loginResponse.setRawData(data);

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

        }

        return loginResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... messageIdentifier) {

        mLoginView.updateProgressMessage(getOpenSRPContext().applicationContext().getString(messageIdentifier[0]));

    }

    @Override
    protected void onPostExecute(final LoginResponse loginResponse) {
        super.onPostExecute(loginResponse);

        mLoginView.showProgress(false);
        afterLoginCheck.onEvent(loginResponse);
    }

    @Override
    protected void onCancelled() {
        mLoginView.showProgress(false);
    }

    public static Context getOpenSRPContext() {
        return CoreLibrary.getInstance().context();
    }

}

