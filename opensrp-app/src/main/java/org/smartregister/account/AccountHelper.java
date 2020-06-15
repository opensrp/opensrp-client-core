package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;

import org.smartregister.CoreLibrary;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountHelper {

    private static AccountManager accountManager = CoreLibrary.getInstance().getAccountManager();

    public final static int MAX_AUTH_RETRIES = 1;


    public static final class CONFIGURATION_CONSTANTS {

        public final static String TOKEN_ENDPOINT_URL = "token_endpoint_url";
        public final static String AUTHORIZATION_ENDPOINT_URL = "authorization_endpoint_url";
        public final static String ISSUER_ENDPOINT_URL = "issuer_endpoint_url";
        public static final String USERINFO_ENDPOINT_URL = "userinfo_endpoint_url";
    }

    public static final class OAUTH {

        public final static String ACCOUNT_CONFIGURATION_ENDPOINT = "/rest/config/keycloak";
        public final static String TOKEN_ENDPOINT = "/oauth/token";

        public static final class GRANT_TYPE {
            public final static String PASSWORD = "password";
            public final static String REFRESH_TOKEN = "refresh_token";

        }
    }

    public static final class INTENT_KEY {

        public final static String ACCOUNT_TYPE = "ACCOUNT_TYPE";
        public final static String AUTH_TYPE = "AUTH_TYPE";
        public final static String ACCOUNT_NAME = "ACCOUNT_NAME";
        public final static String IS_NEW_ACCOUNT = "IS_NEW_ACCOUNT";
        public final static String ACCOUNT_GROUP_ID = "ACCOUNT_GROUP_ID";
        public final static String ACCOUNT_PASSWORD = "ACCOUNT_PASSWORD";
        public final static String ACCOUNT_PASSWORD_SALT = "ACCOUNT_PASSWORD_SALT";
    }

    public static final class TOKEN_TYPE {
        public final static String PROVIDER = "provider";
        public final static String ADMIN = "admin";
    }


    public static Account getOauthAccountByType(String accountType) {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length > 0) {
            Account account = accounts[0];
            return account;
        }
        return null;
    }

    public static String getAccountManagerValue(String key, String accountType) {
        Account account = AccountHelper.getOauthAccountByType(accountType);
        if (account != null) {
            return accountManager.getUserData(account, key);
        }
        return null;
    }

    /**
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return access token
     */
    public static String getOAuthToken(String accountType, String authTokenType) {
        Account account = getOauthAccountByType(accountType);

        try {
            return accountManager.blockingGetAuthToken(account, authTokenType, true);
        } catch (Exception ex) {
            Timber.e(ex, "EXCEPTION: %s", ex.toString());
            return null;
        }
    }

    /** This method invalidates the auth token so that the Authenticator can fetch a new one from server
     * @param accountType unique name to identify our account type in the Account Manager
     * @param authToken   token to invalidate
     */
    public static void invalidateAuthToken(String accountType, String authToken) {
        accountManager.invalidateAuthToken(accountType, authToken);
    }


    /**
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return access token cached
     */
    public static String getCachedOAuthToken(String accountType, String authTokenType) {
        Account account = getOauthAccountByType(accountType);
        return accountManager.peekAuthToken(account, authTokenType);
    }

    /**
     * Prompt the user to re-authenticate
     *
     * @param accountName   name of account within account manage
     * @param accountType   unique name to identify our account type in the Account Manager
     * @param authTokenType type of token requested from server e.g. PROVIDER, ADMIN
     * @return access token
     */
    public static AccountManagerFuture<Bundle> reAuthenticateUserAfterSessionExpired(String accountName, String accountType, String authTokenType) {
        Account account = getOauthAccountByNameAndType(accountName, accountType);
        return accountManager.updateCredentials(account, authTokenType, null, null, null, null);


    }

}
