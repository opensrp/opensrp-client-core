package org.ei.opensrp.view.activity;

import org.ei.opensrp.R;
import org.ei.opensrp.setup.DrishtiTestRunner;
import org.ei.opensrp.shadows.ShadowContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;

@RunWith(DrishtiTestRunner.class)
@Config(shadows = {ShadowContext.class})
public class NativeHomeActivityTest {

    private NativeHomeActivity homeActivity;

    @Before
    public void setup() {
        homeActivity = Robolectric.buildActivity(NativeHomeActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }

    @Test
    public void shouldLaunchEcRegisterOnPressingEcRegisterButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_ec_register, NativeECSmartRegisterActivity.class);
    }

    @Test
    public void shouldLaunchAncRegisterOnPressingAncRegisterButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_anc_register, NativeANCSmartRegisterActivity.class);
    }

    @Test
    public void shouldLaunchPncRegisterOnPressingPncRegisterButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_pnc_register, NativePNCSmartRegisterActivity.class);
    }

    @Test
    public void shouldLaunchFpRegisterOnPressingFpRegisterButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_fp_register, NativeFPSmartRegisterActivity.class);
    }

    @Test
    public void shouldLaunchChildRegisterOnPressingChildRegisterButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_child_register, NativeChildSmartRegisterActivity.class);
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
        ShadowActivity shadowHome = Robolectric.shadowOf(homeActivity);

        homeActivity.findViewById(buttonId).performClick();

        assertEquals(clazz.getName(),
                shadowHome.getNextStartedActivity().getComponent().getClassName());
    }


}
