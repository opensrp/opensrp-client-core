package org.smartregister.configuration;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.smartregister.commonregistry.CommonPersonObjectClient;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 28-09-2020.
 */
public interface ActivityStarter {

    void startProfileActivity(@NonNull Activity contextActivity, @NonNull CommonPersonObjectClient commonPersonObjectClient);

}
