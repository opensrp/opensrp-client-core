package org.smartregister.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.Response;
import org.smartregister.domain.TimeStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.Time;
import org.smartregister.domain.jsonmapping.User;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TeamLocation;
import org.smartregister.domain.jsonmapping.util.TeamMember;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.security.SecurityHelper;
import org.smartregister.sync.SaveANMLocationTask;
import org.smartregister.sync.SaveANMTeamTask;
import org.smartregister.sync.SaveUserInfoTask;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Session;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import timber.log.Timber;

import static org.smartregister.AllConstants.ENGLISH_LANGUAGE;
import static org.smartregister.AllConstants.ENGLISH_LOCALE;
import static org.smartregister.AllConstants.KANNADA_LANGUAGE;
import static org.smartregister.AllConstants.KANNADA_LOCALE;
import static org.smartregister.AllConstants.OPENSRP_AUTH_USER_URL_PATH;
import static org.smartregister.AllConstants.OPENSRP_LOCATION_URL_PATH;
import static org.smartregister.AllConstants.OPERATIONAL_AREAS;
import static org.smartregister.AllConstants.ORGANIZATION_IDS;
import static org.smartregister.event.Event.ON_LOGOUT;

public class UserService {
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String CIPHER = "RSA/ECB/PKCS1Padding";
    private static final String CIPHER_PROVIDER = "AndroidOpenSSL";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private final AllSettings allSettings;
    private final AllSharedPreferences allSharedPreferences;
    private HTTPAgent httpAgent;
    private Session session;
    private DristhiConfiguration configuration;
    private SaveANMLocationTask saveANMLocationTask;
    private SaveANMTeamTask saveANMTeamTask;
    private SaveUserInfoTask saveUserInfoTask;
    private KeyStore keyStore;

    public UserService(AllSettings allSettingsArg, AllSharedPreferences
            allSharedPreferencesArg, HTTPAgent httpAgentArg, Session sessionArg,
                       DristhiConfiguration configurationArg, SaveANMLocationTask
                               saveANMLocationTaskArg, SaveUserInfoTask saveUserInfoTaskArg, SaveANMTeamTask saveANMTeamTaskArg) {
        allSettings = allSettingsArg;
        allSharedPreferences = allSharedPreferencesArg;
        httpAgent = httpAgentArg;
        session = sessionArg;
        configuration = configurationArg;
        saveANMLocationTask = saveANMLocationTaskArg;
        saveUserInfoTask = saveUserInfoTaskArg;
        saveANMTeamTask = saveANMTeamTaskArg;
        initKeyStore();
    }

    private static TimeZone getDeviceTimeZone() {
        return TimeZone.getDefault();
    }

    private static Date getDeviceTime() {
        return Calendar.getInstance().getTime();
    }

