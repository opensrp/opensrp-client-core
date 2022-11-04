package org.smartregister.multitenant.check;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.evernote.android.job.ShadowJobManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.EasyMap;
import org.smartregister.util.NetworkUtils;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-04-2020.
 */
public class EventClientSyncedCheckTest extends BaseRobolectricUnitTest {

    private static final String TEST_URL = "http://opensrp_base_url.com/some/test/url";

    private static final String eventJson = "{\"baseEntityId\":\"69227a92-7979-490c-b149-f28669c6b760\",\"duration\":0,\"entityType\":\"product\",\"eventDate\":\"2021-01-20T00:00:00.000+0300\",\"eventType\":\"flag_problem\",\"formSubmissionId\":\"cfcdfaf1-9e78-49f0-ba68-da412830bf7d\",\"locationId\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"obs\":[{\"fieldCode\":\"flag_problem\",\"fieldDataType\":\"text\",\"fieldType\":\"formsubmissionField\",\"formSubmissionField\":\"flag_problem\",\"humanReadableValues\":[],\"keyValPairs\":{\"not_there\":\"Product is not there\"},\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"Product is not there\"]},{\"fieldCode\":\"not_there\",\"fieldDataType\":\"text\",\"fieldType\":\"formsubmissionField\",\"formSubmissionField\":\"not_there\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"never_received\"]},{\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"start\",\"fieldType\":\"concept\",\"formSubmissionField\":\"start\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2021-01-20 10:36:31\"]},{\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"end\",\"fieldType\":\"concept\",\"formSubmissionField\":\"end\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2021-01-20 10:36:36\"]},{\"fieldCode\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"deviceid\",\"fieldType\":\"concept\",\"formSubmissionField\":\"deviceid\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"358240051111110\"]},{\"fieldCode\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"subscriberid\",\"fieldType\":\"concept\",\"formSubmissionField\":\"subscriberid\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"310260000000000\"]},{\"fieldCode\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"simserial\",\"fieldType\":\"concept\",\"formSubmissionField\":\"simserial\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"89014103211118510720\"]},{\"fieldCode\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"phonenumber\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phonenumber\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"+15555215554\"]}],\"providerId\":\"demo\",\"team\":\"Commune A Team\",\"teamId\":\"abf1be43-32da-4848-9b50-630fb89ec0ef\",\"version\":1611128196841,\"clientApplicationVersion\":1,\"clientApplicationVersionName\":\"0.0.3-v2-EUSM-SNAPSHOT\",\"dateCreated\":\"2021-01-20T10:36:36.841+0300\",\"type\":\"Event\",\"details\":{\"mission\":\"SS\",\"locationName\":\"Ambatoharanana\",\"productId\":\"2\",\"locationId\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"taskIdentifier\":\"6c303b8b-e47c-45e9-8ab5-3374c8f539a3\",\"location_id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"productName\":\"Scale\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"appVersionName\":\"2.0.1-SNAPSHOT\",\"formVersion\":\"0.0.1\"}}";
    private static final String expectedSyncPayloadConstruct = "{\"events\":\"{\\\"baseEntityId\\\":\\\"69227a92-7979-490c-b149-f28669c6b760\\\",\\\"duration\\\":0,\\\"entityType\\\":\\\"product\\\",\\\"eventDate\\\":\\\"2021-01-20T00:00:00.000+0300\\\",\\\"eventType\\\":\\\"flag_problem\\\",\\\"formSubmissionId\\\":\\\"cfcdfaf1-9e78-49f0-ba68-da412830bf7d\\\",\\\"locationId\\\":\\\"b8a7998c-5df6-49eb-98e6-f0675db71848\\\",\\\"obs\\\":[{\\\"fieldCode\\\":\\\"flag_problem\\\",\\\"fieldDataType\\\":\\\"text\\\",\\\"fieldType\\\":\\\"formsubmissionField\\\",\\\"formSubmissionField\\\":\\\"flag_problem\\\",\\\"humanReadableValues\\\":[],\\\"keyValPairs\\\":{\\\"not_there\\\":\\\"Product is not there\\\"},\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"Product is not there\\\"]},{\\\"fieldCode\\\":\\\"not_there\\\",\\\"fieldDataType\\\":\\\"text\\\",\\\"fieldType\\\":\\\"formsubmissionField\\\",\\\"formSubmissionField\\\":\\\"not_there\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"never_received\\\"]},{\\\"fieldCode\\\":\\\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"fieldDataType\\\":\\\"start\\\",\\\"fieldType\\\":\\\"concept\\\",\\\"formSubmissionField\\\":\\\"start\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"2021-01-20 10:36:31\\\"]},{\\\"fieldCode\\\":\\\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"fieldDataType\\\":\\\"end\\\",\\\"fieldType\\\":\\\"concept\\\",\\\"formSubmissionField\\\":\\\"end\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"2021-01-20 10:36:36\\\"]},{\\\"fieldCode\\\":\\\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"fieldDataType\\\":\\\"deviceid\\\",\\\"fieldType\\\":\\\"concept\\\",\\\"formSubmissionField\\\":\\\"deviceid\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"358240051111110\\\"]},{\\\"fieldCode\\\":\\\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"fieldDataType\\\":\\\"subscriberid\\\",\\\"fieldType\\\":\\\"concept\\\",\\\"formSubmissionField\\\":\\\"subscriberid\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"310260000000000\\\"]},{\\\"fieldCode\\\":\\\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"fieldDataType\\\":\\\"simserial\\\",\\\"fieldType\\\":\\\"concept\\\",\\\"formSubmissionField\\\":\\\"simserial\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"89014103211118510720\\\"]},{\\\"fieldCode\\\":\\\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\",\\\"fieldDataType\\\":\\\"phonenumber\\\",\\\"fieldType\\\":\\\"concept\\\",\\\"formSubmissionField\\\":\\\"phonenumber\\\",\\\"humanReadableValues\\\":[],\\\"parentCode\\\":\\\"\\\",\\\"saveObsAsArray\\\":false,\\\"values\\\":[\\\"+15555215554\\\"]}],\\\"providerId\\\":\\\"demo\\\",\\\"team\\\":\\\"Commune A Team\\\",\\\"teamId\\\":\\\"abf1be43-32da-4848-9b50-630fb89ec0ef\\\",\\\"version\\\":1611128196841,\\\"clientApplicationVersion\\\":1,\\\"clientApplicationVersionName\\\":\\\"0.0.3-v2-EUSM-SNAPSHOT\\\",\\\"dateCreated\\\":\\\"2021-01-20T10:36:36.841+0300\\\",\\\"type\\\":\\\"Event\\\",\\\"details\\\":{\\\"mission\\\":\\\"SS\\\",\\\"locationName\\\":\\\"Ambatoharanana\\\",\\\"productId\\\":\\\"2\\\",\\\"locationId\\\":\\\"b8a7998c-5df6-49eb-98e6-f0675db71848\\\",\\\"taskIdentifier\\\":\\\"6c303b8b-e47c-45e9-8ab5-3374c8f539a3\\\",\\\"location_id\\\":\\\"b8a7998c-5df6-49eb-98e6-f0675db71848\\\",\\\"productName\\\":\\\"Scale\\\",\\\"planIdentifier\\\":\\\"335ef7a3-7f35-58aa-8263-4419464946d8\\\",\\\"appVersionName\\\":\\\"2.0.1-SNAPSHOT\\\",\\\"formVersion\\\":\\\"0.0.1\\\"}}\"}";
    private static final String expectedBaseUrlConstruct = "http://opensrp_base_url.com/some/test/url/rest/event/add";

