package org.smartregister.view.customcontrols;

import org.junit.Assert;
import org.junit.Test;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseUnitTest;

/**
 * Created by Vincent Karuri on 12/01/2021
 */
public class CustomFontTextViewTest extends BaseUnitTest {

    @Test
    public void testCustomFontTextViewCreationShouldCreateValidCustomFontTextView() {
        CustomFontTextView customFontTextView = new CustomFontTextView(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(customFontTextView);
        Assert.assertTrue(customFontTextView instanceof  CustomFontTextView);
    }
}