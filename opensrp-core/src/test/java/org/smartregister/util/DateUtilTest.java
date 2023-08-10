package org.smartregister.util;

import androidx.test.core.app.ApplicationProvider;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class DateUtilTest extends BaseUnitTest {
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private Context context;

    @Before
    public void setUp() {

        Mockito.when(coreLibrary.context()).thenReturn(context);
        Mockito.when(context.applicationContext()).thenReturn(ApplicationProvider.getApplicationContext());

    }

    @Test
    public void assertDateUtilNotNUll() {
        org.junit.Assert.assertNotNull(new DateUtil());
    }

    @Test
    public void assertGetDurationTest() {

        Locale locale = ApplicationProvider.getApplicationContext().getApplicationContext().getResources().getConfiguration().locale;

        Assert.assertEquals("1d", DateUtil.getDuration(100000000l, locale));
        Assert.assertEquals("5w 1d", DateUtil.getDuration(3110400000l, locale));
        Assert.assertEquals("5w", DateUtil.getDuration(3024000000l, locale));
        Assert.assertEquals("3m 1w", DateUtil.getDuration(TimeUnit.DAYS.toMillis(100), locale));
        Assert.assertEquals("1y", DateUtil.getDuration(31363200000l, locale));
        Assert.assertEquals("1y 1m", DateUtil.getDuration(36500000000l, locale));
        Assert.assertEquals("2y", DateUtil.getDuration(63113852000l, locale));

        Assert.assertEquals("1d", DateUtil.getDuration(100000000l));

        Assert.assertNotNull(DateUtil.getDuration(ApplicationProvider.getApplicationContext(), new DateTime(0)));
        Assert.assertNull(DateUtil.getDuration(ApplicationProvider.getApplicationContext(), null));
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

    @Test
    public void formatDateTest() {
        Assert.assertEquals("03-10-2019", DateUtil.formatDate(new LocalDate("2019-10-03"), "dd-MM-YYY"));
        Assert.assertEquals("", DateUtil.formatDate(new LocalDate("2019-10-03"), "KK-TT"));
    }

    @Test
    public void getLocalDateTest() {
        Assert.assertEquals(new LocalDate("2019-10-03"), DateUtil.getLocalDate("03/10/2019"));
        Assert.assertEquals(null, DateUtil.getLocalDate("03-15-2019"));
    }

    @Test
    public void differenceTest() {
        Assert.assertEquals(2, DateUtil.dayDifference(new LocalDate("2019-10-01"), new LocalDate("2019-10-03")));
        Assert.assertEquals(1, DateUtil.weekDifference(new LocalDate("2019-09-26"), new LocalDate("2019-10-03")));
    }

    @Test
    public void testSetDefaultDateFormatShouldSSetTheDefaultDateFormatOfDateUtil() {
        DateUtil.setDefaultDateFormat("yyyy/MM/dd");
        Assert.assertEquals("yyyy/MM/dd", DateUtil.DEFAULT_DATE_FORMAT);
    }


    @Test
    public void testFormatFromISOString() {
        String localDateTime = LocalDateTime.parse("22-09-23").toString(ISODateTimeFormat.dateTime());
        String date = DateUtil.formatFromISOString(localDateTime, "dd/MM/YY");
        Assert.assertEquals("23/09/22", date);
    }

    @Test
    public void testGetTimeFromMillis() {
        LocalDate date = DateUtil.getDateFromMillis(1691654548L);
        Assert.assertEquals("1970-01-20", date.toString());
    }
}

