package org.smartregister.util;

import android.support.annotation.NonNull;

import org.smartregister.exception.NewInstanceException;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020
 */

public class ConfigurationInstancesHelper {

    @NonNull
    public static <T> T newInstance(Class<T> clas) {
        try {
            return clas.newInstance();
        } catch (IllegalAccessException e) {
            Timber.e(e);
        } catch (InstantiationException e) {
            Timber.e(e);
        }

        throw new NewInstanceException("Could not create a new instance of " + clas.getName());
    }
}
