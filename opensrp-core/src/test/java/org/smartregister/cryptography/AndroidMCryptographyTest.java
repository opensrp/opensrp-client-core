package org.smartregister.cryptography;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseUnitTest;

/**
 * Created by ndegwamartin on 2019-05-22.
 */

public class AndroidMCryptographyTest extends BaseUnitTest {


    private AndroidMCryptography androidMCryptography;

    @Before
    public void setUp() throws Exception {

        

        androidMCryptography = new AndroidMCryptography(ApplicationProvider.getApplicationContext());
    }


    @Test
    public void testAndroidMCryptographyClassInitsCorrectly() {

        Assert.assertNotNull(androidMCryptography);
        Assert.assertNotNull(androidMCryptography.secureRandom);
        Assert.assertNotNull(androidMCryptography.getAESMode());
        Assert.assertEquals(AndroidMCryptography.AES_MODE, androidMCryptography.getAESMode());

    }

}