    public static TimeZone getServerTimeZone(LoginResponseData userInfo) {
        if (userInfo != null) {
            try {
                Time time = userInfo.time;
                if (time != null) {
                    TimeZone timeZone = TimeZone.getTimeZone(time.getTimeZone());
                    return timeZone;
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        return null;
    }

    private static Date getServerTime(LoginResponseData userInfo) {
        if (userInfo != null) {
            try {
                Time time = userInfo.time;
                if (time != null) {
                    return DATE_FORMAT.parse(time.getTime());
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        return null;
    }

    public void initKeyStore() {
        try {
            this.keyStore = KeyStore.getInstance(KEYSTORE);
            this.keyStore.load(null);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException |
                CertificateException e) {
            Timber.e(e);
        }
    }

    public TimeStatus validateStoredServerTimeZone() {
        TimeStatus result = TimeStatus.ERROR;
        try {
            String serverTimeZoneId = allSharedPreferences.fetchServerTimeZone();
            if (serverTimeZoneId != null) {
                TimeZone serverTimeZone = TimeZone.getTimeZone(serverTimeZoneId);
                TimeZone deviceTimeZone = TimeZone.getDefault();
                if (serverTimeZone != null && deviceTimeZone != null
                        && serverTimeZone.getRawOffset() == deviceTimeZone.getRawOffset()) {
                    result = TimeStatus.OK;
                } else {
                    result = TimeStatus.TIMEZONE_MISMATCH;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        if (!result.equals(TimeStatus.OK)) {
            forceRemoteLogin();
        }

        return result;
    }

    private void saveServerTimeZone(LoginResponseData userInfo) {
        TimeZone serverTimeZone = getServerTimeZone(userInfo);
        String timeZoneId = null;
        if (serverTimeZone != null) {
            timeZoneId = serverTimeZone.getID();
        }

        allSharedPreferences.saveServerTimeZone(timeZoneId);
    }

    public TimeStatus validateDeviceTime(LoginResponseData userInfo, long serverTimeThreshold) {
        TimeZone serverTimeZone = getServerTimeZone(userInfo);
        TimeZone deviceTimeZone = getDeviceTimeZone();
        Date serverTime = getServerTime(userInfo);
        Date deviceTime = getDeviceTime();

        if (serverTimeZone != null && deviceTimeZone != null && serverTime != null
                && deviceTime != null) {
            if (serverTimeZone.getRawOffset() == deviceTimeZone.getRawOffset()) {
                long timeDiff = Math.abs(serverTime.getTime() - deviceTime.getTime());
                if (timeDiff <= serverTimeThreshold) {
                    return TimeStatus.OK;
                } else {
                    return TimeStatus.TIME_MISMATCH;
                }
            } else {
                return TimeStatus.TIMEZONE_MISMATCH;
            }
        }

        return TimeStatus.ERROR;
    }

    public boolean isValidLocalLogin(String userName, char[] password) {
        return allSharedPreferences.fetchRegisteredANM().equals(userName) && DrishtiApplication.getInstance().getRepository()
                .canUseThisPassword(password) && !allSharedPreferences.fetchForceRemoteLogin();
    }

    public boolean isUserInValidGroup(final String userName, final char[] password) {
        // Check if everything OK for local login
        if (keyStore != null && userName != null && password != null && !allSharedPreferences.fetchForceRemoteLogin()) {
            String username = userName.equalsIgnoreCase(allSharedPreferences.fetchRegisteredANM()) ? allSharedPreferences.fetchRegisteredANM() : userName;
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = getUserKeyPair(username);
                if (privateKeyEntry != null) {
                    char[] groupId = getGroupId(username, privateKeyEntry);
                    if (groupId != null) {
                        return isValidGroupId(groupId);
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        return false;
    }

    private boolean isValidGroupId(char[] groupId) {
        return DrishtiApplication.getInstance().getRepository().canUseThisPassword(groupId);
    }

    public char[] getGroupId(String userName) {
        if (keyStore != null && userName != null) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = getUserKeyPair(userName);
                return getGroupId(userName, privateKeyEntry);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return null;
    }

    public char[] getGroupId(String userName, KeyStore.PrivateKeyEntry privateKeyEntry) {
        if (privateKeyEntry != null) {

            String encryptedGroupId = AccountHelper.getAccountManagerValue(AccountHelper.INTENT_KEY.ACCOUNT_GROUP_ID, CoreLibrary.getInstance().getAccountAuthenticatorXml().getAccountType());

            if (encryptedGroupId != null) {
                try {
                    return SecurityHelper.toChars(decryptString(privateKeyEntry, encryptedGroupId));
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the groupId for the provided user is the same as the first person to
     * successfully login
     *
     * @param userName The user to check
     * @return TRUE if the groupIds match
     */
    public boolean isUserInPioneerGroup(String userName) {
        String pioneerUser = allSharedPreferences.fetchPioneerUser();
        if (userName.equals(pioneerUser)) {
            return true;
        } else {
            char[] userGroupId = getGroupId(userName);
            char[] pioneerGroupId = getGroupId(pioneerUser);

            if (userGroupId != null && userGroupId.equals(pioneerGroupId)) {
                return isValidGroupId(userGroupId);
            }
        }

        return false;
    }

    public LoginResponse fetchUserDetails(String accessToken) {
        String requestURL;

        requestURL = configuration.dristhiBaseURL() + OPENSRP_AUTH_USER_URL_PATH;

        LoginResponse loginResponse = httpAgent.fetchUserDetails(requestURL, accessToken);

        return loginResponse;
    }

    public LoginResponse isValidRemoteLogin(String userName, char[] password) {
        String requestURL;

        requestURL = configuration.dristhiBaseURL() + OPENSRP_AUTH_USER_URL_PATH;

        LoginResponse loginResponse = httpAgent.urlCanBeAccessWithGivenCredentials(requestURL, userName, password);

        if (loginResponse != null && loginResponse.equals(LoginResponse.SUCCESS)) {
            saveUserGroup(userName, password, loginResponse.payload());
        }

        return loginResponse;
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return allSharedPreferences;
    }

    public Response<String> getLocationInformation() {
        String requestURL = configuration.dristhiBaseURL() + OPENSRP_LOCATION_URL_PATH;
        return httpAgent.fetch(requestURL);
    }

    private boolean loginWith(String userName, char[] password) {
        boolean loginSuccessful = true;

        if (usesGroupIdAsDBPassword()) {

            String encryptedGroupId = AccountHelper.getAccountManagerValue(AccountHelper.INTENT_KEY.ACCOUNT_GROUP_ID, CoreLibrary.getInstance().getAccountAuthenticatorXml().getAccountType());

            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = getUserKeyPair(userName);
                if (privateKeyEntry != null) {
                    byte[] groupId = Base64.decode(encryptedGroupId, Base64.DEFAULT);
                    setupContextForLogin(SecurityHelper.toChars(decryptString(privateKeyEntry, encryptedGroupId)));
                    SecurityHelper.clearArray(groupId);
                }
            } catch (Exception e) {
                Timber.e(e);
                loginSuccessful = false;
            }
        } else {
            setupContextForLogin(password);
        }
        String username = userName;
        if (allSharedPreferences.fetchRegisteredANM().equalsIgnoreCase(userName))
            username = allSharedPreferences.fetchRegisteredANM();
        allSharedPreferences.updateANMUserName(username);
        DrishtiApplication.getInstance().getRepository().getReadableDatabase();
        allSettings.registerANM(username);
        return loginSuccessful;
    }

    /**
     * Checks whether to use the groupId for the current user to decrypt the database
     *
     * @return TRUE if the user decrypts the database using the groupId
     */
    private boolean usesGroupIdAsDBPassword() {
        return AccountHelper.getAccountManagerValue(AccountHelper.INTENT_KEY.ACCOUNT_GROUP_ID, CoreLibrary.getInstance().getAccountAuthenticatorXml().getAccountType()) != null;
    }

    public void localLogin(String userName, char[] password) {
        loginWith(userName, password);
    }

    public void processLoginResponseDataForUser(String userName, char[] password, LoginResponseData userInfo) {
        String username = userInfo.user != null && StringUtils.isNotBlank(userInfo.user.getUsername())
                ? userInfo.user.getUsername() : userName;
        boolean loginSuccessful = loginWith(username, password);
        saveAnmLocation(getUserLocation(userInfo));
        saveAnmTeam(getUserTeam(userInfo));
        saveUserInfo(getUserData(userInfo));
        saveDefaultLocationId(username, getUserDefaultLocationId(userInfo));
        saveUserLocationId(username, getUserLocationId(userInfo));
        saveDefaultTeam(username, getUserDefaultTeam(userInfo));
        saveDefaultTeamId(username, getUserDefaultTeamId(userInfo));
        saveServerTimeZone(userInfo);
        saveJurisdictions(userInfo.jurisdictions);
        saveOrganizations(getUserTeam(userInfo));
        if (loginSuccessful &&
                (StringUtils.isBlank(getUserDefaultLocationId(userInfo)) ||
                        StringUtils.isNotBlank(allSharedPreferences.fetchDefaultLocalityId(username))) &&
                (StringUtils.isBlank(getUserDefaultTeamId(userInfo)) ||
                        StringUtils.isNotBlank(allSharedPreferences.fetchDefaultTeamId(username))) &&
                (getUserLocation(userInfo) != null ||
                        StringUtils.isNotBlank(allSettings.fetchANMLocation())))
            allSharedPreferences.saveForceRemoteLogin(false);
    }

    public void forceRemoteLogin() {
        allSharedPreferences.saveForceRemoteLogin(true);
    }

    public User getUserData(LoginResponseData userInfo) {
        try {
            if (userInfo != null) {
                return userInfo.user;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public LocationTree getUserLocation(LoginResponseData userInfo) {
        try {
            if (userInfo != null) {
                return userInfo.locations;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public TeamMember getUserTeam(LoginResponseData userInfo) {
        try {
            if (userInfo != null) {
                return userInfo.team;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public void saveDefaultLocationId(String userName, String locationId) {
        if (userName != null) {
            allSharedPreferences.saveDefaultLocalityId(userName, locationId);
        }
    }

    public String getUserDefaultTeam(LoginResponseData userInfo) {
        try {
            if (userInfo != null && userInfo.team != null && userInfo.team.team != null) {
                return userInfo.team.team.teamName;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public void saveDefaultTeam(String userName, String team) {
        if (userName != null) {
            allSharedPreferences.saveDefaultTeam(userName, team);
        }
    }

    public String getUserDefaultTeamId(LoginResponseData userInfo) {
        try {
            if (userInfo != null && userInfo.team != null && userInfo.team.team != null) {
                return userInfo.team.team.uuid;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public void saveDefaultTeamId(String userName, String teamId) {
        if (userName != null) {
            allSharedPreferences.saveDefaultTeamId(userName, teamId);
        }
    }

    public String getUserDefaultLocationId(LoginResponseData userInfo) {
        try {
            if (userInfo != null && userInfo.team != null && userInfo.team.team != null && userInfo.team.team.location != null) {
                return userInfo.team.team.location.uuid;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public String getUserLocationId(LoginResponseData userInfo) {
        try {
            if (userInfo != null && userInfo.team != null && userInfo.team.locations != null && !userInfo.team.locations.isEmpty()) {
                for (TeamLocation teamLocation : userInfo.team.locations) {
                    if (teamLocation != null) {
                        return teamLocation.uuid;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public void saveUserLocationId(String userName, String locationId) {
        if (userName != null) {
            allSharedPreferences.saveUserLocalityId(userName, locationId);
        }
    }

    public void saveAnmLocation(LocationTree anmLocation) {
        String amnLocationString = AssetHandler.javaToJsonString(anmLocation);
        saveANMLocationTask.save(amnLocationString);
    }

    public void saveAnmTeam(TeamMember anmTeam) {
        String anmTeamString = AssetHandler.javaToJsonString(anmTeam);
        saveANMTeamTask.save(anmTeamString);
    }

    public void saveJurisdictions(List<String> jurisdictions) {
        if (jurisdictions != null && !jurisdictions.isEmpty())
            allSharedPreferences.savePreference(OPERATIONAL_AREAS, android.text.TextUtils.join(",", jurisdictions));
    }

    public void saveOrganizations(TeamMember teamMember) {
        if (teamMember != null && teamMember.team != null) {
            List<Long> organizations = teamMember.team.organizationIds;
            if (organizations != null && !organizations.isEmpty())
                allSharedPreferences.savePreference(ORGANIZATION_IDS, android.text.TextUtils.join(",", organizations));
        }
    }

    public void saveUserInfo(User user) {
        try {
            if (user != null && user.getPreferredName() != null) {
                String preferredName = user.getPreferredName();
                String userName = user.getUsername();
                allSharedPreferences.updateANMPreferredName(userName, preferredName);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        String userInfoString = AssetHandler.javaToJsonString(user);
        saveUserInfoTask.save(userInfoString);
    }

    /**
     * Saves the a user's groupId and password in . GroupId and password
     * are not saved if groupId could not be found in userInfo
     *
     * @param userName The username you want to save the password and groupId
     * @param password The user's password
     * @param userInfo The user's info from the
     *                 endpoint (should contain the {team}.{team}.{uuid} key)
     */
    public Bundle saveUserGroup(String userName, char[] password, LoginResponseData userInfo) {
        Bundle bundle = new Bundle();

        String username = userInfo.user != null && StringUtils.isNotBlank(userInfo.user.getUsername()) ? userInfo.user.getUsername() : userName;
        bundle.putString(AccountHelper.INTENT_KEY.ACCOUNT_NAME, username);


        if (keyStore != null && username != null) {

            byte[] groupId = null;

            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = createUserKeyPair(username);

                if (password == null) {
                    return null;
                }

                SyncConfiguration syncConfiguration = CoreLibrary.getInstance().getSyncConfiguration();
                if (syncConfiguration.getEncryptionParam() != null) {
                    SyncFilter syncFilter = syncConfiguration.getEncryptionParam();
                    if (SyncFilter.TEAM.equals(syncFilter) || SyncFilter.TEAM_ID.equals(syncFilter)) {
                        groupId = SecurityHelper.toBytes(getUserDefaultTeamId(userInfo).toCharArray());
                    } else if (SyncFilter.LOCATION.equals(syncFilter) || SyncFilter.LOCATION_ID.equals(syncFilter)) {
                        groupId = SecurityHelper.toBytes(getUserLocationId(userInfo).toCharArray());
                    } else if (SyncFilter.PROVIDER.equals(syncFilter)) {
                        groupId = SecurityHelper.toBytes(new StringBuffer(username).append('-').append(password));
                    }
                }

                if (groupId == null || groupId.length < 1) {
                    return null;
                }

                if (privateKeyEntry != null) {

                    // Then save the encrypted group
                    String encryptedGroupId = encryptString(privateKeyEntry, groupId);
                    bundle.putString(AccountHelper.INTENT_KEY.ACCOUNT_GROUP_ID, encryptedGroupId);

                    // Finally, save the pioneer user
                    if (allSharedPreferences.fetchPioneerUser() == null) {
                        allSharedPreferences.savePioneerUser(username);
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            } finally {

                SecurityHelper.clearArray(password);
                SecurityHelper.clearArray(groupId);
            }
        }

        return bundle;
    }

    public boolean hasARegisteredUser() {
        return !allSharedPreferences.fetchRegisteredANM().equals("");
    }

    public void logout() {
        logoutSession();
        allSettings.registerANM("");
        allSettings.savePreviousFetchIndex("0");
        DrishtiApplication.getInstance().getRepository().deleteRepository();
    }

    public void logoutSession() {
        session().expire();
        ON_LOGOUT.notifyListeners(true);
    }

    public boolean hasSessionExpired() {
        return session().hasExpired();
    }

    protected void setupContextForLogin(char[] password) {
        session().start(session().lengthInMilliseconds());
        configuration.getDrishtiApplication().setPassword(password);
        session().setPassword(SecurityHelper.toBytes(password));
    }

    protected Session session() {
        return session;
    }

    public String switchLanguagePreference() {
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        if (ENGLISH_LOCALE.equals(preferredLocale)) {
            allSharedPreferences.saveLanguagePreference(KANNADA_LOCALE);
            return KANNADA_LANGUAGE;
        } else {
            allSharedPreferences.saveLanguagePreference(ENGLISH_LOCALE);
            return ENGLISH_LANGUAGE;
        }
    }

    private KeyStore.PrivateKeyEntry getUserKeyPair(final String username) throws Exception {
        if (keyStore.containsAlias(username)) {
            return (KeyStore.PrivateKeyEntry) keyStore.getEntry(username, null);
        }

        return null;
    }

    /**
     * Creates a keypair for the provided username
     *
     * @param username The username to create the keypair for
     * @return {@link java.security.KeyStore.PrivateKeyEntry} corresponding to the user or NULL if
     * a problem occurred
     * @throws Exception
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private KeyStore.PrivateKeyEntry createUserKeyPair(final String username) throws Exception {
        if (!keyStore.containsAlias(username)) {
            if (!keyStore.containsAlias(username)) {
                // Create the alias for the user
                Calendar now = Calendar.getInstance();
                Calendar expiry = Calendar.getInstance();
                expiry.add(Calendar.YEAR, 1);

                int serialNumber = Math.abs(0 + (int) (Math.random() * (Integer.MAX_VALUE + 1)));

                KeyPairGeneratorSpec generatorSpec = new KeyPairGeneratorSpec.Builder(
                        DrishtiApplication.getInstance()).setAlias(username)
                        .setSubject(new X500Principal("CN=" + username + ", O=OpenSRP"))
                        .setStartDate(now.getTime()).setEndDate(expiry.getTime())
                        .setSerialNumber(BigInteger.valueOf((long) serialNumber)).build();

                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", KEYSTORE);
                generator.initialize(generatorSpec);
                generator.generateKeyPair();
            }
        }

        return getUserKeyPair(username);
    }

    /**
     * Decrypts a string using the provided keypair
     *
     * @param privateKeyEntry Keypair to use to decrypt the string
     * @param cipherText      String to be decrypted
     * @return char array of text derived from the cipher text
     * @throws Exception
     */
    private byte[] decryptString(KeyStore.PrivateKeyEntry privateKeyEntry, String cipherText)
            throws Exception {

        Cipher output;
        if (Build.VERSION.SDK_INT >= 23) {
            output = Cipher.getInstance(CIPHER);
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        } else {
            output = Cipher.getInstance(CIPHER, CIPHER_PROVIDER);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
            output.init(Cipher.DECRYPT_MODE, privateKey);
        }

        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);

        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }

        return bytes;
    }

    /**
     * Encrypts a string using the provided keypair
     *
     * @param privateKeyEntry The keypair to use to encrypt the text
     * @param plainTextBytes  The plain text to encrypt (should be at most 256bytes)
     * @return Cipher text corresponding to the plain text
     * @throws Exception
     */
    private String encryptString(KeyStore.PrivateKeyEntry privateKeyEntry, byte[] plainTextBytes)
            throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

        Cipher input;
        if (Build.VERSION.SDK_INT >= 23) {
            input = Cipher.getInstance(CIPHER);
        } else {
            input = Cipher.getInstance(CIPHER, CIPHER_PROVIDER);
        }
        input.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
        cipherOutputStream.write(plainTextBytes);
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();

        return Base64.encodeToString(vals, Base64.DEFAULT);
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }
}
