package org.smartregister.repository;

import android.content.SharedPreferences;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.AllConstants;
import org.smartregister.sync.mock.MockEditor;

import static org.smartregister.AllConstants.CAMPAIGNS;

@RunWith(RobolectricTestRunner.class)
public class AllSharedPreferencesTest extends TestCase {
    @Mock
    private SharedPreferences preferences;
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    AllSharedPreferences allSharedPreferences;
    private final String str = "default";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allSharedPreferences = new AllSharedPreferences(preferences);
        Mockito.when(preferences.getString(Mockito.anyString(), Mockito.isNull(String.class))).thenReturn(str);
        Mockito.when(preferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(str);
        Mockito.when(preferences.getLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(0l);
        Mockito.when(preferences.getString(HOST, "")).thenReturn("");
        Mockito.when(preferences.getString(PORT, "80")).thenReturn("8080");
        Mockito.when(preferences.getString(AllConstants.DRISHTI_BASE_URL, "")).thenReturn("http://www.google.com");
        Mockito.when(preferences.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);
        Mockito.when(preferences.edit()).thenReturn(MockEditor.getEditor());
    }

    @Test
    public void assertupdateANMUserNameCallsPreferenceEdit() {
        allSharedPreferences.updateANMUserName("");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertFetchForceRemoteLogin() {
        Assert.assertEquals(allSharedPreferences.fetchForceRemoteLogin(), true);
    }

    @Test
    public void assertFetchServerTimeZone() {
        Assert.assertEquals(str, allSharedPreferences.fetchServerTimeZone());
    }

    @Test
    public void assertSaveServerTimeZone() {
        allSharedPreferences.saveServerTimeZone("");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertFetchEncryptedPassword() {
        Assert.assertNull(allSharedPreferences.fetchEncryptedPassword(null));
        Assert.assertNotNull(allSharedPreferences.fetchEncryptedPassword(""));
        Assert.assertEquals(allSharedPreferences.fetchEncryptedPassword(""), str);
    }

    @Test
    public void assertSaveEncryptedPassword() {
        allSharedPreferences.saveEncryptedPassword("uname", "pword");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertFetchPioneerUser() {
        Assert.assertEquals(allSharedPreferences.fetchPioneerUser(), str);
    }

    @Test
    public void assertfetchLastUpdatedAtDate() {
        Assert.assertEquals(allSharedPreferences.fetchLastUpdatedAtDate(0l), new Long(0l));
    }

    @Test
    public void assertsaveLastUpdatedAtDate() {
        allSharedPreferences.saveLastUpdatedAtDate(0l);
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertfetchCurrentLocality() {
        Assert.assertEquals(allSharedPreferences.fetchCurrentLocality(), str);
    }

    @Test
    public void assertsaveCurrentLocality() {
        allSharedPreferences.saveCurrentLocality("LoCaLiTy");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertfetchEncryptedGroupId() {
        Assert.assertNull(allSharedPreferences.fetchEncryptedGroupId(null));
        Assert.assertEquals(allSharedPreferences.fetchEncryptedGroupId("uname"), str);
    }

    @Test
    public void assertsaveEncryptedGroupId() {
        allSharedPreferences.saveEncryptedGroupId("uname", "Id");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertsaveLanguagePreference() {
        allSharedPreferences.saveLanguagePreference("EN");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertfetchBaseURL() {
        Assert.assertEquals(allSharedPreferences.fetchBaseURL(""), "http://www.google.com");
    }

    @Test
    public void assertsavePreference() {
        allSharedPreferences.savePreference("", "");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertgetPreference() {
        Assert.assertEquals(allSharedPreferences.getPreference(""), str);
    }

    @Test
    public void assertupdateANMPreferredName() {
        allSharedPreferences.updateANMPreferredName("", "");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertgetANMPreferredName() {
        org.junit.Assert.assertEquals(allSharedPreferences.getANMPreferredName("uname"), str);
    }


    @Test
    public void assertfetchHost() {
        Assert.assertEquals(allSharedPreferences.fetchHost(""), "");
        Assert.assertEquals(allSharedPreferences.fetchHost(null), str);
        Mockito.when(preferences.getString(AllConstants.DRISHTI_BASE_URL, "")).thenReturn("Malformed Url");
        Assert.assertEquals(allSharedPreferences.fetchHost(""), "");
    }

    @Test
    public void assertfetchPort() {
        Assert.assertEquals(allSharedPreferences.fetchPort(new Integer(80)), new Integer(8080));
    }

    @Test
    public void assertSavePioneerUser() {
        allSharedPreferences.savePioneerUser("uname");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertSaveDefaultLocalityId() {
        allSharedPreferences.saveDefaultLocalityId("uname", "Id");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void assertFetchDefaultLocalityId() {
        org.junit.Assert.assertNull(allSharedPreferences.fetchDefaultLocalityId(null));
        org.junit.Assert.assertEquals(allSharedPreferences.fetchDefaultLocalityId(""), str);
    }

    @Test
    public void assertSaveForceRemoteLogin() {
        allSharedPreferences.saveForceRemoteLogin(true);
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void shouldFetchANMIdentifierFromPreferences() throws Exception {
        Mockito.when(preferences.getString("anmIdentifier", "")).thenReturn("1234");

        String actual = allSharedPreferences.fetchRegisteredANM();

        Mockito.verify(preferences).getString("anmIdentifier", "");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldTrimANMIdentifier() throws Exception {
        Mockito.when(preferences.getString("anmIdentifier", "")).thenReturn("  1234 ");

        String actual = allSharedPreferences.fetchRegisteredANM();

        Mockito.verify(preferences).getString("anmIdentifier", "");
        assertEquals("1234", actual);
    }

    @Test
    public void shouldFetchLanguagePreference() throws Exception {
        Mockito.when(preferences.getString(AllConstants.LANGUAGE_PREFERENCE_KEY, AllConstants.DEFAULT_LOCALE)).thenReturn(AllConstants.ENGLISH_LANGUAGE);

        assertEquals("English", allSharedPreferences.fetchLanguagePreference());
    }

    @Test
    public void shouldFetchIsSyncInProgress() throws Exception {
        Mockito.when(preferences.getBoolean(AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, false)).thenReturn(true);

        assertTrue(allSharedPreferences.fetchIsSyncInProgress());
    }

    @Test
    public void shouldSaveIsSyncInProgress() throws Exception {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putBoolean(AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, true)).thenReturn(editor);

        allSharedPreferences.saveIsSyncInProgress(true);

        Mockito.verify(editor).putBoolean(AllConstants.IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, true);
        Mockito.verify(editor).commit();
    }

    @Test
    public void shouldSaveCampaignsOperationalArea() {
        allSharedPreferences.savePreference(CAMPAIGNS,"Miti Rural Health Centre");
        Mockito.verify(preferences, Mockito.times(1)).edit();
    }

    @Test
    public void shouldGetCampaignsOperationalArea() {
        Assert.assertEquals(allSharedPreferences.getPreference(CAMPAIGNS), str);
    }

}
