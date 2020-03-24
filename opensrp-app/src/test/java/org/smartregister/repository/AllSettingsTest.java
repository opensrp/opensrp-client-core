package org.smartregister.repository;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllSettingsTest extends BaseUnitTest {

    @Mock
    private SettingsRepository settingsRepository;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    private AllSettings allSettings;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allSettings = new AllSettings(allSharedPreferences, settingsRepository);
    }

    @Test
    public void shouldFetchANMPassword() throws Exception {
        Mockito.when(settingsRepository.querySetting("anmPassword", "")).thenReturn("actual password");

        String actual = allSettings.fetchANMPassword();

        Mockito.verify(settingsRepository).querySetting("anmPassword", "");
        Assert.assertEquals("actual password", actual);
    }

    @Test
    public void shouldSavePreviousFetchIndex() throws Exception {
        allSettings.savePreviousFetchIndex("1234");

        Mockito.verify(settingsRepository).updateSetting("previousFetchIndex", "1234");
    }

    @Test
    public void shouldFetchPreviousFetchIndex() throws Exception {
        Mockito.when(settingsRepository.querySetting("previousFetchIndex", "0")).thenReturn("1234");

        String actual = allSettings.fetchPreviousFetchIndex();

        Mockito.verify(settingsRepository).querySetting("previousFetchIndex", "0");
        Assert.assertEquals("1234", actual);
    }

    @Test
    public void shouldSavePreviousFormSyncIndex() throws Exception {
        allSettings.savePreviousFormSyncIndex("1234");

        Mockito.verify(settingsRepository).updateSetting("previousFormSyncIndex", "1234");
    }

    @Test
    public void shouldFetchPreviousFormSyncIndex() throws Exception {
        Mockito.when(settingsRepository.querySetting("previousFormSyncIndex", "0")).thenReturn("1234");

        String actual = allSettings.fetchPreviousFormSyncIndex();

        Mockito.verify(settingsRepository).querySetting("previousFormSyncIndex", "0");
        Assert.assertEquals("1234", actual);
    }

    @Test
    public void shouldSaveAppliedVillageFilter() throws Exception {
        allSettings.saveAppliedVillageFilter("munjanahalli");

        Mockito.verify(settingsRepository).updateSetting("appliedVillageFilter", "munjanahalli");
    }

    @Test
    public void shouldGetAppliedVillageFilter() throws Exception {
        allSettings.appliedVillageFilter("All");

        Mockito.verify(settingsRepository).querySetting("appliedVillageFilter", "All");
    }

    @Test
    public void assertRegisterANMCallsPreferenceAndRepositoryUpdate() throws Exception {
        Mockito.doNothing().when(allSharedPreferences).updateANMUserName(Mockito.anyString());
        Mockito.doNothing().doNothing().when(settingsRepository).updateSetting(Mockito.anyString(), Mockito.anyString());
        allSettings.registerANM("", "");
        Mockito.verify(allSharedPreferences, Mockito.times(1)).updateANMUserName(Mockito.anyString());
        Mockito.verify(settingsRepository, Mockito.times(1)).updateSetting(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void assertSaveANMLocationCallsRepositoryUpdate() {
        Mockito.doNothing().doNothing().when(settingsRepository).updateSetting(Mockito.anyString(), Mockito.anyString());
        allSettings.saveANMLocation("");
        Mockito.verify(settingsRepository, Mockito.times(1)).updateSetting(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void assertFetchANMLocationCallsRepositoryQuery() {
        Mockito.when(settingsRepository.querySetting(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Assert.assertEquals(allSettings.fetchANMLocation(), "");
        Mockito.verify(settingsRepository, Mockito.times(1)).querySetting(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void assertFetchUserInformationCallsRepositoryQuery() {
        Mockito.when(settingsRepository.querySetting(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Assert.assertEquals(allSettings.fetchUserInformation(), "");
        Mockito.verify(settingsRepository, Mockito.times(1)).querySetting(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void assertSaveUserInformationCallsRepositoryUpdate() {
        Mockito.doNothing().doNothing().when(settingsRepository).updateSetting(Mockito.anyString(), Mockito.anyString());
        allSettings.saveUserInformation("");
        Mockito.verify(settingsRepository, Mockito.times(1)).updateSetting(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void assertGetAuthParamsReturnsUserNamePassword() {
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("username");
        Mockito.when(settingsRepository.querySetting(Mockito.anyString(), Mockito.anyString())).thenReturn("password");
        Map<String, String> auth = allSettings.getAuthParams();
        Assert.assertEquals("username", auth.get("username"));
        Assert.assertEquals("password", auth.get("password"));
    }

    @Test
    public void testSaveANMTeam() {
        allSettings.saveANMTeam("team");

        Mockito.verify(settingsRepository).updateSetting("anmTeam", "team");
    }

    @Test
    public void testPut() {
        allSettings.put("testKey", "testValue");

        Mockito.verify(settingsRepository).updateSetting("testKey", "testValue");
    }

    @Test
    public void testGet() {
        Mockito.when(settingsRepository.querySetting("testKey", null)).thenReturn("testValue");

        String val = allSettings.get("testKey");

        Mockito.verify(settingsRepository).querySetting("testKey", null);
        Assert.assertEquals("testValue", val);
    }

    @Test
    public void testGetSetting() {
        Setting s = new Setting();
        s.setKey("testKey"); s.setValue("testValue");

        Mockito.when(settingsRepository.querySetting("testKey")).thenReturn(s);

        Setting setting = allSettings.getSetting("testKey");

        Mockito.verify(settingsRepository).querySetting("testKey");
        Assert.assertEquals("testKey", setting.getKey());
        Assert.assertEquals("testValue", setting.getValue());
    }

    @Test
    public void testGetSettingsByType() {
        List<Setting> ls = new ArrayList<>();

        Setting s = new Setting();
        s.setKey("testKey"); s.setValue("testValue");
        ls.add(s);
        Setting s2 = new Setting();
        s2.setKey("testKey2"); s2.setValue("testValue2");
        ls.add(s2);

        Mockito.when(settingsRepository.querySettingsByType("testType")).thenReturn(ls);

        List<Setting> settings = allSettings.getSettingsByType("testType");

        Mockito.verify(settingsRepository).querySettingsByType("testType");
        Assert.assertEquals("testKey", settings.get(0).getKey());
        Assert.assertEquals("testValue", settings.get(0).getValue());
    }

    @Test
    public void testPutSetting() {
        Setting s = new Setting();
        s.setKey("testKey"); s.setValue("testValue");

        allSettings.putSetting(s);

        Mockito.verify(settingsRepository).updateSetting(s);
    }

    @Test
    public void testGetUnsyncedSettings() {
        List<Setting> ls = new ArrayList<>();

        Setting s = new Setting();
        s.setKey("testUnsyncedKey"); s.setValue("testUnsyncedValue");
        ls.add(s);

        Mockito.when(settingsRepository.queryUnsyncedSettings()).thenReturn(ls);

        List<Setting> settings = allSettings.getUnsyncedSettings();

        Mockito.verify(settingsRepository).queryUnsyncedSettings();

        Assert.assertEquals("testUnsyncedKey", settings.get(0).getKey());
        Assert.assertEquals("testUnsyncedValue", settings.get(0).getValue());
    }
}
