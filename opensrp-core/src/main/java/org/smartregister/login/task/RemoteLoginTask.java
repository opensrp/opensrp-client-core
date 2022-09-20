package org.smartregister.login.task;

import static org.smartregister.domain.LoginResponse.CUSTOM_SERVER_RESPONSE;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountConfiguration;
import org.smartregister.account.AccountError;
import org.smartregister.account.AccountHelper;
import org.smartregister.account.AccountResponse;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.jsonmapping.User;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;
import org.smartregister.util.EasyMap;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class RemoteLoginTask {

    private final String mUsername;
    private final char[] mPassword;
    private final AccountAuthenticatorXml mAccountAuthenticatorXml;
    private final Listener<LoginResponse> afterLoginCheck;
    private boolean cancelled;
    private BaseLoginContract.View mLoginView;

    public RemoteLoginTask(BaseLoginContract.View loginView, String username, char[] password, AccountAuthenticatorXml accountAuthenticatorXml, Listener<LoginResponse> afterLoginCheck) {
        mLoginView = loginView;
        mUsername = username;
        mPassword = password;
        mAccountAuthenticatorXml = accountAuthenticatorXml;
        this.afterLoginCheck = afterLoginCheck;
    }

    public static Context getOpenSRPContext() {
        return CoreLibrary.getInstance().context();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel(boolean cancelled) {
        this.cancelled = cancelled;
        mLoginView.showProgress(!cancelled);
    }

    public void execute() {

        mLoginView.showProgress(true);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {

            LoginResponse loginResponse = doInBackground();

            mLoginView.getAppCompatActivity().runOnUiThread(() -> {

                mLoginView.showProgress(false);
                afterLoginCheck.onEvent(loginResponse);

            });

        });

    }

    protected LoginResponse doInBackground() {

        LoginResponse loginResponse;
        try {

            AccountConfiguration accountConfiguration = getOpenSRPContext().getHttpAgent().fetchOAuthConfiguration();

            boolean isKeycloakConfigured = accountConfiguration != null;

            //Persist config resources
            AllSharedPreferences allSharedPreferences = getOpenSRPContext().allSharedPreferences();
            SharedPreferences.Editor sharedPrefEditor = allSharedPreferences.getPreferences().edit();
            sharedPrefEditor.putBoolean(AccountHelper.CONFIGURATION_CONSTANTS.IS_KEYCLOAK_CONFIGURED, isKeycloakConfigured);
            sharedPrefEditor.apply();


            if (!isKeycloakConfigured) {
                accountConfiguration = new AccountConfiguration();
                accountConfiguration.setGrantTypesSupported(Arrays.asList(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD));
                accountConfiguration.setTokenEndpoint(getOpenSRPContext().configuration().dristhiBaseURL() + AccountHelper.OAUTH.TOKEN_ENDPOINT);
                accountConfiguration.setAuthorizationEndpoint("");
                accountConfiguration.setIssuerEndpoint("");
            }

            if (accountConfiguration != null) {

                if (!accountConfiguration.getGrantTypesSupported().contains(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD))
                    throw new AccountsException("OAuth configuration DOES NOT support the Password Grant Type");

                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL, accountConfiguration.getTokenEndpoint());
                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.AUTHORIZATION_ENDPOINT_URL, accountConfiguration.getAuthorizationEndpoint());
                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL, accountConfiguration.getIssuerEndpoint());
                sharedPrefEditor.putString(AccountHelper.CONFIGURATION_CONSTANTS.USERINFO_ENDPOINT_URL, accountConfiguration.getUserinfoEndpoint());
                sharedPrefEditor.apply();

                AccountResponse response = getOpenSRPContext().getHttpAgent().oauth2authenticate(mUsername, mPassword, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, accountConfiguration.getTokenEndpoint());

                if (response.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST && response.getAccountError() != null) {

                    if (AccountError.ACCOUNT_NOT_FULLY_SETUP.equals(response.getAccountError().getErrorDescription())) {
                        return LoginResponse.INVALID_GRANT.withRawData(new JSONObject(EasyMap.mapOf(AccountHelper.CONFIGURATION_CONSTANTS.ISSUER_ENDPOINT_URL, accountConfiguration.getIssuerEndpoint())));
                    } else if (AccountError.INVALID_CLIENT.equals(response.getAccountError().getError())) {
                        return LoginResponse.UNAUTHORIZED_CLIENT;
                    }
                }

                AccountManager mAccountManager = CoreLibrary.getInstance().getAccountManager();

                loginResponse = getOpenSRPContext().userService().fetchUserDetails(response.getAccessToken());

                if (loginResponse != null && loginResponse.equals(LoginResponse.SUCCESS)) {
                    User user = loginResponse.payload() != null ? loginResponse.payload().user : null;
                    String username = user != null && StringUtils.isNotBlank(user.getUsername())
                            ? user.getUsername() : mUsername;

                    Bundle userData = getOpenSRPContext().userService().saveUserCredentials(username, mPassword, loginResponse.payload());

                    final Account account = new Account(username, mAccountAuthenticatorXml.getAccountType());

                    mAccountManager.addAccountExplicitly(account, response.getRefreshToken(), userData);
                    mAccountManager.setAuthToken(account, mLoginView.getAuthTokenType(), response.getAccessToken());
                    mAccountManager.setPassword(account, response.getRefreshToken());
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_LOCAL_PASSWORD, userData.getString(AccountHelper.INTENT_KEY.ACCOUNT_LOCAL_PASSWORD));
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_LOCAL_PASSWORD_SALT, userData.getString(AccountHelper.INTENT_KEY.ACCOUNT_LOCAL_PASSWORD_SALT));
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_NAME, userData.getString(AccountHelper.INTENT_KEY.ACCOUNT_NAME));
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_REFRESH_TOKEN, response.getRefreshToken());
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_ACCESS_TOKEN_EXPIRES_IN, Utils.toStringNullable(response.getExpiresIn()));
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_REFRESH_TOKEN_CREATED_AT, Utils.toStringNullable(Calendar.getInstance().getTimeInMillis()));
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_REFRESH_TOKEN_EXPIRES_IN, Utils.toStringNullable(response.getRefreshExpiresIn()));
                    mAccountManager.setUserData(account, AccountHelper.INTENT_KEY.ACCOUNT_ROLES, user.getRoles() != null ? user.getRoles().toString() : Collections.EMPTY_LIST.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mAccountManager.notifyAccountAuthenticated(account);
                    }

                    if (getOpenSRPContext().userService().getDecryptedPassphraseValue(username) != null && CoreLibrary.getInstance().getSyncConfiguration().isSyncSettings()) {

                        mLoginView.getAppCompatActivity().runOnUiThread(() -> mLoginView.updateProgressMessage(getOpenSRPContext().applicationContext().getString(R.string.loading_client_settings)));

                        SyncSettingsServiceHelper syncSettingsServiceHelper = new SyncSettingsServiceHelper(getOpenSRPContext().configuration().dristhiBaseURL(), getOpenSRPContext().getHttpAgent());

                        try {
                            JSONArray settings = pullSetting(syncSettingsServiceHelper, loginResponse, response.getAccessToken());

                            JSONObject prefSettingsData = new JSONObject();
                            prefSettingsData.put(AllConstants.PREF_KEY.SETTINGS, settings);
                            loginResponse.setRawData(prefSettingsData);

                        } catch (JSONException e) {
                            Timber.e(e);
                        }

                    }

                    allSharedPreferences.updateLastAuthenticationHttpStatus(0);
                } else {
                    if (response.getAccountError() != null && response.getAccountError().getError() != null
                            && !response.getAccountError().getError().equalsIgnoreCase(LoginResponse.INVALID_GRANT.name())) {
                        return LoginResponse.valueOf(response.getAccountError().getError().toUpperCase(Locale.ENGLISH));
                    } else if (loginResponse == null) {
                        return null;
                    }
                }

            } else {
                throw new AccountsException("Could not fetch OAuth Configuration");
            }

        } catch (Exception e) {

            loginResponse = CUSTOM_SERVER_RESPONSE.withMessage(getOpenSRPContext().applicationContext().getResources().getString(R.string.unknown_response));

            Timber.e(e);
        }

        return loginResponse;
    }

    protected JSONArray pullSetting(SyncSettingsServiceHelper syncSettingsServiceHelper, LoginResponse loginResponse, String accessToken) {
        JSONArray settings = new JSONArray();
        try {
            settings = syncSettingsServiceHelper.pullSettingsFromServer(Utils.getFilterValue(loginResponse, CoreLibrary.getInstance().getSyncConfiguration().getSyncFilterParam()), accessToken);
        } catch (JSONException e) {
            Timber.e(e);
        }

        return settings;
    }
}

