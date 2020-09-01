package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseErrorStatus;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.SyncUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Richard Kareko on 7/21/20.
 */

public class SyncIntentServiceTest extends BaseRobolectricUnitTest {

    @Mock
    private SyncUtils syncUtils;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Captor
    private ArgumentCaptor<Intent>  intentArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Mock
    private HTTPAgent httpAgent;

    private Context context = RuntimeEnvironment.application;

    private SyncIntentService syncIntentService;

    private String eventSyncPayload = "{\n" +
            "  \"events\": [\n" +
            "    {\n" +
            "      \"type\": \"Event\",\n" +
            "      \"dateCreated\": \"2020-08-13T13:24:43.048+02:00\",\n" +
            "      \"serverVersion\": 1597318034276,\n" +
            "      \"clientApplicationVersion\": 21,\n" +
            "      \"clientDatabaseVersion\": 8,\n" +
            "      \"identifiers\": {},\n" +
            "      \"baseEntityId\": \"eb2eab25-9744-44e4-bd84-c69079156ce2\",\n" +
            "      \"locationId\": \"e3a1eac5-61e3-4e6d-b8b2-cbe9417a6ebd\",\n" +
            "      \"eventDate\": \"2020-08-13T06:00:00.000+02:00\",\n" +
            "      \"eventType\": \"Register_Structure\",\n" +
            "      \"formSubmissionId\": \"d9d621b7-3cf1-437b-ae48-6f1a229cec7e\",\n" +
            "      \"providerId\": \"namibia1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"Event\",\n" +
            "      \"dateCreated\": \"2020-08-13T13:24:43.090+02:00\",\n" +
            "      \"serverVersion\": 1597318034277,\n" +
            "      \"clientApplicationVersion\": 21,\n" +
            "      \"clientDatabaseVersion\": 8,\n" +
            "      \"identifiers\": {},\n" +
            "      \"baseEntityId\": \"eb2eab25-9744-44e4-bd84-c69079156ce2\",\n" +
            "      \"locationId\": \"e3a1eac5-61e3-4e6d-b8b2-cbe9417a6ebd\",\n" +
            "      \"eventDate\": \"2020-08-13T06:00:00.000+02:00\",\n" +
            "      \"eventType\": \"Spray\",\n" +
            "      \"formSubmissionId\": \"e63db3c8-7bbf-4186-ad43-f03b1e847e15\",\n" +
            "      \"providerId\": \"namibia1\"\n" +
            "    }\n" +
            "    ],\n" +
            "  \"clients\": [],\n" +
            "  \"no_of_events\": 2,\n" +
            "  \"total_records\": 2\n" +
            "}";


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "https://sample-stage.smartregister.org/opensrp");
        syncIntentService = new SyncIntentService();
        syncIntentService.init(context);
        Whitebox.setInternalState(syncIntentService, "mBase", RuntimeEnvironment.application);
    }

    @Test
    public void testInit() {
        assertNotNull(Whitebox.getInternalState(syncIntentService, "httpAgent"));
        assertNotNull(Whitebox.getInternalState(syncIntentService, "syncUtils"));
        assertNotNull(Whitebox.getInternalState(syncIntentService, "context"));
    }

    @Test
    public void testHandleSyncSendFetchStartedBroadCast() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);
        Whitebox.setInternalState(syncIntentService, "syncUtils", syncUtils);
        when(syncUtils.verifyAuthorization()).thenReturn(true);
        when(syncUtils.isAppVersionAllowed()).thenReturn(true);

        syncIntentService.handleSync();
        verify(syncIntentService, times(2)).sendBroadcast(intentArgumentCaptor.capture());
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getAllValues().get(0).getAction());
        assertEquals(FetchStatus.fetchStarted, intentArgumentCaptor.getAllValues().get(0).getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));

    }

    @Test
    public void testHandleSyncCallsLogoutUserIfHasValidAuthorizationIsFalse() {
        Whitebox.setInternalState(syncIntentService, "syncUtils", syncUtils);
        when(syncUtils.verifyAuthorization()).thenReturn(false);
        when(syncConfiguration.disableSyncToServerIfUserIsDisabled()).thenReturn(true);
        syncIntentService.handleSync();
        verify(syncUtils).logoutUser();
    }

    @Test
    public void testHandleSyncCallsLogOutUserIfAppVersionIsNotAllowedAnd() {
        syncIntentService = spy(syncIntentService);
        Whitebox.setInternalState(syncIntentService, "syncUtils", syncUtils);
        when(syncUtils.verifyAuthorization()).thenReturn(true);
        when(syncConfiguration.disableSyncToServerIfUserIsDisabled()).thenReturn(true);
        syncIntentService.handleSync();
        verify(syncUtils).logoutUser();
    }

    @Test
    public void testHandleSyncCallsPullECFromServerIfHasValidAuthorizationAndIsAppVersionAllowed() throws PackageManager.NameNotFoundException {
        initMocksForPullECFromServer();
        syncIntentService.handleSync();
        verify(syncIntentService).pullECFromServer();
    }

    @Test
    public void testPullEcFromServerWhenSyncFilterParamIsNull() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);
        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());

        // sync fetch failed broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        assertEquals(FetchStatus.fetchedFailed, intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));

    }

    @Test
    public void testPullEcFromServerWhenSyncFilterValueIsNull() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);
        when(syncConfiguration.getSyncFilterParam()).thenReturn(SyncFilter.LOCATION);
        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());

        // sync fetch failed broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        assertEquals(FetchStatus.fetchedFailed, intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));

    }

    @Test
    public void testPullEcFromServerWhenHttpAgentIsNull() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);

        Whitebox.setInternalState(syncIntentService, "httpAgent", (Object[]) null);
        assertNull(Whitebox.getInternalState(syncIntentService, "httpAgent"));

        when(syncConfiguration.getSyncFilterParam()).thenReturn(SyncFilter.LOCATION);
        when(syncConfiguration.getSyncFilterValue()).thenReturn("location-1");
        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());

        // sync fetch failed broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        assertEquals(FetchStatus.fetchedFailed, intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));

    }

    @Test
    public void testPullEcFromServerUsingPOSTUsesCorrectURLAndParams() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);

        initMocksForPullECFromServerUsingPOST();
        ResponseStatus responseStatus = ResponseStatus.failure;
        responseStatus.setDisplayValue(ResponseErrorStatus.malformed_url.name());
        Mockito.doReturn(new Response<>(responseStatus, null))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp/rest/event/sync", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("{\"locationId\":\"location-1\",\"serverVersion\":0,\"limit\":250,\"return_count\":true}", requestString);

    }

    @Test
    public void testPullEcFromServerWithURLError() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);

        initMocksForPullECFromServerUsingPOST();
        ResponseStatus responseStatus = ResponseStatus.failure;
        responseStatus.setDisplayValue(ResponseErrorStatus.malformed_url.name());
        Mockito.doReturn(new Response<>(responseStatus, null))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());

        // sync fetch failed broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        FetchStatus actualFetchStatus = (FetchStatus) intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS);
        assertEquals(FetchStatus.fetchedFailed, actualFetchStatus);
        assertEquals("malformed_url", actualFetchStatus.displayValue());

    }

    @Test
    public void testPullEcFromServerWithTimeoutError() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);

        initMocksForPullECFromServerUsingPOST();
        ResponseStatus responseStatus = ResponseStatus.failure;
        responseStatus.setDisplayValue(ResponseErrorStatus.timeout.name());
        Mockito.doReturn(new Response<>(responseStatus, null))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());

        // sync fetch failed broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        FetchStatus actualFetchStatus = (FetchStatus) intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS);
        assertEquals(FetchStatus.fetchedFailed, actualFetchStatus);
        assertEquals("timeout", actualFetchStatus.displayValue());

    }

    @Test
    public void testPullEcFromServerWithFetchFailureCallsFetchFailed() {

        initMocksForPullECFromServerUsingPOST();
        syncIntentService = spy(syncIntentService);
        ResponseStatus responseStatus = ResponseStatus.failure;
        responseStatus.setDisplayValue(null);
        Mockito.doReturn(new Response<>(responseStatus, null))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        syncIntentService.pullECFromServer();
        verify(syncIntentService).fetchFailed(0);

    }

    @Test
    public void testFetchFailedWithCountLessThanMaxSyncRetryCallsFetchRetry() {
        when(syncConfiguration.getSyncMaxRetries()).thenReturn(1);
        initMocksForPullECFromServerUsingPOST();
        syncIntentService = spy(syncIntentService);
        ResponseStatus responseStatus = ResponseStatus.failure;
        Mockito.doReturn(new Response<>(responseStatus, null))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());
        syncIntentService.fetchFailed(0);
        verify(syncIntentService).fetchFailed(1);

    }

    @Test
    public void testFetchFailedWithCountEqualToMaxSyncRetryCallsComplete() {
        syncIntentService = spy(syncIntentService);
        when(syncConfiguration.getSyncMaxRetries()).thenReturn(1);
        syncIntentService.fetchFailed(1);
        verify(syncIntentService).complete(FetchStatus.fetchedFailed);

    }

    @Test
    public void testFetchFailedWithCountGreaterThanMaxSyncRetryCallsComplete() {
        syncIntentService = spy(syncIntentService);
        when(syncConfiguration.getSyncMaxRetries()).thenReturn(1);
        syncIntentService.fetchFailed(2);
        verify(syncIntentService).complete(FetchStatus.fetchedFailed);

    }

    @Test
    public void testSuccessfulPullEcFromServerWithNullPayloadSendsNothingFetchedBroadcast() {

        initMocksForPullECFromServerUsingPOST();
        syncIntentService = spy(syncIntentService);
        ResponseStatus responseStatus = ResponseStatus.success;
        Mockito.doReturn(new Response<>(responseStatus, null).withTotalRecords(0l))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        syncIntentService.pullECFromServer();

        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());
        // sync complete broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        FetchStatus actualFetchStatus = (FetchStatus) intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS);
        assertEquals(FetchStatus.nothingFetched, actualFetchStatus);

    }

    @Test
    public void testSuccessfulPullEcSendsSyncProgressAndSyncStatusBroadcasts() {

        initMocksForPullECFromServerUsingPOST();
        syncIntentService = spy(syncIntentService);
        ResponseStatus responseStatus = ResponseStatus.success;
        Mockito.doReturn(new Response<>(responseStatus, eventSyncPayload).withTotalRecords(2l),
                new Response<>(responseStatus, null).withTotalRecords(0l))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        syncIntentService.pullECFromServer();
        verify(syncIntentService).sendSyncProgressBroadcast(2);
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());
        // sync complete broadcast sent
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        FetchStatus actualFetchStatus = (FetchStatus) intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS);
        assertEquals(FetchStatus.nothingFetched, actualFetchStatus);

    }

    private void initMocksForPullECFromServerUsingPOST() {
        Whitebox.setInternalState(syncIntentService, "httpAgent", httpAgent);
        when(syncConfiguration.isSyncUsingPost()).thenReturn(true);
        when(syncConfiguration.getSyncFilterParam()).thenReturn(SyncFilter.LOCATION);
        when(syncConfiguration.getSyncFilterValue()).thenReturn("location-1");
    }

    private void initMocksForPullECFromServer() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);
        Whitebox.setInternalState(syncIntentService, "syncUtils", syncUtils);
        when(syncUtils.verifyAuthorization()).thenReturn(true);
        when(syncUtils.isAppVersionAllowed()).thenReturn(true);
        when(syncConfiguration.disableSyncToServerIfUserIsDisabled()).thenReturn(true);
    }

}
