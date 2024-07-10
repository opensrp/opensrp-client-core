package org.smartregister.shadows;

import android.content.Context;
import android.support.annotation.NonNull;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.p2p.model.AppDatabase;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-05-31
 */

@Implements(AppDatabase.class)
public class ShadowAppDatabase {


    @Implementation
    public static AppDatabase getInstance(@NonNull Context context, @NonNull String passphrase) {
        // Do nothing
        return null;
    }
}
