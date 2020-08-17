package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.SyncUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    private Context context = RuntimeEnvironment.application;

    private SyncIntentService syncIntentService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
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

    private void initMocksForPullECFromServer() throws PackageManager.NameNotFoundException {
        syncIntentService = spy(syncIntentService);
        Whitebox.setInternalState(syncIntentService, "syncUtils", syncUtils);
        when(syncUtils.verifyAuthorization()).thenReturn(true);
        when(syncUtils.isAppVersionAllowed()).thenReturn(true);
        when(syncConfiguration.disableSyncToServerIfUserIsDisabled()).thenReturn(true);
    }

}
