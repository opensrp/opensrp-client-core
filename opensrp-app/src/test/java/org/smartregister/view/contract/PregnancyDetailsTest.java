package org.smartregister.view.contract;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PregnancyDetailsTest {

    private String magicDate = "2012-09-17";

    @Test
    public void isLastMonthOfPregnancy() throws Exception {
        PregnancyDetails pregnancyDetails = new PregnancyDetails("8", magicDate, 0);
        Assert.assertTrue(pregnancyDetails.isLastMonthOfPregnancy());

        pregnancyDetails = new PregnancyDetails("7", magicDate, 0);
        Assert.assertFalse(pregnancyDetails.isLastMonthOfPregnancy());
    }
}
