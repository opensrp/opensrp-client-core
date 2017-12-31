package org.smartregister;

import android.view.View;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.customshadows.FontTextViewShadow;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by onaio on 29/08/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {FontTextViewShadow.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public abstract class BaseUnitTest {
    public void resetWindowManager() {

        try {

            final Class<?> btclass = Class.forName("com.android.internal.os.BackgroundThread");
            Object backgroundThreadSingleton = ReflectionHelpers.getStaticField(btclass, "sInstance");
            if (backgroundThreadSingleton != null) {
                btclass.getMethod("quit").invoke(backgroundThreadSingleton);
                ReflectionHelpers.setStaticField(btclass, "sInstance", null);
                ReflectionHelpers.setStaticField(btclass, "sHandler", null);
            }

            Class clazz = ReflectionHelpers.loadClass(getClass().getClassLoader(), "android.view.WindowManagerGlobal");
            Object instance = ReflectionHelpers.callStaticMethod(clazz, "getInstance");

            Object lock = ReflectionHelpers.getField(instance, "mLock");

            ArrayList<Object> roots = ReflectionHelpers.getField(instance, "mRoots");

            synchronized (lock) {
                for (int i = 0; i < roots.size(); i++) {
                    ReflectionHelpers.callInstanceMethod(instance, "removeViewLocked",
                            ReflectionHelpers.ClassParameter.from(int.class, i),
                            ReflectionHelpers.ClassParameter.from(boolean.class, false));
                }
            }

            // Views will still be held by this array. We need to clear it out to ensure
            // everything is released.
            Collection<View> dyingViews = ReflectionHelpers.getField(instance, "mDyingViews");
            dyingViews.clear();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        System.out.println("Resetting Window Manager");

    }

}
