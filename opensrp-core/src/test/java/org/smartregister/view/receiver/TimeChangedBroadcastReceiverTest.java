package org.smartregister.view.receiver;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.google.common.collect.ImmutableList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;

import java.util.ArrayList;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-07-2020.
 */
public class TimeChangedBroadcastReceiverTest extends BaseRobolectricUnitTest {

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(TimeChangedBroadcastReceiver.class, "singleton", null);
    }

    @Test
    public void initShouldCreateSingletonAndRegisterTimeChangedReceivers() {
        // Assert that the instance does not exist
        Assert.assertNull(ReflectionHelpers.getStaticField(TimeChangedBroadcastReceiver.class, "singleton"));

        // Remove previous receivers
        Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).clearRegisteredReceivers();

        // Assert that there are no registered recievers
        Assert.assertEquals(0, Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getRegisteredReceivers().size());

        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());

        // Assert that the receivers were registered
        TimeChangedBroadcastReceiver receiver = ReflectionHelpers.getStaticField(TimeChangedBroadcastReceiver.class, "singleton");
        Assert.assertNotNull(receiver);
        ImmutableList<ShadowApplication.Wrapper> receivers = Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getRegisteredReceivers();
        Assert.assertEquals(2, receivers.size());

        // Assert the time changed & timezone changed intent filtered were registered
        Assert.assertEquals(Intent.ACTION_TIME_CHANGED, receivers.get(0).intentFilter.getAction(0));
        Assert.assertEquals(Intent.ACTION_TIMEZONE_CHANGED, receivers.get(1).intentFilter.getAction(0));

        // Assert that the two actions were registered to the TimeChangedBroadcastReceiver
        Assert.assertEquals(receiver, receivers.get(0).broadcastReceiver);
        Assert.assertEquals(receiver, receivers.get(1).broadcastReceiver);
    }


    @Test
    public void initShouldCallDestroyWhenTheReceiverWasPreviouslyInitialised() {
        // Remove previous receivers
        Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).clearRegisteredReceivers();

        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());

        // Assert that the receivers were registered
        Assert.assertNotNull(TimeChangedBroadcastReceiver.getInstance());
        ImmutableList<ShadowApplication.Wrapper> receivers = Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getRegisteredReceivers();
        Assert.assertEquals(2, receivers.size());

        // Mock the application instance
        Context context = Mockito.spy(ApplicationProvider.getApplicationContext());
        TimeChangedBroadcastReceiver timeChangedBroadcastReceiver = TimeChangedBroadcastReceiver.getInstance();


        TimeChangedBroadcastReceiver.init(context);

        // Verify that destroy was called
        Mockito.verify(context).unregisterReceiver(timeChangedBroadcastReceiver);
    }


    @Test
    public void destroyShouldUnregisterTheReceiverWhenSingletonIsNotNull() {
        // Assert that the instance does not exist
        Assert.assertNull(ReflectionHelpers.getStaticField(TimeChangedBroadcastReceiver.class, "singleton"));

        // Remove previous receivers
        Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).clearRegisteredReceivers();

        // Assert that there are no registered receivers
        Assert.assertEquals(0, Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getRegisteredReceivers().size());

        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());

        // Assert that the receivers were registered
        Assert.assertNotNull(TimeChangedBroadcastReceiver.getInstance());
        ImmutableList<ShadowApplication.Wrapper> receivers = Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getRegisteredReceivers();
        Assert.assertEquals(2, receivers.size());


        // Call DESTROY
        TimeChangedBroadcastReceiver.destroy(ApplicationProvider.getApplicationContext());

        // Assert that the receivers were unregistered
        Assert.assertEquals(0, Shadows.shadowOf((Application) ApplicationProvider.getApplicationContext()).getRegisteredReceivers().size());
    }

    @Test
    public void addOnTimeChangedListenerShouldAddListener() {
        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());

        // Confirm that there are 0 listeners
        ArrayList<TimeChangedBroadcastReceiver.OnTimeChangedListener> listeners = ReflectionHelpers.getField(TimeChangedBroadcastReceiver.getInstance(), "onTimeChangedListeners");
        Assert.assertEquals(0, listeners.size());

        // Add a listener
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(Mockito.mock(TimeChangedBroadcastReceiver.OnTimeChangedListener.class));

        // Confirm that there is 1 listener
        listeners = ReflectionHelpers.getField(TimeChangedBroadcastReceiver.getInstance(), "onTimeChangedListeners");
        Assert.assertEquals(1, listeners.size());
    }

    @Test
    public void removeOnTimeChangedListenerShouldAddListener() {
        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());
        TimeChangedBroadcastReceiver.OnTimeChangedListener onTimeChangedListener = Mockito.mock(TimeChangedBroadcastReceiver.OnTimeChangedListener.class);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(onTimeChangedListener);

        // Confirm that there is 1 listener
        ArrayList<TimeChangedBroadcastReceiver.OnTimeChangedListener> listeners = ReflectionHelpers.getField(TimeChangedBroadcastReceiver.getInstance(), "onTimeChangedListeners");
        Assert.assertEquals(1, listeners.size());

        // Add a listener
        TimeChangedBroadcastReceiver.getInstance().removeOnTimeChangedListener(onTimeChangedListener);

        // Confirm that there are 0 listeners
        listeners = ReflectionHelpers.getField(TimeChangedBroadcastReceiver.getInstance(), "onTimeChangedListeners");
        Assert.assertEquals(0, listeners.size());
    }

    @Test
    public void onReceiveShouldCallListenerOnTimeChangedWhenIntentIsTimeChanged() {
        // Add listener
        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());
        TimeChangedBroadcastReceiver.OnTimeChangedListener onTimeChangedListener = Mockito.mock(TimeChangedBroadcastReceiver.OnTimeChangedListener.class);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(onTimeChangedListener);


        TimeChangedBroadcastReceiver.getInstance().onReceive(ApplicationProvider.getApplicationContext(), new Intent(Intent.ACTION_TIME_CHANGED));

        // Verify that time has changed
        Mockito.verify(onTimeChangedListener).onTimeChanged();
    }

    @Test
    public void onReceiveShouldCallListenerOnTimeZoneChangedWhenIntentIsTimeZoneChanged() {
        // Add listener
        TimeChangedBroadcastReceiver.init(ApplicationProvider.getApplicationContext());
        TimeChangedBroadcastReceiver.OnTimeChangedListener onTimeChangedListener = Mockito.mock(TimeChangedBroadcastReceiver.OnTimeChangedListener.class);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(onTimeChangedListener);


        TimeChangedBroadcastReceiver.getInstance().onReceive(ApplicationProvider.getApplicationContext(), new Intent(Intent.ACTION_TIMEZONE_CHANGED));

        // Verify that time has changed
        Mockito.verify(onTimeChangedListener).onTimeZoneChanged();
    }

}