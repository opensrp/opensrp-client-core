package org.smartregister.view.activity;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.service.ZiggyService;
import org.smartregister.view.activity.mock.NativeHomeActivityMock;


public class NativeHomeActivityTest extends BaseRobolectricUnitTest {

    private NativeHomeActivityMock homeActivity;

    @Mock
    private ZiggyService ziggyService;

    @Before
    public void setUp() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "ziggyService", ziggyService);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), NativeHomeActivityMock.class);
        homeActivity = Robolectric.buildActivity(NativeHomeActivityMock.class, intent)
                .create()
                .start()
                .resume()
                .visible()
                .get();

    }

    @Test
    public void assertHomeActivityNotNull() {
        Assert.assertNotNull(homeActivity);
    }

    @Test
    public void shouldLaunchEcRegisterOnPressingEcRegisterButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_ec_register, NativeECSmartRegisterActivity.class);
    }

    @Test
    public void shouldLaunchReportingActivityOnPressingReportingButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_reporting, ReportsActivity.class);
    }

    @Test
    public void shouldLaunchVideosActivityOnPressingVideosButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_videos, VideosActivity.class);
    }

    public <T> void verifyLaunchOfActivityOnPressingButton(int buttonId, Class<T> clazz) {
//        ShadowActivity shadowHome = Shadows.shadowOf(homeActivity);

        homeActivity.findViewById(buttonId).performClick();

//        assertEquals(clazz.getName(),getNextStartedActivity().getComponent().getClassName());
    }


}
