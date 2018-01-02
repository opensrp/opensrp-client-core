package org.smartregister.repository;

import java.util.HashMap;
import java.util.Map;

public class AllSettings {
    public static final String APPLIED_VILLAGE_FILTER_SETTING_KEY = "appliedVillageFilter";
    public static final String PREVIOUS_FETCH_INDEX_SETTING_KEY = "previousFetchIndex";
    public static final String PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY = "previousFormSyncIndex";
    private static final String ANM_PASSWORD_PREFERENCE_KEY = "anmPassword";
    private static final String ANM_LOCATION = "anmLocation";
    private static final String ANM_TEAM = "anmTeam";
    private static final String USER_INFORMATION = "userInformation";

    protected AllSharedPreferences preferences;
    protected SettingsRepository settingsRepository;

    public AllSettings(AllSharedPreferences preferences, SettingsRepository settingsRepository) {
        this.preferences = preferences;
        this.settingsRepository = settingsRepository;
    }

    public void registerANM(String userName, String password) {
        preferences.updateANMUserName(userName);
        settingsRepository.updateSetting(ANM_PASSWORD_PREFERENCE_KEY, password);
    }

    public void savePreviousFetchIndex(String value) {
        settingsRepository.updateSetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, value);
    }

    public String fetchPreviousFetchIndex() {
        return settingsRepository.querySetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, "0");
    }

    public void saveAppliedVillageFilter(String village) {
        settingsRepository.updateSetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, village);
    }

    public String appliedVillageFilter(String defaultFilterValue) {
        return settingsRepository
                .querySetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, defaultFilterValue);
    }

    public String fetchANMPassword() {
        return settingsRepository.querySetting(ANM_PASSWORD_PREFERENCE_KEY, "");
    }

    public String fetchPreviousFormSyncIndex() {
        return settingsRepository.querySetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, "0");
    }

    public void savePreviousFormSyncIndex(String value) {
        settingsRepository.updateSetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, value);
    }

    public void saveANMLocation(String anmLocation) {
        settingsRepository.updateSetting(ANM_LOCATION, anmLocation);
    }

    public void saveANMTeam(String anmTeam) {
        settingsRepository.updateSetting(ANM_TEAM, anmTeam);
    }

    public String fetchANMLocation() {
        return settingsRepository.querySetting(ANM_LOCATION, "");
    }

    public void saveUserInformation(String userInformation) {
        settingsRepository.updateSetting(USER_INFORMATION, userInformation);
    }

    public String fetchUserInformation() {
        return settingsRepository.querySetting(USER_INFORMATION, "");
    }

    public Map<String, String> getAuthParams() {
        Map<String, String> authParams = new HashMap<String, String>();
        authParams.put("username", preferences.fetchRegisteredANM());
        authParams.put("password", fetchANMPassword());
        return authParams;
    }
}
