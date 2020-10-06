package org.smartregister.view;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Vincent Karuri on 06/10/2020
 */
public class LocationPickerViewTest extends BaseUnitTest {

    private LocationPickerView locationPickerView;

    @Before
    public void setUp() throws Exception {
        locationPickerView = new LocationPickerView(RuntimeEnvironment.application);
    }

    @Test
    public void initShouldCorrectlyInitializeLocationPicker() {
        String defaultLocation = "default_location";
        LocationHelper.init(new ArrayList<String>(){{ add(defaultLocation); }}, defaultLocation);
        locationPickerView.init();
    }
}