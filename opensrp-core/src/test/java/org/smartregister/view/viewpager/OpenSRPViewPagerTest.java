package org.smartregister.view.viewpager;

import android.util.AttributeSet;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

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
    public void assertOpenSRPViewPagerInitializationTest2() {
        AttributeSet attributeSet = ApplicationProvider.getApplicationContext().getResources().getXml(android.R.layout.activity_list_item);
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(ApplicationProvider.getApplicationContext(), attributeSet);
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
