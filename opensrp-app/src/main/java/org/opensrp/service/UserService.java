package org.opensrp.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import org.opensrp.AllConstants;
import org.opensrp.DristhiConfiguration;
import org.opensrp.domain.LoginResponse;
import org.opensrp.domain.Response;
import org.opensrp.domain.TimeStatus;
import org.opensrp.repository.AllSettings;
import org.opensrp.repository.AllSharedPreferences;
import org.opensrp.repository.Repository;
import org.opensrp.sync.SaveANMLocationTask;
import org.opensrp.sync.SaveUserInfoTask;
import org.opensrp.util.Session;
import org.opensrp.view.activity.DrishtiApplication;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import static org.opensrp.AllConstants.*;
import static org.opensrp.event.Event.ON_LOGOUT;

public class UserService {
    private static final String TAG = UserService.class.getCanonicalName();
    private static final String KEYSTORE = "AndroidKeyStore";
    private static final String CIPHER = "RSA/ECB/PKCS1Padding";
    private static final String CIPHER_PROVIDER = "AndroidOpenSSL";
    private static final String CIPHER_TEXT_CHARACTER_CODE = "UTF-8";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Repository repository;
    private final AllSettings allSettings;
    private final AllSharedPreferences allSharedPreferences;
    private HTTPAgent httpAgent;
    private Session session;
    private DristhiConfiguration configuration;
    private SaveANMLocationTask saveANMLocationTask;
    private SaveUserInfoTask saveUserInfoTask;
    private KeyStore keyStore;

    public UserService(Repository repository, AllSettings allSettings, AllSharedPreferences allSharedPreferences, HTTPAgent httpAgent, Session session,
                       DristhiConfiguration configuration, SaveANMLocationTask saveANMLocationTask,
                       SaveUserInfoTask saveUserInfoTask) {
        this.repository = repository;
        this.allSettings = allSettings;
        this.allSharedPreferences = allSharedPreferences;
        this.httpAgent = httpAgent;
        this.session = session;
        this.configuration = configuration;
        this.saveANMLocationTask = saveANMLocationTask;
        this.saveUserInfoTask = saveUserInfoTask;
        initKeyStore();
    }

    public void initKeyStore() {
        try {
            this.keyStore = KeyStore.getInstance(KEYSTORE);
            this.keyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            Log.e(TAG, Log.getStackTraceString(e));
        }

        if (!result.equals(TimeStatus.OK)) {
            forceRemoteLogin();
        }

        return result;
    }

    private void saveServerTimeZone(String userInfo) {
        TimeZone serverTimeZone = getServerTimeZone(userInfo);
        String timeZoneId = null;
        if (serverTimeZone != null) {
            timeZoneId = serverTimeZone.getID();
        }

        allSharedPreferences.saveServerTimeZone(timeZoneId);
    }

