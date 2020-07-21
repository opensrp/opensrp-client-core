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
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.SyncUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Richard Kareko on 7/21/20.
 */

public class SyncIntentServiceTest extends BaseRobolectricUnitTest {

    @Mock
    private SyncUtils syncUtils;

    @Captor
    private ArgumentCaptor<Intent>  intentArgumentCaptor;

    private Context context = RuntimeEnvironment.application;

    private SyncIntentService syncIntentService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        when(syncUtils.isAppVersionAllowed()).thenReturn(false);

        syncIntentService.handleSync();
        verify(syncIntentService).sendBroadcast(intentArgumentCaptor.capture());
        assertEquals(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, intentArgumentCaptor.getValue().getAction());
        assertEquals(FetchStatus.fetchStarted, intentArgumentCaptor.getValue().getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));

    }

}
