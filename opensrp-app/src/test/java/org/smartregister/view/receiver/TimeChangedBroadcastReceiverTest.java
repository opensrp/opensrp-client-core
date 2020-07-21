package org.smartregister.view.receiver;

import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;

import java.util.List;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-07-2020.
 */
public class TimeChangedBroadcastReceiverTest extends BaseRobolectricUnitTest {

    @Test
    public void initShouldCreateSingletonAndRegisterTimeChangedReceivers() {
        // Assert that the instance does not exist
        Assert.assertNull(ReflectionHelpers.getStaticField(TimeChangedBroadcastReceiver.class, "singleton"));

        // Remove previous receivers
        List<ShadowApplication.Wrapper> receivers = ShadowApplication.getInstance().getRegisteredReceivers();
        receivers.clear();

        // Assert that there are no registered recievers
        Assert.assertEquals(0, ShadowApplication.getInstance().getRegisteredReceivers().size());

        TimeChangedBroadcastReceiver.init(RuntimeEnvironment.application);


        // Assert that the receivers were registered
        TimeChangedBroadcastReceiver receiver = ReflectionHelpers.getStaticField(TimeChangedBroadcastReceiver.class, "singleton");
        Assert.assertNotNull(receiver);
        receivers = ShadowApplication.getInstance().getRegisteredReceivers();
        Assert.assertEquals(2, receivers.size());

        // Assert the time changed & timezone changed intent filtered were registered
        Assert.assertEquals(Intent.ACTION_TIME_CHANGED, receivers.get(0).intentFilter.getAction(0));
        Assert.assertEquals(Intent.ACTION_TIMEZONE_CHANGED, receivers.get(1).intentFilter.getAction(0));

        // Assert that the two actions were registered to the TimeChangedBroadcastReceiver
        Assert.assertEquals(receiver, receivers.get(0).broadcastReceiver);
        Assert.assertEquals(receiver, receivers.get(1).broadcastReceiver);
    }


    @Test
    public void destroyShouldUnregisterTheReciever() {
        // Assert that the instance does not exist
        Assert.assertNull(ReflectionHelpers.getStaticField(TimeChangedBroadcastReceiver.class, "singleton"));

        // Remove previous receivers
        List<ShadowApplication.Wrapper> receivers = ShadowApplication.getInstance().getRegisteredReceivers();
        receivers.clear();

        // Assert that there are no registered recievers
        Assert.assertEquals(0, ShadowApplication.getInstance().getRegisteredReceivers().size());

        TimeChangedBroadcastReceiver.init(RuntimeEnvironment.application);

        // Assert that the receivers were registered
        Assert.assertNotNull(TimeChangedBroadcastReceiver.getInstance());
        receivers = ShadowApplication.getInstance().getRegisteredReceivers();
        Assert.assertEquals(2, receivers.size());


        // Call DESTROY
        TimeChangedBroadcastReceiver.destroy(RuntimeEnvironment.application);

        // Assert that the recievers were unregistered
        Assert.assertEquals(0, ShadowApplication.getInstance().getRegisteredReceivers().size());
    }

}