    public TimeStatus validateDeviceTime(String userInfo, long serverTimeThreshold) {
        TimeZone serverTimeZone = getServerTimeZone(userInfo);
        TimeZone deviceTimeZone = getDeviceTimeZone();
        Date serverTime = getServerTime(userInfo);
        Date deviceTime = getDeviceTime();

        if (serverTimeZone != null && deviceTimeZone != null
                && serverTime != null && deviceTime != null) {
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

    private static TimeZone getDeviceTimeZone() {
        return TimeZone.getDefault();
    }

    private static Date getDeviceTime() {
        Calendar.getInstance().getTime();
        return Calendar.getInstance().getTime();
    }

    public static TimeZone getServerTimeZone(String userInfo) {
        if (userInfo != null) {
            try {
                JSONObject userInfoData = new JSONObject(userInfo);
                TimeZone timeZone = TimeZone.getTimeZone(userInfoData.getJSONObject("time").getString("timeZone"));
                return timeZone;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        return null;
    }

    private static Date getServerTime(String userInfo) {
        if (userInfo != null) {
            try {
                JSONObject userInfoData = new JSONObject(userInfo);
                return DATE_FORMAT.parse(userInfoData.getJSONObject("time").getString("time"));
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

        return null;
    }

    public boolean isValidLocalLogin(String userName, String password) {
        return allSharedPreferences.fetchRegisteredANM().equals(userName) && repository.canUseThisPassword(password) && !allSharedPreferences.fetchForceRemoteLogin();
    }

    public boolean isUserInValidGroup(final String userName, final String password) {
        // Check if everything OK for local login
        if (keyStore != null && userName != null && password != null
                && !allSharedPreferences.fetchForceRemoteLogin()) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = getUserKeyPair(userName);
                if (privateKeyEntry != null) {
                    // Compare stored encrypted password with provided password
                    String encryptedPassword = allSharedPreferences.fetchEncryptedPassword(userName);
                    String decryptedPassword = decryptString(privateKeyEntry, encryptedPassword);

                    if(password.equals(decryptedPassword)) {
                        String groupId = getGroupId(userName, privateKeyEntry);
                        if(groupId != null) {
                            return isValidGroupId(groupId);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean isValidGroupId(String groupId) {
        return repository.canUseThisPassword(groupId);
    }

    public String getGroupId(String userName) {
        if (keyStore != null && userName != null) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = getUserKeyPair(userName);
                return getGroupId(userName, privateKeyEntry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getGroupId(String userName, KeyStore.PrivateKeyEntry privateKeyEntry) {
        if (privateKeyEntry != null) {
            String encryptedGroupId = allSharedPreferences.fetchEncryptedGroupId(userName);
            if (encryptedGroupId != null) {
                try {
                    return decryptString(privateKeyEntry, encryptedGroupId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the groupId for the provided user is the same as the first person to
     * successfully login
     *
     * @param userName  The user to check
     * @return  TRUE if the groupIds match
     */
    public boolean isUserInPioneerGroup(String userName) {
        String pioneerUser = allSharedPreferences.fetchPioneerUser();
        if (userName.equals(pioneerUser)) {
            return true;
        } else {
            String userGroupId = getGroupId(userName);
            String pioneerGroupId = getGroupId(pioneerUser);

            if (userGroupId != null && userGroupId.equals(pioneerGroupId)) {
                return isValidGroupId(userGroupId);
            }
        }

        return false;
    }

    public LoginResponse isValidRemoteLogin(String userName, String password) {
        String requestURL;

        requestURL = configuration.dristhiBaseURL() + OPENSRP_AUTH_USER_URL_PATH;

        LoginResponse loginResponse = httpAgent.urlCanBeAccessWithGivenCredentials(requestURL, userName, password);
        saveUserGroup(userName, password, loginResponse.payload());

        return loginResponse;
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return allSharedPreferences;
    }

    public Response<String> getLocationInformation() {
        String requestURL = configuration.dristhiBaseURL() + OPENSRP_LOCATION_URL_PATH;
        return httpAgent.fetch(requestURL);
    }

    private void loginWith(String userName, String password) {
        if (usesGroupIdAsDBPassword(userName)) {
            String encryptedGroupId = allSharedPreferences.fetchEncryptedGroupId(userName);
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = getUserKeyPair(userName);
                if (privateKeyEntry != null) {
                    String groupId = decryptString(privateKeyEntry, encryptedGroupId);
                    setupContextForLogin(userName, groupId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setupContextForLogin(userName, password);
        }
        allSettings.registerANM(userName, password);
    }

    /**
     * Checks whether to use the groupId for the current user to decrypt the database
     *
     * @param userName  The user to check
     *
     * @return  TRUE if the user decrypts the database using the groupId
     */
    private boolean usesGroupIdAsDBPassword(String userName) {
        try {
            if (keyStore != null && keyStore.containsAlias(userName)) {
                if (allSharedPreferences.fetchEncryptedGroupId(userName) != null) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void localLogin(String userName, String password) {
        loginWith(userName, password);
    }

    public void remoteLogin(String userName, String password, String userInfo) {
        allSharedPreferences.saveForceRemoteLogin(false);
        loginWith(userName, password);
        saveAnmLocation(getUserLocation(userInfo));
        saveUserInfo(getUserData(userInfo));
        saveDefaultLocationId(userName, getUserDefaultLocationId(userInfo));
        saveServerTimeZone(userInfo);
    }

    public void forceRemoteLogin() {
        allSharedPreferences.saveForceRemoteLogin(true);
    }

    public String getUserData(String userInfo) {
        try {
            JSONObject userInfoJson = new JSONObject(userInfo);
            return userInfoJson.getString("user");
        } catch (JSONException e) {
            Log.v("Error : ", e.getMessage());
            return null;
        }
    }

    public String getUserLocation(String userInfo) {
        try {
            JSONObject userLocationJSON = new JSONObject(userInfo);
            return userLocationJSON.getString("locations");
        } catch (JSONException e) {
            Log.v("Error : ", e.getMessage());
            return null;
        }
    }

    public void saveDefaultLocationId(String userName, String locationId) {
        if (userName != null) {
            allSharedPreferences.saveDefaultLocalityId(userName, locationId);
        }
    }

    public String getUserDefaultLocationId(String userInfo) {
        try {
            JSONObject userLocationJSON = new JSONObject(userInfo);
            return userLocationJSON.getJSONObject("team").getJSONObject("team")
                    .getJSONObject("location").getString("uuid");
        } catch (JSONException e) {
            Log.v("Error : ", e.getMessage());
        }

        return null;
    }

    public void saveAnmLocation(String anmLocation) {
        saveANMLocationTask.save(anmLocation);
    }

    public void saveUserInfo(String userInfo) {
       try{
           JSONObject userInfoObject = new JSONObject(userInfo);
           if (userInfoObject.has("preferredName")) {
               String preferredName=userInfoObject.getString("preferredName");
               String userName=userInfoObject.getString("username");
               allSharedPreferences.updateANMPreferredName(userName,preferredName);
           }
       }catch(Exception e){
        Log.e(TAG,e.getMessage());
       }

        saveUserInfoTask.save(userInfo);
    }

    /**
     * Saves the a user's groupId and password in . GroupId and password
     * are not saved if groupId could not be found in userInfo
     *
     * @param userName  The username you want to save the password and groupId
     * @param password  The user's password
     * @param userInfo  The user's info from the
     *                  endpoint (should contain the {team}.{team}.{uuid} key)
     */
    public void saveUserGroup(String userName, String password, String userInfo) {
        if (keyStore != null && userName != null) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = createUserKeyPair(userName);
                if (privateKeyEntry != null) {
                    JSONObject userInfoObject = new JSONObject(userInfo);
                    if (userInfoObject.has("team")
                            && userInfoObject.getJSONObject("team").has("team")
                            && userInfoObject.getJSONObject("team").getJSONObject("team").has("uuid")) {
                        // First save the encrypted password
                        String encryptedPassword = encryptString(privateKeyEntry, password);
                        allSharedPreferences.saveEncryptedPassword(userName, encryptedPassword);

                        // Then save the encrypted group
                        String groupId = userInfoObject.getJSONObject("team")
                                .getJSONObject("team").getString("uuid");
                        String encryptedGroupId = encryptString(privateKeyEntry, groupId);
                        allSharedPreferences.saveEncryptedGroupId(userName, encryptedGroupId);

                        // Finally, save the pioneer user
                        if(allSharedPreferences.fetchPioneerUser() == null) {
                            allSharedPreferences.savePioneerUser(userName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasARegisteredUser() {
        return !allSharedPreferences.fetchRegisteredANM().equals("");
    }

    public void logout() {
        logoutSession();
        allSettings.registerANM("", "");
        allSettings.savePreviousFetchIndex("0");
        repository.deleteRepository();
    }

    public void logoutSession() {
        session().expire();
        ON_LOGOUT.notifyListeners(true);
    }

    public boolean hasSessionExpired() {
        return session().hasExpired();
    }

    protected void setupContextForLogin(String userName, String password) {
        session().start(session().lengthInMilliseconds());
        DrishtiApplication.getInstance().setPassword(password);
        session().setPassword(password);
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
     * @param username  The username to create the keypair for
     * @return  {@link java.security.KeyStore.PrivateKeyEntry} corresponding to the user or NULL if
     *          a problem occurred
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

                int serialNumber = Math.abs(0 + (int)(Math.random() * (Integer.MAX_VALUE + 1)));

                KeyPairGeneratorSpec generatorSpec = new KeyPairGeneratorSpec.Builder(DrishtiApplication.getInstance())
                        .setAlias(username)
                        .setSubject(new X500Principal("CN=" + username + ", O=OpenSRP"))
                        .setStartDate(now.getTime())
                        .setEndDate(expiry.getTime())
                        .setSerialNumber(BigInteger.valueOf((long)serialNumber))
                        .build();

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
     * @param privateKeyEntry   Keypair to use to decrypt the string
     * @param cipherText        String to be decrypted
     * @return  Plain text derived from the cipher text
     * @throws Exception
     */
    private String decryptString(KeyStore.PrivateKeyEntry privateKeyEntry, String cipherText)
            throws Exception {

        Cipher output;
        if(Build.VERSION.SDK_INT >= 23) {
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
            bytes[i] = values.get(i).byteValue();
        }

        return new String(bytes, 0, bytes.length, CIPHER_TEXT_CHARACTER_CODE);
    }

    /**
     * Encrypts a string using the provided keypair
     *
     * @param privateKeyEntry   The keypair to use to encrypt the text
     * @param plainText         The plain text to encrypt (should be at most 256bytes)
     * @return  Cipher text corresponding to the plain text
     * @throws Exception
     */
    private String encryptString(KeyStore.PrivateKeyEntry privateKeyEntry, String plainText)
            throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

        Cipher input;
        if(Build.VERSION.SDK_INT >= 23) {
            input = Cipher.getInstance(CIPHER);
        } else {
            input = Cipher.getInstance(CIPHER, CIPHER_PROVIDER);
        }
        input.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(
                outputStream, input);
        cipherOutputStream.write(plainText.getBytes(CIPHER_TEXT_CHARACTER_CODE));
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();

        return Base64.encodeToString(vals, Base64.DEFAULT);
    }
}
