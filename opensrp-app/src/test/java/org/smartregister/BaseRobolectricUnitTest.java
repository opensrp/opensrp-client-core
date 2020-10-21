package org.smartregister;

import android.os.Build;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.customshadows.ShadowLocalBroadcastManager;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.shadows.ShadowDrawableResourcesImpl;
import com.evernote.android.job.ShadowJobManager;
import org.smartregister.shadows.ShadowSQLiteDatabase;

/**
 * Created by onaio on 29/08/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class, shadows = {ShadowLocalBroadcastManager.class, FontTextViewShadow.class, ShadowDrawableResourcesImpl.class, ShadowAppDatabase.class, ShadowJobManager.class, ShadowSQLiteDatabase.class}, sdk = Build.VERSION_CODES.O_MR1)
public abstract class BaseRobolectricUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public static final int ASYNC_TIMEOUT=2000;

}
