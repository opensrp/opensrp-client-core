package org.smartregister.cryptography;

import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.security.KeyStore;

/**
 * Created by ndegwamartin on 2019-05-22.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class AndroidMCryptographyTest {

    @Mock
    private KeyStore keystore;

    private AndroidMCryptography androidMCryptography;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        androidMCryptography = new AndroidMCryptography(RuntimeEnvironment.application);
        androidMCryptography.setKeyStore(keystore);
    }


    @Test
    public void testAndroidMCryptographyClassInitsCorrectly() {

        Assert.assertNotNull(androidMCryptography);
        Assert.assertNotNull(androidMCryptography.secureRandom);
        Assert.assertNotNull(androidMCryptography.getAESMode());
        Assert.assertEquals(AndroidMCryptography.AES_MODE, androidMCryptography.getAESMode());

    }

}
