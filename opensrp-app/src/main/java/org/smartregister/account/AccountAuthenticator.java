package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.LoginActivity;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;

    public AccountAuthenticator(Context context) {
        super(context);

        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountHelper.INTENT_KEY.ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountHelper.INTENT_KEY.AUTH_TYPE, authTokenType);
        intent.putExtra(AccountHelper.INTENT_KEY.IS_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        Timber.d("getAuthToken");

        AccountManager accountManager = CoreLibrary.getInstance().getAccountManager();

        String authToken = accountManager.peekAuthToken(account, authTokenType);
        String refreshToken = "";
        Timber.d("peekAuthToken " + authToken);

        if (TextUtils.isEmpty(authToken)) {
            final String password = accountManager.getPassword(account);
            if (password != null) {
                try {

                    Timber.d("Authenticate with saved credentials");

                    AccountResponse accountResponse = CoreLibrary.getInstance().context().getHttpAgent().oauth2authenticate(account.name, password, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, CoreLibrary.getInstance().context().allSettings().get(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL));
                    authToken = accountResponse.getAccessToken();
                    refreshToken = accountResponse.getRefreshToken();

                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(AccountHelper.KEY_REFRESH_TOKEN, refreshToken);
            return result;
        }

        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountHelper.INTENT_KEY.ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountHelper.INTENT_KEY.AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return authTokenType.toUpperCase();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getRefreshToken() {
        return AccountHelper.getAccountManagerValue(AccountHelper.KEY_REFRESH_TOKEN, CoreLibrary.getInstance().getAccountAuthenticatorXml().getAccountType());
    }
}
