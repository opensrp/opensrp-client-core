package org.smartregister.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.util.NetworkUtils;

/**
 * Created by samuelgithengi on 8/11/20.
 */
@Implements(NetworkUtils.class)
public class ShadowNetworkUtils {

    @Implementation
    public static boolean isNetworkAvailable() {
        return false;
    }

}