    private EventClientSyncedCheck eventClientSyncedCheck;

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private Context opensrpContext;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private DristhiConfiguration dristhiConfiguration;

    @AfterClass
    public static void afterClass() {
        ShadowJobManager.mockJobManager = null;
    }

    @Before
    public void setUp() throws Exception {
        eventClientSyncedCheck = Mockito.spy(new EventClientSyncedCheck());
    }

    @Test
    public void isCheckOkShouldCallIsEventsClientSynced() {
        Mockito.doReturn(false).when(eventClientSyncedCheck).isEventsClientSynced(Mockito.eq(DrishtiApplication.getInstance()));
        eventClientSyncedCheck.isCheckOk(DrishtiApplication.getInstance());

        Mockito.verify(eventClientSyncedCheck).isEventsClientSynced(Mockito.eq(DrishtiApplication.getInstance()));
    }

    @Test
    public void isEventsClientSyncedShouldReturnTrueWhenUnsyncedEventCountIsZero() {
        EventClientRepository eventClientRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getEventClientRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "eventClientRepository", eventClientRepository);

        Mockito.doReturn(0).when(eventClientRepository).getUnSyncedEventsCount();

        assertTrue(eventClientSyncedCheck.isEventsClientSynced(DrishtiApplication.getInstance()));
        Mockito.verify(eventClientRepository).getUnSyncedEventsCount();
    }

    @Test
    public void isEventsClientSyncedShouldReturnFalseWhenUnsyncedEventCountIsAboveZero() {
        EventClientRepository eventClientRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getEventClientRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "eventClientRepository", eventClientRepository);

        Mockito.doReturn(1).when(eventClientRepository).getUnSyncedEventsCount();

        assertFalse(eventClientSyncedCheck.isEventsClientSynced(DrishtiApplication.getInstance()));
        Mockito.verify(eventClientRepository).getUnSyncedEventsCount();
    }

    @Test
    public void isEventsClientSyncedShouldReturnFalseWhenEventClientRepositoryIsNull() {
        Context mockedContext = Mockito.spy(DrishtiApplication.getInstance().getContext());
        DrishtiApplication mockedDrishtiApplication = Mockito.spy(DrishtiApplication.getInstance());
        Mockito.doReturn(mockedContext).when(mockedDrishtiApplication).getContext();
        Mockito.doReturn(null).when(mockedContext).getEventClientRepository();

        // Perform assertions & call method under test
        assertFalse(eventClientSyncedCheck.isEventsClientSynced(mockedDrishtiApplication));
        assertNull(mockedDrishtiApplication.getContext().getEventClientRepository());
    }

    @Test
    public void performPreResetAppOperations() throws PreResetAppOperationException {

        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class, Mockito.CALLS_REAL_METHODS); MockedStatic<NetworkUtils> networkUtilsMockedStatic = Mockito.mockStatic(NetworkUtils.class)) {

            networkUtilsMockedStatic.when(NetworkUtils::isNetworkAvailable).thenReturn(true);
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);

            Mockito.doReturn(opensrpContext).when(coreLibrary).context();
            Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(opensrpContext).applicationContext();
            Mockito.doReturn(dristhiConfiguration).when(opensrpContext).configuration();
            Mockito.doReturn(TEST_URL).when(dristhiConfiguration).dristhiBaseURL();
            Mockito.doReturn(false).when(opensrpContext).hasForeignEvents();
            Mockito.doReturn(eventClientRepository).when(opensrpContext).getEventClientRepository();
            Mockito.doNothing().when(eventClientSyncedCheck).onSyncStart();
            Mockito.doNothing().when(eventClientSyncedCheck).onSyncComplete(FetchStatus.fetchedFailed);
            Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
            Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
            Mockito.doReturn("TeamA").when(allSharedPreferences).fetchDefaultTeam(ArgumentMatchers.anyString());
            Mockito.doReturn(httpAgent).when(opensrpContext).getHttpAgent();
            Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
            Mockito.doReturn(false).when(syncConfiguration).firebasePerformanceMonitoringEnabled();
            Mockito.doReturn(false).when(syncConfiguration).disableSyncToServerIfUserIsDisabled();
            Mockito.doReturn(true).when(httpAgent).verifyAuthorization();
            Response<String> response = new Response<>(ResponseStatus.failure, "{}");
            Mockito.doReturn(response).when(httpAgent).post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
            Mockito.doReturn(EasyMap.mapOf(AllConstants.KEY.EVENTS, eventJson)).when(eventClientRepository).getUnSyncedEvents(ArgumentMatchers.anyInt());

            eventClientSyncedCheck.performPreResetAppOperations(DrishtiApplication.getInstance());

            ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> payloadArgumentCaptor = ArgumentCaptor.forClass(String.class);

            // Verify that performSync() was called
            Mockito.verify(httpAgent).post(urlArgumentCaptor.capture(), payloadArgumentCaptor.capture());

            String requestBaseUrl = urlArgumentCaptor.getValue();
            String requestPayload = payloadArgumentCaptor.getValue();

            Assert.assertNotNull(requestBaseUrl);
            Assert.assertNotNull(requestPayload);
            Assert.assertEquals(expectedBaseUrlConstruct, requestBaseUrl);
            Assert.assertEquals(expectedSyncPayloadConstruct, requestPayload);
        }
    }
}