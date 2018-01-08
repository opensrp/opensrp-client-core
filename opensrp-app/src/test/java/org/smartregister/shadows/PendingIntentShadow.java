package org.smartregister.shadows;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.Menu;
import android.view.MenuItem;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowPendingIntent;
import org.smartregister.Context;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by Raihan Ahmed on 17/12/17.
 */
@Implements(PendingIntent.class)
public class PendingIntentShadow extends ShadowPendingIntent {

    public static PendingIntent realPendingIntentObject;

    public PendingIntentShadow() {

    }
    public static PendingIntent getBroadcast(android.content.Context context, int requestCode,
                                             Intent intent, int flags) {
        realPendingIntentObject = Mockito.mock(PendingIntent.class);
        return realPendingIntentObject;
    }

}
