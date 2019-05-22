package org.smartregister.cryptography;

import android.os.Build;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.UnsupportedEncodingException;
import java.security.KeyStoreException;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class CryptographicHelperTest {

    public static final String SAMPLE_STRING = "I am a high security string that needs to be hidden from prying eyes";


    public static final String SAMPLE_KEY_ALIAS = "SampleKeyAlias";

    private CryptographicHelper cryptographicHelper;

    @Mock
    private AndroidMCryptography androidMCryptography;

    @Mock
    private AndroidLegacyCryptography androidLegacyCryptography;

    @Before
    public void setUp() throws KeyStoreException {

        MockitoAnnotations.initMocks(this);

        cryptographicHelper = CryptographicHelper.getInstance(RuntimeEnvironment.application);

        Assert.assertNotNull(cryptographicHelper);
        cryptographicHelper.setMCryptography(androidMCryptography);
        cryptographicHelper.setLegacyCryptography(androidLegacyCryptography);
    }

    @Test
    public void testCryptographicHelperInitsCorrectly() {
        Assert.assertNotNull(cryptographicHelper);

    }

    @Test
    public void testCryptographicHelperInvokesAndroidMEncryptMethod() throws UnsupportedEncodingException {

        cryptographicHelper.encrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).encrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());

    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperInvokesLegacyEncryptMethod() throws UnsupportedEncodingException {
        cryptographicHelper.encrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).encrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());

    }


    @Test
    public void testCryptographicHelperInvokesAndroidMDecryptMethod() throws UnsupportedEncodingException {

        cryptographicHelper.decrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).decrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());

    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperInvokesLegacyDecryptMethod() throws UnsupportedEncodingException {
        cryptographicHelper.decrypt(SAMPLE_STRING.getBytes(CharEncoding.UTF_8), SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).decrypt(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyString());

    }

    @Test
    public void testCryptographicHelperInvokesAndroidMGenerateKeyMethod() {

        cryptographicHelper.generateKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).generateKey(SAMPLE_KEY_ALIAS);

    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperInvokesLegacyGenerateKeyMethod() {
        cryptographicHelper.generateKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).generateKey(SAMPLE_KEY_ALIAS);

    }

    @Test
    public void testCryptographicHelperInvokesAndroidMGetKeyMethod() {

        cryptographicHelper.getKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidMCryptography).getKey(SAMPLE_KEY_ALIAS);

    }

    @Test
    @Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void testCryptographicHelperInvokesLegacyGetKeyMethod() {
        cryptographicHelper.getKey(SAMPLE_KEY_ALIAS);

        Mockito.verify(androidLegacyCryptography).getKey(SAMPLE_KEY_ALIAS);

    }

}
