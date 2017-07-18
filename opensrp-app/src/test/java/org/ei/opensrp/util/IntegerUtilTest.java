package org.ei.opensrp.util;

import org.robolectric.RobolectricTestRunner;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
public class IntegerUtilTest extends TestCase {


    @Test
    public void shouldReturnDefaultValueWhenStringIsNaN() {
        int value = IntegerUtil.tryParse("NaN", 0);

        assertEquals(value, 0);
    }

    @Test
    public void shouldReturn0WhenStringValueIs0() {
        int value = IntegerUtil.tryParse("0", 0);

        assertEquals(value, 0);
    }

    @Test
    public void shouldReturn1WhenStringValueIs01() {
        int value = IntegerUtil.tryParse("01", 0);

        assertEquals(value, 1);
    }

    @Test
    public void shouldReturn10WhenStringValueIs10() {
        int value = IntegerUtil.tryParse("10", 0);

        assertEquals(value, 10);
    }

    @Test
    public void shouldReturnDefaultValueWhenStringIsEmpty() {
        int value = IntegerUtil.tryParse("", 1);

        assertEquals(value, 1);
    }

    @Test
    public void shouldReturnStringDefaultValueWhenStringIsNaN() {
        String value = IntegerUtil.tryParse("NaN", "0");

        assertEquals(value, "0");
    }

    @Test
    public void shouldReturnString0WhenStringValueIs0() {
        String value = IntegerUtil.tryParse("0", "0");

        assertEquals(value, "0");
    }

    @Test
    public void shouldReturnString1WhenStringValueIs01() {
        String value = IntegerUtil.tryParse("01", "0");

        assertEquals(value, "1");
    }

    @Test
    public void shouldReturnString10WhenStringValueIs10() {
        String value = IntegerUtil.tryParse("10", "0");

        assertEquals(value, "10");
    }

    @Test
    public void shouldReturnStringDefaultValueWhenStringIsEmpty() {
        String value = IntegerUtil.tryParse("", "1");

        assertEquals(value, "1");
    }
}
