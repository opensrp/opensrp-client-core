package org.ei.opensrp.util;

import org.junit.Test;

import static org.ei.opensrp.util.StringUtil.humanize;
import static org.junit.Assert.assertEquals;

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
