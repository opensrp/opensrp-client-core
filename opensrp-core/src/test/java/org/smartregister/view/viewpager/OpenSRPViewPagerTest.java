package org.smartregister.view.viewpager;

import org.junit.Assert;

import org.junit.Ignore;
import org.junit.Test;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.ViewAttributes;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class OpenSRPViewPagerTest extends BaseUnitTest {

    @Test
    public void assertOpenSRPViewPagerInitializationTest() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(openSRPViewPager);
    }

    @Test
    @Ignore
    public void assertOpenSRPViewPagerInitializationTest2() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(ApplicationProvider.getApplicationContext(), ViewAttributes.attrs);
        Assert.assertNotNull(openSRPViewPager);
    }

    @Test
    public void assertOnInterceptTouchEventReturnsFalse() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(ApplicationProvider.getApplicationContext());
        org.junit.Assert.assertEquals(openSRPViewPager.onInterceptTouchEvent(null), false);
    }

    @Test
    public void assertOnTouchEventReturnsFalse() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(ApplicationProvider.getApplicationContext());
        org.junit.Assert.assertEquals(openSRPViewPager.onTouchEvent(null), false);
    }

}
