package org.smartregister.util;

import static org.mockito.Mockito.doReturn;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.util.mock.OpenSRPImageLoaderTestActivity;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
public class OpenSRPImageLoaderTest extends BaseUnitTest {

    private OpenSRPImageLoaderTestActivity activity;

    @Mock
    private Context context;

    @Mock
    private CoreLibrary coreLibrary;

    private ActivityController<OpenSRPImageLoaderTestActivity> controller;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OpenSRPImageLoaderTestActivity.class);
        controller = Robolectric.buildActivity(OpenSRPImageLoaderTestActivity.class, intent);
        activity = controller.get();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(context).when(coreLibrary).context();
        controller.setup();
    }

    @Test
    public void assertActivityNotNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity.getInstance());
        Assert.assertNotNull(openSRPImageLoader);
    }

    @Test
    public void assertFragmentActivityConstructorInitializationNotNull() throws Exception {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity, -1);
        Assert.assertNotNull(openSRPImageLoader);
    }
}
