package org.smartregister.util;

import static org.mockito.ArgumentMatchers.eq;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.BuildConfig;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.security.SecurityHelper;
import org.smartregister.service.UserService;

import java.util.Arrays;

/**
 * Created by ndegwamartin on 22/07/2020.
 */
public class CredentialsHelperTest extends BaseUnitTest {

    private static final String TEST_USERNAME = "demo";
    private static final char[] TEST_DUMMY_PASSWORD = "test_password".toCharArray();
    private static final String TEST_ENCRYPTED_PWD = "4794#25%&%34&";
    private static final String TEST_LOCATION_ID = "3SF43-4AG3-3SUI44";
    private static final String TEST_TEAM_ID = "48SG2-23B4F2-F442-F3F44";
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private Context context;
    @Mock
    private UserService userService;
    @Mock
    private SyncConfiguration syncConfiguration;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private LoginResponseData userInfo;
    private CredentialsHelper credentialsHelper;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
        }
        Mockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(context);
        Mockito.when(context.applicationContext()).thenReturn(RuntimeEnvironment.application);

        Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn(userService).when(context).userService();

        Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();

        credentialsHelper = new CredentialsHelper(context);
        Assert.assertNotNull(credentialsHelper);

    }

    @Test
    public void testShouldMigrateReturnsTrueForDBEncryptionVersionZero() {
        boolean shouldMigrate = CredentialsHelper.shouldMigrate();
        Assert.assertTrue(shouldMigrate);
    }

    @Test
    public void testGetCredentialsInvokesGetDecryptedPassphraseValueWithCorrectValuesForDBAuth() {

        credentialsHelper.getCredentials(TEST_USERNAME, CredentialsHelper.CREDENTIALS_TYPE.DB_AUTH);
        ArgumentCaptor<String> usernameArgCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userService, Mockito.times(1)).getDecryptedPassphraseValue(usernameArgCaptor.capture());
        Assert.assertEquals(TEST_USERNAME, usernameArgCaptor.getValue());
    }

    @Test
    public void testGetCredentialsInvokesGetDecryptedPassphraseValueWithCorrectValuesForLocalAuth() {

        credentialsHelper.getCredentials(TEST_USERNAME, CredentialsHelper.CREDENTIALS_TYPE.LOCAL_AUTH);

        ArgumentCaptor<String> usernameArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyArgCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userService, Mockito.times(1)).getDecryptedAccountValue(usernameArgCaptor.capture(), keyArgCaptor.capture());

        Assert.assertEquals(TEST_USERNAME, usernameArgCaptor.getValue());
        Assert.assertEquals(AccountHelper.INTENT_KEY.ACCOUNT_LOCAL_PASSWORD, keyArgCaptor.getValue());
    }

    @Test
    public void testSaveCredentialsUpdatesSharedPreferencesWithEncryptedPassphrase() {

        Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
        Mockito.doReturn(SyncFilter.TEAM_ID).when(syncConfiguration).getEncryptionParam();

        credentialsHelper.saveCredentials(CredentialsHelper.CREDENTIALS_TYPE.DB_AUTH, TEST_ENCRYPTED_PWD, TEST_USERNAME);

        ArgumentCaptor<String> encryptionValueArgCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> encryptionParamArgCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(allSharedPreferences, Mockito.times(1)).savePassphrase(encryptionValueArgCaptor.capture(), encryptionParamArgCaptor.capture(), eq(TEST_USERNAME));

        Assert.assertEquals(TEST_ENCRYPTED_PWD, encryptionValueArgCaptor.getValue());
        Assert.assertEquals(SyncFilter.TEAM_ID.name(), encryptionParamArgCaptor.getValue());
    }

    @Test
    public void testSaveCredentialsUpdatesSharedPreferencesWithNewDBEncryptionVersion() {

        Mockito.doReturn(SyncFilter.LOCATION_ID).when(syncConfiguration).getEncryptionParam();

        credentialsHelper.saveCredentials(CredentialsHelper.CREDENTIALS_TYPE.DB_AUTH, TEST_ENCRYPTED_PWD, TEST_USERNAME);

        ArgumentCaptor<Integer> encryptionValueArgCaptor = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(allSharedPreferences, Mockito.times(1)).setDBEncryptionVersion(encryptionValueArgCaptor.capture());

        Assert.assertEquals((Integer) BuildConfig.DB_ENCRYPTION_VERSION, encryptionValueArgCaptor.getValue());
    }

    @Test
    public void generateLocalAuthCredentials() throws Exception {

        Assert.assertNotNull(credentialsHelper);

        try (MockedStatic<SecurityHelper> securityHelperMockedStatic = Mockito.mockStatic(SecurityHelper.class)) {
            securityHelperMockedStatic.when(() -> SecurityHelper.getPasswordHash(TEST_DUMMY_PASSWORD)).thenReturn(null);
        }

        credentialsHelper.generateLocalAuthCredentials(TEST_DUMMY_PASSWORD);

        Mockito.verify(SecurityHelper.class);
        SecurityHelper.getPasswordHash(TEST_DUMMY_PASSWORD);
    }

    @Test
    public void testGenerateDBCredentialsReturnsCorrectBytesForSyncByProvider() {

        Mockito.doReturn(SyncFilter.PROVIDER).when(syncConfiguration).getEncryptionParam();

        byte[] bytes = credentialsHelper.generateDBCredentials(TEST_DUMMY_PASSWORD, userInfo);
        Assert.assertTrue(Arrays.equals(SecurityHelper.toBytes(TEST_DUMMY_PASSWORD), bytes));
    }

    @Test
    public void testGenerateDBCredentialsReturnsCorrectBytesForSyncByTeam() {

        Mockito.doReturn(SyncFilter.TEAM_ID).when(syncConfiguration).getEncryptionParam();
        Mockito.doReturn(TEST_TEAM_ID).when(userService).getUserDefaultTeamId(userInfo);

        byte[] bytes = credentialsHelper.generateDBCredentials(TEST_DUMMY_PASSWORD, userInfo);
        Assert.assertTrue(Arrays.equals(SecurityHelper.toBytes(TEST_TEAM_ID.toCharArray()), bytes));
    }

    @Test
    public void testGenerateDBCredentialsReturnsCorrectBytesForSyncByLocation() {

        Mockito.doReturn(SyncFilter.LOCATION_ID).when(syncConfiguration).getEncryptionParam();
        Mockito.doReturn(TEST_LOCATION_ID).when(userService).getUserLocationId(userInfo);

        byte[] bytes = credentialsHelper.generateDBCredentials(TEST_DUMMY_PASSWORD, userInfo);
        Assert.assertTrue(Arrays.equals(SecurityHelper.toBytes(TEST_LOCATION_ID.toCharArray()), bytes));
    }
}