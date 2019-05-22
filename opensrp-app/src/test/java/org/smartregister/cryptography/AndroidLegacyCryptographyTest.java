package org.smartregister.cryptography;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

public class AndroidLegacyCryptographyTest extends BaseUnitTest {

    @Mock
    KeyStore keystore;

    AndroidLegacyCryptography androidLegacyCryptography;

    @Before
    public void setUp() throws KeyStoreException {

        MockitoAnnotations.initMocks(this);
        androidLegacyCryptography = new AndroidLegacyCryptography(RuntimeEnvironment.application);
        androidLegacyCryptography.setKeyStore(keystore);
    }


    @Test
    public void testAndroidLegacyCryptographyClassInitsCorrectly() {

        Assert.assertNotNull(androidLegacyCryptography);
        Assert.assertNotNull(androidLegacyCryptography.secureRandom);
        Assert.assertNotNull(androidLegacyCryptography.getAESMode());
        Assert.assertEquals(AndroidLegacyCryptography.AES_MODE, androidLegacyCryptography.getAESMode());

    }

}
