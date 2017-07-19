package org.smartregister.util;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Weeks;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static String DATE_FORMAT_FOR_TIMELINE_EVENT = "dd-MM-yyyy";

    private static DateUtility dateUtility = new RealDate();

    public static void fakeIt(LocalDate fakeDayAsToday) {
        dateUtility = new MockDate(fakeDayAsToday);
    }

    public static void setDefaultDateFormat(String defaultDateFormat) {
        DEFAULT_DATE_FORMAT = defaultDateFormat;
    }

    public static LocalDate today() {
        return dateUtility.today();
    }

    public static String formatDateForTimelineEvent(String unformattedDate) {
        return formatDate(unformattedDate, DATE_FORMAT_FOR_TIMELINE_EVENT);
    }

    public static String formatDate(String unformattedDate) {
        return formatDate(unformattedDate, DEFAULT_DATE_FORMAT);
    }

    public static String formatDate(String date, String pattern) {
        try {
            return LocalDate.parse(date).toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDate(LocalDate date, String pattern) {
        try {
            return date.toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatFromISOString(String date, String pattern) {
        try {
            return getLocalDateFromISOString(date).toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static LocalDate getLocalDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            Date formattedDate = format.parse(date);
            return new LocalDate(formattedDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate getLocalDateFromISOString(String date) {
        try {
            return LocalDateTime.parse(date).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    public static int dayDifference(LocalDate startDate, LocalDate endDate) {
        try {
            return Math.abs(Days.daysBetween(startDate, endDate).getDays());
        } catch (Exception e) {
            return 0;
        }
    }

    public static int weekDifference(LocalDate startDate, LocalDate endDate) {
        try {
            return Math.abs(Weeks.weeksBetween(startDate, endDate).getWeeks());
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isValidDate(String dateString) {
        if(dateString==null||dateString.length() != "yyyy-MM-dd".length()) {
            return false;
        }

        return dateString.matches("\\d{4}-\\d{2}-\\d{2}");

    }

}

interface DateUtility {
    LocalDate today();
}

class RealDate implements DateUtility {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}

class MockDate implements DateUtility {
    private LocalDate fakeDay;

    MockDate(LocalDate fakeDay) {
        this.fakeDay = fakeDay;
    }

    @Override
    public LocalDate today() {
        return fakeDay;
    }
}
