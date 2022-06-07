package org.smartregister.broadcastreceivers;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import androidx.test.core.app.ApplicationProvider;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-06-2020.
 */
public class OpenSRPClientBroadCastReceiverTest extends BaseRobolectricUnitTest {

    @Mock
    private SecuredActivity activity;

    private OpenSRPClientBroadCastReceiver openSRPClientBroadCastReceiver;
    private DrishtiApplication drishtiApplication;

    @Before
    public void setUp() throws Exception {
        openSRPClientBroadCastReceiver = new OpenSRPClientBroadCastReceiver(activity);
        drishtiApplication = Mockito.spy(DrishtiApplication.getInstance());

        Mockito.doNothing().when(activity).showToast(Mockito.any());
        Mockito.doReturn(drishtiApplication).when(activity).getApplication();
    }

    @Test
    public void onReceiveShouldLogoutUserWhenActionTimeChanged() {
        Intent intent = new Intent(Intent.ACTION_TIME_CHANGED);
        openSRPClientBroadCastReceiver.onReceive(ApplicationProvider.getApplicationContext(), intent);

        Mockito.verify(drishtiApplication).logoutCurrentUser();
    }

    @Test
    public void onReceiveShouldLogoutUserWhenActionTimeZoneChanged() {
        Intent intent = new Intent(Intent.ACTION_TIMEZONE_CHANGED);
        openSRPClientBroadCastReceiver.onReceive(ApplicationProvider.getApplicationContext(), intent);

        Mockito.verify(drishtiApplication).logoutCurrentUser();
    }
}