package org.smartregister.util;

import android.util.Log;

import com.google.firebase.crashlytics.CustomKeysAndValues;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

public class CrashLyticsTree extends Timber.Tree {
    private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private static final String CRASHLYTICS_KEY_TAG = "tag";
    private static final String CRASHLYTICS_KEY_MESSAGE = "message";
    private String userName;

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return;
        }

        if (userName == null) {
            userName = DrishtiApplication.getInstance().getUsername();
        }
        try {
            CustomKeysAndValues customKeysAndValues = new CustomKeysAndValues.Builder().putInt(CRASHLYTICS_KEY_PRIORITY, priority)
                    .putString(CRASHLYTICS_KEY_TAG, tag)
                    .putString(CRASHLYTICS_KEY_MESSAGE, message)
                    .build();
            FirebaseCrashlytics.getInstance().setCustomKeys(customKeysAndValues);
            FirebaseCrashlytics.getInstance().setUserId(userName);
            if (t == null) {
                FirebaseCrashlytics.getInstance().recordException(new Exception(message));
            } else {
                FirebaseCrashlytics.getInstance().recordException(t);
            }

        } catch (NoClassDefFoundError e) {//We might not have Firebase in the classpath e.g. in another depending Library
            e.printStackTrace();
        }
    }
}
