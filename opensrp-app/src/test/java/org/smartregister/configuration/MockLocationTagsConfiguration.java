package org.smartregister.configuration;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class MockLocationTagsConfiguration implements org.smartregister.configuration.LocationTagsConfiguration {
    @NonNull
    @Override
    public ArrayList<String> getAllowedLevels() {
        return new ArrayList<>(Arrays.asList("Country", "County", "Town", "Region", "District", "Ward", "Health Facility", "Village", "Village Sublocations"));
    }

    @NonNull
    @Override
    public String getDefaultLocationLevel() {
        return "Village Sublocations";
    }

    @NonNull
    @Override
    public ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList("Country","County", "Town","Region","District","Ward" , "Health Facility", "Village", "Village Sublocations"));
    }

    @NonNull
    @Override
    public ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<String>(Arrays.asList("County", "Town", "MOH Jhpiego Facility Name", "Village"));
    }
}
