package org.smartregister.view.activity;

import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.service.ZiggyService;
import org.smartregister.shadows.SecuredActivityShadow;
import org.smartregister.shadows.ShadowContext;
import org.smartregister.view.activity.mock.NativeHomeActivityMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@Config(shadows = {ShadowContext.class, SecuredActivityShadow.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
public class NativeHomeActivityTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private NativeHomeActivityMock homeActivity;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private ZiggyService ziggyService;

    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        when(coreLibrary.context()).thenReturn(context_);
        when(context_.updateApplicationContext(any(Context.class))).thenReturn(context_);
        when(context_.ziggyService()).thenReturn(ziggyService);
        Intent intent = new Intent(RuntimeEnvironment.application, NativeHomeActivityMock.class);
        homeActivity = Robolectric.buildActivity(NativeHomeActivityMock.class)
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

    @Ignore // FIXME Failing test
    @Test
    public void shouldLaunchReportingActivityOnPressingReportingButton() {
        verifyLaunchOfActivityOnPressingButton(R.id.btn_reporting, ReportsActivity.class);
    }

    @Ignore // FIXME Failing test
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
