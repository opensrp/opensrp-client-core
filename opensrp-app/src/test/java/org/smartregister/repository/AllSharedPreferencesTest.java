package org.smartregister.repository;

import android.content.SharedPreferences;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.smartregister.AllConstants.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AllSharedPreferencesTest extends TestCase {
    @Mock
    private SharedPreferences preferences;

    AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allSharedPreferences = new AllSharedPreferences(preferences);
    }

    @Test
    public void shouldFetchANMIdentifierFromPreferences() throws Exception {
        when(preferences.getString("anmIdentifier", "")).thenReturn("1234");

        String actual = allSharedPreferences.fetchRegisteredANM();

        verify(preferences).getString("anmIdentifier", "");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldTrimANMIdentifier() throws Exception {
        when(preferences.getString("anmIdentifier", "")).thenReturn("  1234 ");

        String actual = allSharedPreferences.fetchRegisteredANM();

        verify(preferences).getString("anmIdentifier", "");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldFetchLanguagePreference() throws Exception {
        when(preferences.getString(LANGUAGE_PREFERENCE_KEY, DEFAULT_LOCALE)).thenReturn(ENGLISH_LANGUAGE);

        assertEquals("English", allSharedPreferences.fetchLanguagePreference());
    }

    @Test
    public void shouldFetchIsSyncInProgress() throws Exception {
        when(preferences.getBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, false)).thenReturn(true);

        assertTrue(allSharedPreferences.fetchIsSyncInProgress());
    }

    @Test
    public void shouldSaveIsSyncInProgress() throws Exception {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(preferences.edit()).thenReturn(editor);
        when(editor.putBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, true)).thenReturn(editor);

        allSharedPreferences.saveIsSyncInProgress(true);

        verify(editor).putBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, true);
        verify(editor).commit();
    }

}
