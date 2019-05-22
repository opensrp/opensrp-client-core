package org.smartregister.cryptography;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

import java.security.KeyStore;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

public class AndroidMCryptographyTest extends BaseUnitTest {

    @Mock
    KeyStore keystore;

    AndroidMCryptography androidMCryptography;

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
