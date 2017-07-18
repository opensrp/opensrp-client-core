package org.ei.opensrp.service;

import org.ei.opensrp.repository.*;
import org.ei.opensrp.sync.SaveUserInfoTask;
import org.json.JSONObject;
import org.mockito.Spy;
import org.opensrp.api.domain.Location;
import org.opensrp.api.domain.User;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.robolectric.RobolectricTestRunner;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.sync.SaveANMLocationTask;
import org.ei.opensrp.util.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Map;

import static org.ei.opensrp.AllConstants.ENGLISH_LOCALE;
import static org.ei.opensrp.AllConstants.KANNADA_LOCALE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class UserServiceTest {
    @Mock
    private Repository repository;
    @Mock
    private AllSettings allSettings;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AllAlerts allAlerts;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private Session session;
    @Mock
    private HTTPAgent httpAgent;
    @Mock
    private DristhiConfiguration configuration;
    @Mock
    private SaveANMLocationTask saveANMLocationTask;
    @Mock
    private SaveUserInfoTask saveUserInfoTask;

    private UserService userService;

    private String userInfoJSON;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        userService = new UserService(repository, allSettings, allSharedPreferences, httpAgent, session, configuration, saveANMLocationTask, saveUserInfoTask);
        userInfoJSON = "{\"locations\":{\"locationsHierarchy\":{\"map\":{\"cd4ed528-87cd-42ee-a175-5e7089521ebd\":{\"id\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\",\"label\":\"Pakistan\",\"node\":{\"locationId\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\",\"name\":\"Pakistan\",\"tags\":[\"Country\"],\"voided\":false},\"children\":{\"461f2be7-c95d-433c-b1d7-c68f272409d7\":{\"id\":\"461f2be7-c95d-433c-b1d7-c68f272409d7\",\"label\":\"Sindh\",\"node\":{\"locationId\":\"461f2be7-c95d-433c-b1d7-c68f272409d7\",\"name\":\"Sindh\",\"parentLocation\":{\"locationId\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\",\"name\":\"Pakistan\",\"voided\":false},\"tags\":[\"Province\"],\"voided\":false},\"children\":{\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\":{\"id\":\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\",\"label\":\"Karachi\",\"node\":{\"locationId\":\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\",\"name\":\"Karachi\",\"parentLocation\":{\"locationId\":\"461f2be7-c95d-433c-b1d7-c68f272409d7\",\"name\":\"Sindh\",\"parentLocation\":{\"locationId\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\",\"name\":\"Pakistan\",\"voided\":false},\"voided\":false},\"tags\":[\"City\"],\"voided\":false},\"children\":{\"60c21502-fec1-40f5-b77d-6df3f92771ce\":{\"id\":\"60c21502-fec1-40f5-b77d-6df3f92771ce\",\"label\":\"Baldia\",\"node\":{\"locationId\":\"60c21502-fec1-40f5-b77d-6df3f92771ce\",\"name\":\"Baldia\",\"parentLocation\":{\"locationId\":\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\",\"name\":\"Karachi\",\"parentLocation\":{\"locationId\":\"461f2be7-c95d-433c-b1d7-c68f272409d7\",\"name\":\"Sindh\",\"voided\":false},\"voided\":false},\"tags\":[\"Town\"],\"attributes\":{\"at1\":\"atttt1\"},\"voided\":false},\"parent\":\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\"}},\"parent\":\"461f2be7-c95d-433c-b1d7-c68f272409d7\"}},\"parent\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\"}}}},\"parentChildren\":{\"cd4ed528-87cd-42ee-a175-5e7089521ebd\":[\"461f2be7-c95d-433c-b1d7-c68f272409d7\"],\"461f2be7-c95d-433c-b1d7-c68f272409d7\":[\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\"],\"a529e2fc-6f0d-4e60-a5df-789fe17cca48\":[\"60c21502-fec1-40f5-b77d-6df3f92771ce\"]}}},\"user\":{\"username\":\"demotest\",\"roles\":[\"Provider\",\"Authenticated\",\"Thrive Member\"],\"permissions\":[\"Delete Reports\",\"Remove Allergies\",\"Edit Cohorts\",\"View Unpublished Forms\",\"Patient Dashboard - View Patient Summary\",\"Add Relationships\",\"Edit Problems\",\"Remove Problems\",\"Patient Dashboard - View Forms Section\",\"Manage Report Designs\",\"Add Patient Programs\",\"Delete Orders\",\"Manage Identifier Types\",\"Manage Person Attribute Types\",\"Add Patient Identifiers\",\"View Visit Types\",\"View Patients\",\"Delete Concept Proposals\",\"View Identifier Types\",\"Delete Encounters\",\"View Global Properties\",\"Edit Visits\",\"View Concept Map Types\",\"Add Users\",\"Delete Cohorts\",\"Manage Scheduled Report Tasks\",\"View Concepts\",\"Edit Concept Proposals\",\"Add Visits\",\"Edit Patient Programs\",\"Manage Concept Datatypes\",\"Manage Indicator Definitions\",\"View Concept Proposals\",\"Add Allergies\",\"Edit Allergies\",\"Delete Observations\",\"View Roles\",\"Manage Address Templates\",\"Configure Visits\",\"Manage Data Set Definitions\",\"View Concept Sources\",\"Patient Dashboard - View Regimen Section\",\"View Calculations\",\"Manage Encounter Roles\",\"Delete People\",\"Edit Report Objects\",\"View People\",\"Manage Concept Sources\",\"View Orders\",\"Manage Concept Map Types\",\"Delete Patient Programs\",\"Add Problems\",\"Edit People\",\"Manage Locations\",\"View Patient Programs\",\"View Field Types\",\"View Relationship Types\",\"Manage Visit Attribute Types\",\"Manage Order Types\",\"Manage Location Attribute Types\",\"Form Entry\",\"View Encounter Types\",\"View Encounter Roles\",\"Manage Programs\",\"Edit Reports\",\"View Location Attribute Types\",\"View Order Types\",\"Manage Relationship Types\",\"Manage Providers\",\"Manage Reports\",\"Manage Concept Classes\",\"Add Concept Proposals\",\"View Patient Identifiers\",\"View Privileges\",\"View Data Entry Statistics\",\"Patient Dashboard - View Graphs Section\",\"Manage Tokens\",\"Add Reports\",\"View Forms\",\"View Administration Functions\",\"Manage Relationships\",\"View Observations\",\"View Team\",\"Add Observations\",\"View Member\",\"View Report Objects\",\"Edit Relationships\",\"View Relationships\",\"Manage Scheduler\",\"View Allergies\",\"View Concept Reference Terms\",\"View Encounters\",\"Edit Patient Identifiers\",\"Edit Observations\",\"Delete Patients\",\"Delete Patient Identifiers\",\"View Person Attribute Types\",\"Add Encounters\",\"View Problems\",\"Manage FormEntry XSN\",\"View Visits\",\"Edit Team\",\"Manage Field Types\",\"Patient Dashboard - View Encounters Section\",\"Add Team\",\"Add Cohorts\",\"Add Patients\",\"Patient Dashboard - View Demographics Section\",\"Manage Concepts\",\"View Rule Definitions\",\"Add Orders\",\"Manage Visit Types\",\"Patient Dashboard - View Visits Section\",\"View Locations\",\"Manage Forms\",\"Edit Encounters\",\"Delete Relationships\",\"Manage Concept Reference Terms\",\"Add Report Objects\",\"Manage Alerts\",\"View Users\",\"Edit Patients\",\"Manage Concept Stop Words\",\"View Concept Classes\",\"View Patient Cohorts\",\"View Visit Attribute Types\",\"Manage Location Tags\",\"Manage Encounter Types\",\"View Concept Datatypes\",\"View Navigation Menu\",\"Delete Visits\",\"Add People\",\"Edit Orders\",\"Manage Concept Name tags\",\"Run Reports\",\"View Providers\",\"Patient Dashboard - View Overview Section\",\"Manage Cohort Definitions\",\"View Reports\",\"View Programs\",\"Delete Report Objects\",\"Manage Report Definitions\"],\"baseEntityId\":\"6637559e-ebf9-480a-9731-c47e16e95716\",\"baseEntity\":{\"id\":\"6637559e-ebf9-480a-9731-c47e16e95716\",\"firstName\":\"Demo test User\",\"middleName\":\"\",\"lastName\":\"\",\"gender\":\"M\",\"attributes\":{\"Location\":\"cd4ed528-87cd-42ee-a175-5e7089521ebd\"},\"voided\":false},\"voided\":false}}";

    }

    @Test
    public void shouldUseHttpAgentToDoRemoteLoginCheck() {
        when(configuration.dristhiBaseURL()).thenReturn("http://dristhi_base_url");

        userService.isValidRemoteLogin("userX", "password Y");

        verify(httpAgent).urlCanBeAccessWithGivenCredentials("http://dristhi_base_url/security/authenticate", "userX", "password Y");
    }

    @Test
    public void shouldGetANMLocation() {
        when(configuration.dristhiBaseURL()).thenReturn("http://opensrp_base_url");
        userService.getLocationInformation();
        verify(httpAgent).fetch("http://opensrp_base_url/location/location-tree");
    }

    @Test
    public void shouldSaveUserInformationRemoteLoginIsSuccessful() {
        userService.saveUserInfo("user info");
        verify(saveUserInfoTask).save("user info");
    }

    @Test
    public void shouldSaveANMLocation() {
        userService.saveAnmLocation("anm location");
        verify(saveANMLocationTask).save("anm location");
    }

    @Test
    public void shouldConsiderALocalLoginValidWhenUsernameMatchesRegisteredUserAndPasswordMatchesTheOneInDB() {
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("ANM X");
        when(repository.canUseThisPassword("password Z")).thenReturn(true);

        assertTrue(userService.isValidLocalLogin("ANM X", "password Z"));

        verify(allSharedPreferences).fetchRegisteredANM();
        verify(repository).canUseThisPassword("password Z");
    }

    @Test
    public void shouldConsiderALocalLoginInvalidWhenRegisteredUserDoesNotMatch() {
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("ANM X");

        assertFalse(userService.isValidLocalLogin("SOME OTHER ANM", "password"));

        verify(allSharedPreferences).fetchRegisteredANM();
        verifyZeroInteractions(repository);
    }

    @Test
    public void shouldConsiderALocalLoginInvalidWhenRegisteredUserMatchesButNotThePassword() {
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("ANM X");
        when(repository.canUseThisPassword("password Z")).thenReturn(false);

        assertFalse(userService.isValidLocalLogin("ANM X", "password Z"));

        verify(allSharedPreferences).fetchRegisteredANM();
        verify(repository).canUseThisPassword("password Z");
    }

    @Test
    public void shouldRegisterANewUser() {
        userService.remoteLogin("user X", "password Y", "");

        verify(allSettings).registerANM("user X", "password Y");
        verify(session).setPassword("password Y");
    }

    @Test
    public void shouldDeleteDataAndSettingsWhenLogoutHappens() throws Exception {
        userService.logout();

        verify(repository).deleteRepository();
        verify(allSettings).savePreviousFetchIndex("0");
        verify(allSettings).registerANM("", "");
    }

    @Test
    public void shouldSwitchLanguageToKannada() throws Exception {
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn(ENGLISH_LOCALE);

        userService.switchLanguagePreference();

        verify(allSharedPreferences).saveLanguagePreference(KANNADA_LOCALE);
    }

    @Test
    public void shouldSwitchLanguageToEnglish() throws Exception {
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn(KANNADA_LOCALE);

        userService.switchLanguagePreference();

        verify(allSharedPreferences).saveLanguagePreference(ENGLISH_LOCALE);
    }

    @Test
    public void shouldGetUserDataFromJSON() throws Exception {

        String jsonString = userService.getUserData(userInfoJSON);
        assertNotNull(jsonString);

        User userObj = EntityUtils.fromJson(jsonString, User.class);

        assertEquals("demotest", userObj.getUsername());
        assertEquals("Demo test User", userObj.getBaseEntity().getFirstName());
    }

    @Test
    public void shouldGetUserLocationFromJSON() throws Exception {

        String jsonString = userService.getUserLocation(userInfoJSON);
        assertNotNull(jsonString);

        LocationTree locationTree = EntityUtils.fromJson(jsonString, LocationTree.class);

        Map<String, TreeNode<String, Location>> mapLocation = locationTree.getLocationsHierarchy();

        assertEquals("Pakistan", mapLocation.values().iterator().next().getLabel());
    }

}
