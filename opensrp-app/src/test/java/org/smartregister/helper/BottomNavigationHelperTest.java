package org.smartregister.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@Ignore
@RunWith(PowerMockRunner.class)
public class BottomNavigationHelperTest extends BaseUnitTest {

    @Mock
    private Bitmap bitmap;

    @Mock
    private Drawable drawable;

    @Mock
    private GradientDrawable gradientDrawable;

    @Mock
    private BitmapDrawable bitmapDrawable;

    private BottomNavigationHelper bottomNavigationHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bottomNavigationHelper = new BottomNavigationHelper();
    }

    @Test
    @PrepareForTest({BitmapFactory.class})
    public void testConvertDrawableToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);

        Resources resources = Mockito.mock(Resources.class);
        Assert.assertNotNull(resources);

        Mockito.doReturn(drawable).when(resources).getDrawable(INITIALS_RESOURCE_ID);
        Assert.assertNotNull(drawable);

        mockStatic(BitmapFactory.class);
        when(BitmapFactory.decodeResource(resources, INITIALS_RESOURCE_ID)).thenReturn(bitmap);
        Assert.assertNull(bitmap.copy(Bitmap.Config.ARGB_8888, true));

        spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);

        Assert.assertNotNull(bitmap);
    }

    @Test
    @PrepareForTest({BitmapFactory.class, Bitmap.class})
    public void testConvertGradientDrawableToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);

        Resources resources = Mockito.mock(Resources.class);
        Assert.assertNotNull(resources);

        Mockito.doReturn(gradientDrawable).when(resources).getDrawable(R.drawable.bottom_bar_initials_background);
        Assert.assertNotNull(gradientDrawable);

        int width = 27;
        int height = 27;
        mockStatic(Bitmap.class);
        when(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)).thenReturn(bitmap);
        Assert.assertNotNull(bitmap);

        spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);
        Assert.assertNotNull(bitmap);
        Mockito.verify(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
    }

    @Test
    public void testConvertBitDrawableToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);

        Resources resources = Mockito.mock(Resources.class);
        Mockito.doReturn(bitmapDrawable).when(resources).getDrawable(R.drawable.bottom_bar_initials_background);
        Assert.assertNotNull(bitmapDrawable);

        Mockito.doReturn(bitmap).when(bitmapDrawable).getBitmap();
        spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);

        Assert.assertNotNull(bitmap);
    }

    @Test
    public void convertDrawableResToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);

        Resources resources = Mockito.mock(Resources.class);
        Assert.assertNotNull(resources);

        Mockito.doReturn(bitmap).when(spyBottomNavigationHelper).convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources);
        Assert.assertNotNull(bitmap);

        spyBottomNavigationHelper.writeOnDrawable(INITIALS_RESOURCE_ID, INITIALS_TEXT, resources);
        Mockito.verify(bitmap).copy(Bitmap.Config.ARGB_8888, true);
    }
}
