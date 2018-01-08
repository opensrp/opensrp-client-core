package org.smartregister.shadows;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowPendingIntent;

/**
 * Created by Raihan Ahmed on 17/12/17.
 */
@Implements(AlarmManager.class)
public class AlarmManagerShadow extends ShadowAlarmManager {


    public AlarmManagerShadow() {

    }

    @Implementation
    public void setRepeating(int type, long triggerAtMillis,
                             long intervalMillis, PendingIntent operation) {

    }

}
