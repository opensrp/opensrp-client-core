package org.smartregister;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 19-01-2021.
 */
public class ContextRobolectricTest extends BaseRobolectricUnitTest {

    @Test
    public void getInstanceShouldCallConstructorWhenContextIsNull() {
        ReflectionHelpers.setStaticField(Context.class, "context", null);
        Assert.assertNull(ReflectionHelpers.getStaticField(Context.class, "context"));

        Context.getInstance();

        Assert.assertNotNull(ReflectionHelpers.getStaticField(Context.class, "context"));
    }

    @Test
    public void setInstanceShouldReturnNullWhenContextPassedIsNull() {
        Assert.assertNull(Context.setInstance(null));
    }

}
