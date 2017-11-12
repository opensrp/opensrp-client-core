package org.smartregister.util;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class DateUtilTest {

    @Test
    public void assertDateUtilNotNUll() {
     org.junit.Assert.assertNotNull(new DateUtil());
    }

    @Test
    public void assertGetDurationTest() {
        Assert.assertEquals("1d",DateUtil.getDuration(100000000l));
        Assert.assertEquals("4w 6d",DateUtil.getDuration(3000000000l));
        Assert.assertEquals("3m 1w",DateUtil.getDuration(TimeUnit.DAYS.toMillis(100)));
        Assert.assertEquals("1y 1m",DateUtil.getDuration(36500000000l));
        Assert.assertNotNull(DateUtil.getDuration(new DateTime(0)));
        Assert.assertNull(DateUtil.getDuration(null));
    }
}
