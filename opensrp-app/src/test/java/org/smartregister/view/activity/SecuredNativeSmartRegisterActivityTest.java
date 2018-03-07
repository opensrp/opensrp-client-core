package org.smartregister.view.activity;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;

/**
 * Created by kaderchowdhury on 11/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
@PrepareForTest({CoreLibrary.class})
public class SecuredNativeSmartRegisterActivityTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<SecuredNativeSmartRegisterActivity> controller;

    private SecuredNativeSmartRegisterActivity activity;

    @Mock
    private org.smartregister.Context context_;
    @Mock
    CoreLibrary coreLibrary;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        Intent intent = new Intent(RuntimeEnvironment.application, SecuredNativeSmartRegisterActivity.class);
//        controller = Robolectric.buildActivity(SecuredNativeSmartRegisterActivity.class, intent);
//        activity = controller.get();
//        Context context = CoreLibrary.getInstance().context().updateApplicationContext(activity.getApplicationContext());
//        this.context_ = context;
//        controller.start();
//        controller.create();
    }

    @Test
    public void assertActivityTest() {
//        Assert.assertNotNull(activity);
    }

}
