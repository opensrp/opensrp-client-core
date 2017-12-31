package org.smartregister.view.activity;

import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.service.ZiggyService;
import org.smartregister.shadows.SecuredActivityShadow;
import org.smartregister.shadows.ShadowContext;
import org.smartregister.view.activity.mock.NativeECSmartRegisterActivityMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by kaderchowdhury on 11/11/17.
 */
@Config(shadows = {ShadowContext.class,SecuredActivityShadow.class,FontTextViewShadow.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
public class NativeECSmartRegisterActivityTestTwo extends BaseUnitTest {

    private ActivityController<NativeECSmartRegisterActivityMock> controller;

    @InjectMocks
    private NativeECSmartRegisterActivityMock activity;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private ZiggyService ziggyService;

    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        activity.setContext(context_);
        when(context_.ziggyService()).thenReturn(ziggyService);
        activity = Robolectric.buildActivity(NativeECSmartRegisterActivityMock.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

    }

    @Test
    public void assertTestingTestToSeeTestWorks() {
        Assert.assertNotNull(activity);
    }

}
