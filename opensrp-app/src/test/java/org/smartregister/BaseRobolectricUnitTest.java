package org.smartregister;

import android.os.Build;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.shadows.ShadowDrawableResourcesImpl;
import org.smartregister.shadows.ShadowJobManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by onaio on 29/08/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {FontTextViewShadow.class, ShadowDrawableResourcesImpl.class, ShadowAppDatabase.class, ShadowJobManager.class}, sdk = Build.VERSION_CODES.O_MR1)
public abstract class BaseRobolectricUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

}
