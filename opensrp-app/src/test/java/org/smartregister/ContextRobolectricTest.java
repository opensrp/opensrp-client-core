package org.smartregister;

import android.content.res.Configuration;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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

    @Test
    public void applicationContextShouldReturnNullWhenApplicationContextIsNull() {
        android.content.Context context = ReflectionHelpers.getField(Context.getInstance(), "applicationContext");
        Context.getInstance().setApplicationContext(null);

        // Call the actual method
        Assert.assertNull(Context.getInstance().applicationContext());

        // Return the previous context
        Context.getInstance().setApplicationContext(context);
    }

    @Test
    public void applicationContextShouldReturnThrowExceptionAndReturnApplicationContext() {
        android.content.Context context = ReflectionHelpers.getField(Context.getInstance(), "applicationContext");
        android.content.Context spiedContext = Mockito.spy(context);
        Context.getInstance().setApplicationContext(spiedContext);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new Exception("An exception");
            }
        }).when(spiedContext).getResources();

        // Call the actual method
        Assert.assertEquals(spiedContext, Context.getInstance().applicationContext());

        Mockito.verify(spiedContext, Mockito.times(0)).createConfigurationContext(Mockito.any(Configuration.class));

        // Return the previous context
        Context.getInstance().setApplicationContext(context);
    }

}
