package org.smartregister.helper;

import android.util.Pair;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.controller.ANMLocationController;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class LocationHelperTest extends BaseUnitTest {

    private final String anmLocation = "{\"locationsHierarchy\":{\"map\":{\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":{\"children\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":{\"children\":{\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":{\"children\":{\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":{\"children\":{\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":{\"children\":{\"982eb3f3-b7e3-450f-a38e-d067f2345212\":{\"id\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"label\":\"Jambula Girls School\",\"node\":{\"locationId\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"name\":\"Jambula Girls School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"School\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"},\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\":{\"id\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"label\":\"Nsalo Secondary School\",\"node\":{\"locationId\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"name\":\"Nsalo Secondary School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"School\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"}},\"id\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"label\":\"Bukesa Urban Health Centre\",\"node\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"Health Facility\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"}},\"id\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"label\":\"Central Division\",\"node\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"Sub-county\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"}},\"id\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"label\":\"KCCA\",\"node\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false,\"serverVersion\":0},\"voided\":false,\"serverVersion\":0},\"tags\":[\"County\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"8340315f-48e4-4768-a1ce-414532b4c49b\"}},\"id\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"label\":\"Kampala\",\"node\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false,\"serverVersion\":0},\"tags\":[\"District\"],\"voided\":false,\"serverVersion\":0},\"parent\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\"}},\"id\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"label\":\"Uganda\",\"node\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"tags\":[\"Country\"],\"voided\":false,\"serverVersion\":0}}},\"parentChildren\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":[\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"],\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":[\"8340315f-48e4-4768-a1ce-414532b4c49b\"],\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":[\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"],\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":[\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"],\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":[\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"]}}}";

    @Mock
    private Context context;

    @Mock
    private ANMLocationController anmLocationController;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private LocationHelper locationHelper;

    @Before
    public void setUp() {
        ArrayList<String> ALLOWED_LEVELS;
        String DEFAULT_LOCATION_LEVEL = "Health Facility";
        String SCHOOL = "School";

        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(SCHOOL);

        LocationHelper.init(ALLOWED_LEVELS, "Health Facility");
        locationHelper = LocationHelper.getInstance();

        Mockito.when(context.anmLocationController()).thenReturn(anmLocationController);
        Mockito.when(anmLocationController.get()).thenReturn(anmLocation);
    }

    @Test
    public void testGetChildLocationIdForJambulaGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
            assertEquals("982eb3f3-b7e3-450f-a38e-d067f2345212", parentAndChildLocationIds.second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetChildLocationIdForNsaloGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
            assertEquals("ee08a6e0-3f73-4c28-b186-64d5cd06f4ce", parentAndChildLocationIds.second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetChildLocationIdForBukesaUrbanHealthCentre() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.second);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParentLocationIdForNsaloGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Nsalo Secondary School");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParentLocationIdForJambulaGirlsSchool() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Jambula Girls School");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetParentLocationIdForBukesaUrbanHealthCentre() {

        try {
            Pair<String, String> parentAndChildLocationIds = Whitebox.invokeMethod(locationHelper, "getParentAndChildLocationIds", "Bukesa Urban Health Centre");
            assertEquals("44de66fb-e6c6-4bae-92bb-386dfe626eba", parentAndChildLocationIds.first);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParentLocationIdSetterAndGetter() {

        locationHelper.setParentLocationId("parentId");
        assertEquals("parentId", locationHelper.getParentLocationId());
    }

    @Test
    public void testChildLocationIdSetterAndGetter() {

        locationHelper.setChildLocationId("childId");
        assertEquals("childId", locationHelper.getChildLocationId());
    }
}
