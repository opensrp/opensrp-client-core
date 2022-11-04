package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kaderchowdhury on 14/11/17.
 */

public class FloatUtilTest {

    @Test
    public void assertInitializationNotNull() {
        Assert.assertNotNull(new FloatUtil());
    }

    @Test
    public void assertTryParseWithInvalidValue() {
        Assert.assertEquals(FloatUtil.tryParse("invalid", 1.0f), new Float(1.0));
        Assert.assertEquals(FloatUtil.tryParse("invalid", "1"), "1");
    }

    @Test
    public void assertTryParseWithValidValue() {
        Assert.assertEquals(FloatUtil.tryParse("1", 1.0f), new Float(1.0f));
        Assert.assertEquals(FloatUtil.tryParse("1", "1"), "1.0");
    }
}
