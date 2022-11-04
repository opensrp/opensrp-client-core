package org.smartregister.view.customcontrols;

import org.junit.Assert;
import org.junit.Test;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseUnitTest;

/**
 * Created by Vincent Karuri on 12/01/2021
 */
public class CustomFontRadioButtonTest extends BaseUnitTest {

    @Test
    public void testCustomFontRadioButtonCreationShouldReturnAValidCustomFontRadioButton() {
        CustomFontRadioButton customFontRadioButton = new CustomFontRadioButton(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(customFontRadioButton);
        Assert.assertTrue(customFontRadioButton instanceof  CustomFontRadioButton);
    }
}