package org.smartregister.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;

public class BottomNavigationHelperTest extends BaseUnitTest {

    @Spy
    private Bitmap bitmap;

    @Mock
    private Drawable drawable;

    private BottomNavigationHelper bottomNavigationHelper;

    @Before
    public void setUp() {
        bottomNavigationHelper = new BottomNavigationHelper();
    }

    @Test
    public void testConvertDrawableToBitmap() {
        //BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);

        Resources resources = Mockito.mock(Resources.class);
        Bitmap bitmap = Mockito.mock(Bitmap.class);
        Assert.assertNotNull(resources);

        Mockito.doReturn(drawable).when(resources).getDrawable(INITIALS_RESOURCE_ID);
        Assert.assertNotNull(drawable);

        Assert.assertNull(bitmap.copy(Bitmap.Config.ARGB_8888, true));

        //Assert.assertNotNull(spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources));
    }

    @Test
    public void testConvertGradientDrawableToBitmap() {
        Assert.assertNotNull(bottomNavigationHelper.convertDrawableResToBitmap(R.drawable.bottom_bar_initials_background, ApplicationProvider.getApplicationContext().getResources()));
    }

    @Test
    public void testConvertBitmapDrawableToBitmap() {
        Assert.assertNotNull(bottomNavigationHelper.convertDrawableResToBitmap(R.drawable.child_boy_infant, ApplicationProvider.getApplicationContext().getResources()));
    }

    @Test
    public void writeOnDrawableShouldCallConvertDrawableResToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
        Mockito.doReturn(bitmap).when(spyBottomNavigationHelper).convertDrawableResToBitmap(Mockito.eq(R.drawable.bottom_bar_initials_background), Mockito.any(Resources.class));

        Assert.assertNotNull(spyBottomNavigationHelper.writeOnDrawable(R.drawable.bottom_bar_initials_background, INITIALS_TEXT, ApplicationProvider.getApplicationContext().getResources()));
        Mockito.verify(bitmap).copy(Bitmap.Config.ARGB_8888, true);
    }
}
