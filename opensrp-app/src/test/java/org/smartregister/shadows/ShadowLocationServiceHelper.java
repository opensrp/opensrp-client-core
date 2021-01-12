package org.smartregister.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.smartregister.sync.helper.LocationServiceHelper;

/**
 * Created by Vincent Karuri on 12/01/2021
 */

@Implements(LocationServiceHelper.class)
public class ShadowLocationServiceHelper extends Shadow {

    private static LocationServiceHelper locationServiceHelper;

    @Implementation
    public static LocationServiceHelper getInstance() {
        return locationServiceHelper;
    }

    public static void setLocationServiceHelper(LocationServiceHelper locationServiceHelper) {
        ShadowLocationServiceHelper.locationServiceHelper = locationServiceHelper;
    }
}
