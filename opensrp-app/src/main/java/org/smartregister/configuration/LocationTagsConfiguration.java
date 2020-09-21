package org.smartregister.configuration;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-09-2020.
 */
public interface LocationTagsConfiguration {

    @NonNull
    ArrayList<String> getAllowedLevels();

    @NonNull
    String getDefaultLocationLevel();

    @NonNull
    ArrayList<String> getLocationLevels();

    @NonNull
    ArrayList<String> getHealthFacilityLevels();

}
