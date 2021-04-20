package org.smartregister.account;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 20-04-2021.
 */
public class AccountAuthenticatorTest extends BaseRobolectricUnitTest {

    private AccountAuthenticator accountAuthenticator;

    @Before
    public void setUp() throws Exception {
        accountAuthenticator = new AccountAuthenticator(RuntimeEnvironment.application);
    }

    @Test
    public void addAccountShouldReturnBundleWithCorrectDetails() throws NetworkErrorException {
        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String accountType = "org.smartregister.app";
        String authTokenType = "oauth2";
        Bundle bundle = accountAuthenticator.addAccount(accountAuthenticatorResponse, accountType, authTokenType, null, null);

        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        Assert.assertEquals(accountType, intent.getStringExtra(AccountHelper.INTENT_KEY.ACCOUNT_TYPE));
        Assert.assertEquals(authTokenType, intent.getStringExtra(AccountHelper.INTENT_KEY.AUTH_TYPE));
        Assert.assertTrue(intent.getBooleanExtra(AccountHelper.INTENT_KEY.IS_NEW_ACCOUNT, false));
        Assert.assertEquals(accountAuthenticatorResponse, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
    }

    @Test
    public void getAuthTokenLabelShouldReturnUpperCaseString() {
        Assert.assertEquals("MY-LABEL", accountAuthenticator.getAuthTokenLabel("my-label"));
    }

    @Test
    public void hasFeaturesShouldReturnBundleWithFalseBooleanResult() throws NetworkErrorException {
        Bundle result = accountAuthenticator.hasFeatures(null, null, null);

        Assert.assertFalse(result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
        Assert.assertEquals(1, result.keySet().size());
    }

    @Test
    public void editPropertiesShouldReturnNullAlways() {
        Assert.assertNull(accountAuthenticator.editProperties(null, "account-type-1"));
        Assert.assertNull(accountAuthenticator.editProperties(Mockito.mock(AccountAuthenticatorResponse.class), "account-type-1"));
        Assert.assertNull(accountAuthenticator.editProperties(Mockito.mock(AccountAuthenticatorResponse.class), null));
    }

    @Test
    public void confirmCredentialsShouldDoNothingAndReturnOptions() throws NetworkErrorException {
        Bundle bundle = Mockito.mock(Bundle.class);
        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        Assert.assertEquals(bundle, accountAuthenticator.confirmCredentials(accountAuthenticatorResponse, null, bundle));

        Mockito.verifyNoInteractions(bundle, accountAuthenticatorResponse);
    }

    @Test
    public void updateCredentialsShouldReturnBundleWithCorrectDetails() throws NetworkErrorException {
        AccountAuthenticatorResponse accountAuthenticatorResponse = Mockito.mock(AccountAuthenticatorResponse.class);
        String authTokenType = "oauth2";
        Bundle bundle = accountAuthenticator.updateCredentials(accountAuthenticatorResponse, null, authTokenType, null);

        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        Assert.assertEquals(authTokenType, intent.getStringExtra(AccountHelper.INTENT_KEY.AUTH_TYPE));
        Assert.assertFalse(intent.getBooleanExtra(AccountHelper.INTENT_KEY.IS_NEW_ACCOUNT, false));
        Assert.assertEquals(accountAuthenticatorResponse, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
    }
}