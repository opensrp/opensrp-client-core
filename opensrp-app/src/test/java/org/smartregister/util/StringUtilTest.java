package org.smartregister.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.smartregister.util.StringUtil.humanize;

public class StringUtilTest {

    @Test
    public void shouldCapitalize() throws Exception {
        assertEquals("Abc", humanize("abc"));
        assertEquals("Abc", humanize("Abc"));
    }

    @Test
    public void shouldReplaceUnderscoreWithSpace() throws Exception {
        assertEquals("Abc def", humanize("abc_def"));
        assertEquals("Abc def", humanize("Abc_def"));
    }

    @Test
    public void shouldHandleEmptyAndNull() throws Exception {
        assertEquals("", humanize(""));
    }
}
