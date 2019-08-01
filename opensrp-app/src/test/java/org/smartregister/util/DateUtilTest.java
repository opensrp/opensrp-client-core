package org.smartregister.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class DateUtilTest extends BaseUnitTest {

    @Mock
    public CoreLibrary coreLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void assertDateUtilNotNUll() {
        org.junit.Assert.assertNotNull(new DateUtil());
    }

    @Test
    public void assertGetDurationTest() {

        Assert.assertEquals("1d", DateUtil.getDuration(RuntimeEnvironment.application, 100000000l));
        Assert.assertEquals("5w 1d", DateUtil.getDuration(RuntimeEnvironment.application, 3110400000l));
        Assert.assertEquals("5w", DateUtil.getDuration(RuntimeEnvironment.application, 3024000000l));
        Assert.assertEquals("3m 1w", DateUtil.getDuration(RuntimeEnvironment.application, TimeUnit.DAYS.toMillis(100)));
        Assert.assertEquals("1y", DateUtil.getDuration(RuntimeEnvironment.application, 31363200000l));
        Assert.assertEquals("1y 1m", DateUtil.getDuration(RuntimeEnvironment.application, 36500000000l));
        Assert.assertEquals("2y", DateUtil.getDuration(RuntimeEnvironment.application, 63113852000l));
        Assert.assertNotNull(DateUtil.getDuration(RuntimeEnvironment.application, new DateTime(0)));
        Assert.assertNull(DateUtil.getDuration(RuntimeEnvironment.application, null));
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
