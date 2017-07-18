package org.ei.opensrp.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AllSettingsTest {
    @Mock
    private SettingsRepository settingsRepository;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    private AllSettings allSettings;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allSettings = new AllSettings(allSharedPreferences, settingsRepository);
    }

    @Test
    public void shouldFetchANMPassword() throws Exception {
        when(settingsRepository.querySetting("anmPassword", "")).thenReturn("actual password");

        String actual = allSettings.fetchANMPassword();

        verify(settingsRepository).querySetting("anmPassword", "");
        assertEquals("actual password", actual);
    }

    @Test
    public void shouldSavePreviousFetchIndex() throws Exception {
        allSettings.savePreviousFetchIndex("1234");

        verify(settingsRepository).updateSetting("previousFetchIndex", "1234");
    }

    @Test
    public void shouldFetchPreviousFetchIndex() throws Exception {
        when(settingsRepository.querySetting("previousFetchIndex", "0")).thenReturn("1234");

        String actual = allSettings.fetchPreviousFetchIndex();

        verify(settingsRepository).querySetting("previousFetchIndex", "0");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldSavePreviousFormSyncIndex() throws Exception {
        allSettings.savePreviousFormSyncIndex("1234");

        verify(settingsRepository).updateSetting("previousFormSyncIndex", "1234");
    }

    @Test
    public void shouldFetchPreviousFormSyncIndex() throws Exception {
        when(settingsRepository.querySetting("previousFormSyncIndex", "0")).thenReturn("1234");

        String actual = allSettings.fetchPreviousFormSyncIndex();

        verify(settingsRepository).querySetting("previousFormSyncIndex", "0");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldSaveAppliedVillageFilter() throws Exception {
        allSettings.saveAppliedVillageFilter("munjanahalli");

        verify(settingsRepository).updateSetting("appliedVillageFilter", "munjanahalli");
    }

    @Test
    public void shouldGetAppliedVillageFilter() throws Exception {
        allSettings.appliedVillageFilter("All");

        verify(settingsRepository).querySetting("appliedVillageFilter", "All");
    }

}
