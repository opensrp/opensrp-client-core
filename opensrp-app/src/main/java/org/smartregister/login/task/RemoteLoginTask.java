package org.smartregister.login.task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountConfiguration;
import org.smartregister.account.AccountHelper;
import org.smartregister.account.AccountResponse;
import org.smartregister.domain.LoginResponse;
import org.smartregister.event.Listener;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.Arrays;

import timber.log.Timber;

import static org.smartregister.domain.LoginResponse.CUSTOM_SERVER_RESPONSE;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class RemoteLoginTask extends AsyncTask<Void, Integer, LoginResponse> {

    private BaseLoginContract.View mLoginView;
    private final String mUsername;
    private final char[] mPassword;
    private final AccountAuthenticatorXml mAccountAuthenticatorXml;

    private final Listener<LoginResponse> afterLoginCheck;

    public RemoteLoginTask(BaseLoginContract.View loginView, String username, char[] password, AccountAuthenticatorXml accountAuthenticatorXml, Listener<LoginResponse> afterLoginCheck) {
        mLoginView = loginView;
        mUsername = username;
        mPassword = password;
        mAccountAuthenticatorXml = accountAuthenticatorXml;
        this.afterLoginCheck = afterLoginCheck;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoginView.showProgress(true);
    }

    @Override
    protected LoginResponse doInBackground(Void... params) {

        LoginResponse loginResponse;
        try {

            AccountConfiguration accountConfiguration = CoreLibrary.getInstance().context().getHttpAgent().fetchOAuthConfiguration();

            boolean isKeyclockConfigured = accountConfiguration != null;

            if (!isKeyclockConfigured) {
                accountConfiguration = new AccountConfiguration();
                accountConfiguration.setGrantTypesSupported(Arrays.asList(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD));
                accountConfiguration.setTokenEndpoint(CoreLibrary.getInstance().context().configuration().dristhiBaseURL() + AccountHelper.OAUTH.TOKEN_ENDPOINT);
                accountConfiguration.setAuthorizationEndpoint("");
                accountConfiguration.setIssuerEndpoint("");
            }

            if (accountConfiguration != null) {

                if (!accountConfiguration.getGrantTypesSupported().contains(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD))
                    throw new AccountsException("OAuth configuration DOES NOT support the Password Grant Type");

                //Persist config resources
                SharedPreferences.Editor sharedPrefEditor = CoreLibrary.getInstance().context().allSharedPreferences().getPreferences().edit();

                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL, accountConfiguration.getTokenEndpoint());
                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.AUTHORIZATION_ENDPOINT_URL, accountConfiguration.getAuthorizationEndpoint());
                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL, accountConfiguration.getIssuerEndpoint());
                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.USERINFO_ENDPOINT_URL, accountConfiguration.getUserinfoEndpoint());
                sharedPrefEditor.apply();

                AccountResponse response = CoreLibrary.getInstance().context().getHttpAgent().oauth2authenticate(mUsername, mPassword, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, accountConfiguration.getTokenEndpoint());

                AccountManager mAccountManager = CoreLibrary.getInstance().getAccountManager();

                final Account account = new Account(mUsername, mAccountAuthenticatorXml.getAccountType());

                loginResponse = getOpenSRPContext().userService().fetchUserDetails(response.getAccessToken());

                if (loginResponse != null && loginResponse.equals(LoginResponse.SUCCESS)) {

                    Bundle userData = getOpenSRPContext().userService().saveUserGroup(mUsername, mPassword, loginResponse.payload());

                    mAccountManager.addAccountExplicitly(account, response.getRefreshToken(), userData);
                    mAccountManager.setAuthToken(account, mLoginView.getAuthTokenType(), response.getAccessToken());
                    mAccountManager.setPassword(account, response.getRefreshToken());
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_GROUP_ID, userData.getString(AccountHelper.INTENT_KEY.ACCOUNT_GROUP_ID));

                    if (getOpenSRPContext().userService().getGroupId(mUsername) != null && CoreLibrary.getInstance().getSyncConfiguration().isSyncSettings()) {

                        publishProgress(R.string.loading_client_settings);

                        SyncSettingsServiceHelper syncSettingsServiceHelper = new SyncSettingsServiceHelper(getOpenSRPContext().configuration().dristhiBaseURL(), getOpenSRPContext().getHttpAgent());

                        try {
                            JSONArray settings = syncSettingsServiceHelper.pullSettingsFromServer(Utils.getFilterValue(loginResponse, CoreLibrary.getInstance().getSyncConfiguration().getSyncFilterParam()), response.getAccessToken());

                            JSONObject prefSettingsData = new JSONObject();
                            prefSettingsData.put(AllConstants.PREF_KEY.SETTINGS, settings);
                            loginResponse.setRawData(prefSettingsData);

                        } catch (JSONException e) {
                            Timber.e(e);
                        }

                    }
                }

            } else {
                throw new AccountsException("Could not fetch OAuth Configuration");
            }

        } catch (Exception e) {

            loginResponse = CUSTOM_SERVER_RESPONSE.withMessage(e.getMessage());
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

    protected JSONArray pullSetting(SyncSettingsServiceHelper syncSettingsServiceHelper, LoginResponse loginResponse) {
        JSONArray settings = new JSONArray();
        try {
            settings = syncSettingsServiceHelper.pullSettingsFromServer(Utils.getFilterValue(loginResponse, CoreLibrary.getInstance().getSyncConfiguration().getSyncFilterParam()));
        } catch (JSONException e) {
            Timber.e(e);
        }

        return settings;
    }
}

