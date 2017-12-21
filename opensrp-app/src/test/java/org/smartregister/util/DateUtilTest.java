package org.smartregister.util;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;
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
        Assert.assertEquals("1d", DateUtil.getDuration(100000000l));
        Assert.assertEquals("4w 6d", DateUtil.getDuration(3000000000l));
        Assert.assertEquals("3m 1w", DateUtil.getDuration(TimeUnit.DAYS.toMillis(100)));
        Assert.assertEquals("1y 1m", DateUtil.getDuration(36500000000l));
        Assert.assertNotNull(DateUtil.getDuration(new DateTime(0)));
        Assert.assertNull(DateUtil.getDuration(null));
    }

    @Test
    public void assertCheckIfDateThreeMonthsOlderReturnsBoolean() {
        Assert.assertEquals(DateUtil.checkIfDateThreeMonthsOlder(new Date()), false);
    }

    @Test
    public void assertIsVaidDateReturnsBoolean() {
        Assert.assertEquals(DateUtil.isValidDate(null), false);
        Assert.assertEquals(DateUtil.isValidDate("invaliddate"), false);
        Assert.assertEquals(DateUtil.isValidDate("2017-10-20"), true);
    }

    @Test
    public void assertWeekDifferenceReturnsInt() {
        LocalDate start = new LocalDate(1447487308000l);
        LocalDate end = new LocalDate(1510645708000l);
        Assert.assertEquals(DateUtil.weekDifference(start, end), 104);
    }
}
