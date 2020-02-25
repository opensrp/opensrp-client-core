package org.smartregister.view.fragment;

import android.content.Intent;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import org.smartregister.view.fragment.mock.SecuredNativeSmartRegisterFragmentActivityMock;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
@Config(shadows = {FontTextViewShadow.class})
public class SecuredNativeSmartRegisterFragmentTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<SecuredNativeSmartRegisterFragmentActivityMock> controller;

    @InjectMocks
    private SecuredNativeSmartRegisterFragmentActivityMock activity;
    @Mock
    CoreLibrary coreLibrary;
    @Mock
    private org.smartregister.Context context_;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);

        Intent intent = new Intent(RuntimeEnvironment.application, SecuredNativeSmartRegisterFragmentActivityMock.class);
        controller = Robolectric.buildActivity(SecuredNativeSmartRegisterFragmentActivityMock.class, intent);

        CoreLibrary.init(context_);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context_);
        PowerMockito.when(context_.updateApplicationContext(Mockito.any(android.content.Context.class))).thenReturn(context_);
        Mockito.when(context_.IsUserLoggedOut()).thenReturn(false);

        activity = controller.create().start().resume().get();

    }

    @Test
    public void testActivityShouldNotBeNull() {

        Assert.assertNotNull(activity);
    }

    @After
    public void tearDown() {
        destroyController();
        activity = null;
        controller = null;
    }

    private void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }

        System.gc();
    }

    @Test
    public void assertActivityContainsFragments() {
        Assert.assertNotNull(activity.getFragmentManager().getFragments());
        Assert.assertTrue(activity.getSupportFragmentManager().getFragments().size() > 0);
    }
}
