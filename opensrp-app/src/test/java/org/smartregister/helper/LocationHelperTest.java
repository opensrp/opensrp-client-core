package org.smartregister.helper;

import android.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.view.controller.ANMLocationController;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class LocationHelperTest extends BaseRobolectricUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private Repository repository;

    private LocationHelper locationHelper;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        ArrayList<String> ALLOWED_LEVELS;
        String DEFAULT_LOCATION_LEVEL = "Health Facility";
        String SCHOOL = "School";

        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(SCHOOL);
        ALLOWED_LEVELS.add("MOH Jhpiego Facility Name");
        ALLOWED_LEVELS.add("Village");

        LocationHelper.init(ALLOWED_LEVELS, "Health Facility");
        locationHelper = LocationHelper.getInstance();

        repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
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

    @Test
    public void locationIdsFromHierarchy() {
        AllSettings allSettings = Mockito.spy(CoreLibrary.getInstance().context().allSettings());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSettings", allSettings);
        SettingsRepository settingsRepository = ReflectionHelpers.getField(allSettings, "settingsRepository");
        settingsRepository.updateMasterRepository(repository);

        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        ANMLocationController anmLocationController = Mockito.spy(CoreLibrary.getInstance().context().anmLocationController());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "anmLocationController", anmLocationController);

        Mockito.doReturn("d60e1ee9-19e9-4e7d-a949-39f790a0ceda").when(allSharedPreferences).fetchDefaultTeamId(Mockito.nullable(String.class));
        Mockito.doReturn("{\"locationsHierarchy\":{\"map\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":{\"children\":{\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":{\"children\":{\"fc747bcd-f812-4229-b40e-9de496e016ac\":{\"children\":{\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":{\"children\":{\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":{\"children\":{\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\":{\"id\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"label\":\"Kabila Village\",\"node\":{\"locationId\":\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\",\"name\":\"Kabila Village\",\"parentLocation\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Village\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"}},\"id\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"label\":\"Huruma\",\"node\":{\"locationId\":\"718b2864-7d6a-44c8-b5b6-bb375f82654e\",\"name\":\"Huruma\",\"parentLocation\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"MOH Jhpiego Facility Name\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"}},\"id\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"label\":\"Kabila\",\"node\":{\"locationId\":\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\",\"name\":\"Kabila\",\"parentLocation\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Ward\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"fc747bcd-f812-4229-b40e-9de496e016ac\"}},\"id\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"label\":\"Magu DC\",\"node\":{\"locationId\":\"fc747bcd-f812-4229-b40e-9de496e016ac\",\"name\":\"Magu DC\",\"parentLocation\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"}},\"id\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"label\":\"Mwanza\",\"node\":{\"locationId\":\"9429fcd2-fdbd-4026-a3ce-6890a791efda\",\"name\":\"Mwanza\",\"parentLocation\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Region\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\"}},\"id\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"label\":\"Tanzania\",\"node\":{\"locationId\":\"02472eaf-2e85-44c7-8720-ae15b915f9ed\",\"name\":\"Tanzania\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"02472eaf-2e85-44c7-8720-ae15b915f9ed\":[\"9429fcd2-fdbd-4026-a3ce-6890a791efda\"],\"9429fcd2-fdbd-4026-a3ce-6890a791efda\":[\"fc747bcd-f812-4229-b40e-9de496e016ac\"],\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\":[\"718b2864-7d6a-44c8-b5b6-bb375f82654e\"],\"fc747bcd-f812-4229-b40e-9de496e016ac\":[\"2d0d9d5b-f5cf-40c1-8f84-d0cef48250c7\"],\"718b2864-7d6a-44c8-b5b6-bb375f82654e\":[\"2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8\"]}}}")
                .when(anmLocationController).get();

        LocationHelper spiedLocationHelper = Mockito.spy(locationHelper);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", spiedLocationHelper);

        String locationIds = spiedLocationHelper.locationIdsFromHierarchy();

        Mockito.verify(spiedLocationHelper).locationsFromHierarchy(Mockito.eq(true), Mockito.nullable(String.class));
        Assert.assertEquals("718b2864-7d6a-44c8-b5b6-bb375f82654e,2c3a0ebd-f79d-4128-a6d3-5dfbffbd01c8", locationIds);
    }
}
