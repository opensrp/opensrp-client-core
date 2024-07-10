package org.smartregister.view.fragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
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

//        Intent intent = new Intent(RuntimeEnvironment.application, SecuredNativeSmartRegisterFragmentActivityMock.class);
//        controller = Robolectric.buildActivity(SecuredNativeSmartRegisterFragmentActivityMock.class, intent);

//        RuntimeEnvironment.application.startActivity(intent);
//        activity = controller.create().start().resume().get();
//        activity = Robolectric.setupActivity(SecuredNativeSmartRegisterFragmentActivityMock.class);

        CoreLibrary.init(context_);

//        MockFragment fragment = new MockFragment();
//        PowerMockito.mockStatic(CoreLibrary.class);
//        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
//        PowerMockito.when(coreLibrary.context()).thenReturn(context_);
//        PowerMockito.when(context_.updateApplicationContext(Mockito.any(android.content.Context.class))).thenReturn(context_);
        //when(context_.IsUserLoggedOut()).thenReturn(false);

//        SupportFragmentTestUtil.startFragment(fragment);

    }

//    @After
//    public void tearDown() {
//        destroyController();
//        activity = null;
//        controller = null;
//
//    }
//
//    private void destroyController() {
//        try {
//            activity.finish();
//            controller.pause().stop().destroy(); //destroy controller if we can
//
//        } catch (Exception e) {
//            Log.e(getClass().getCanonicalName(), e.getMessage());
//        }
//
//        System.gc();
//    }

    @Test
    public void assertThatCallToNewInstanceCreatesAFragment() {
//        junit.framework.Assert.assertNotNull(ServiceDialogFragment.newInstance(Collections.EMPTY_LIST, new ServiceWrapper()));
//        junit.framework.Assert.assertNotNull(ServiceDialogFragment.newInstance(new DateTime(), Collections.EMPTY_LIST, new ServiceWrapper(), true));
    }
}
