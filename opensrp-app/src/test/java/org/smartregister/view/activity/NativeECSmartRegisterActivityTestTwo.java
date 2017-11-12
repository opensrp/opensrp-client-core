package org.smartregister.view.activity;

import android.content.Intent;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.mock.NativeECSmartRegisterActivityMock;

/**
 * Created by kaderchowdhury on 11/11/17.
 */

public class NativeECSmartRegisterActivityTestTwo extends BaseUnitTest {

    private ActivityController<NativeECSmartRegisterActivityMock> controller;

    @InjectMocks
    private NativeECSmartRegisterActivityMock activity;

    @Mock
    private org.smartregister.Context context_;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);

        Intent intent = new Intent(RuntimeEnvironment.application, NativeECSmartRegisterActivityMock.class);
        controller = Robolectric.buildActivity(NativeECSmartRegisterActivityMock.class, intent);
        activity = controller.get();

        CoreLibrary.init(context_);
//        controller.setup();

    }

    @Test
    public void assertTestingTestToSeeTestWorks(){
        Assert.assertNotNull(activity);
    }

}
