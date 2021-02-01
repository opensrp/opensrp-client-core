package org.smartregister.configuration;

import androidx.annotation.NonNull;

import java.util.ArrayList;

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
