package org.smartregister.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;

import java.io.IOException;

/**
 * Created by ndegwamartin on 26/05/2020.
 */

public class AccountHelperTest extends BaseUnitTest {
    private static final String CORE_ACCOUNT_NAME = "demo";
    private static final String CORE_ACCOUNT_TYPE = "org.smartregister.core";
    private static final String TEST_KEY = "testKey";
    private static final String TEST_VALUE = "random-test-value";
    private static final String AUTH_TOKEN_TYPE = "My Admin Token Type";
    private static final String TEST_TOKEN_VALUE = "sample-token";

    @Mock
    private AccountManager accountManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Account[] accounts = {new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE)};
        Mockito.doReturn(accounts).when(accountManager).getAccountsByType(CORE_ACCOUNT_TYPE);

        Whitebox.setInternalState(AccountHelper.class, "accountManager", accountManager);
    }

    @Test
    public void testGetOauthAccountByType() {

        Account account = AccountHelper.getOauthAccountByType(CORE_ACCOUNT_TYPE);
        Assert.assertNotNull(account);
        Assert.assertEquals(CORE_ACCOUNT_NAME, account.name);
    }

    @Test
    public void testGetAccountManagerValue() {

        Whitebox.setInternalState(AccountHelper.class, "accountManager", accountManager);

        Mockito.doReturn(TEST_VALUE).when(accountManager).getUserData(ArgumentMatchers.any(Account.class), ArgumentMatchers.eq(TEST_KEY));

        String value = AccountHelper.getAccountManagerValue(TEST_KEY, CORE_ACCOUNT_TYPE);
        Assert.assertNotNull(value);
        Assert.assertEquals(TEST_VALUE, value);
    }

    @Test
    public void testGetOAuthToken() throws AuthenticatorException, OperationCanceledException, IOException {

        Mockito.doReturn(TEST_TOKEN_VALUE).when(accountManager).blockingGetAuthToken(new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE), AUTH_TOKEN_TYPE, true);
        String myToken = AccountHelper.getOAuthToken(CORE_ACCOUNT_TYPE, AUTH_TOKEN_TYPE);
        Assert.assertNotNull(myToken);
        Assert.assertEquals(TEST_TOKEN_VALUE, myToken);
    }

    @Test
    public void testInvalidateAuthToken() {

        AccountHelper.invalidateAuthToken(CORE_ACCOUNT_TYPE, TEST_TOKEN_VALUE);
        Mockito.verify(accountManager).invalidateAuthToken(CORE_ACCOUNT_TYPE, TEST_TOKEN_VALUE);
    }

}