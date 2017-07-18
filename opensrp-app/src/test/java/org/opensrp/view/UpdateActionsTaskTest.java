package org.opensrp.view;

import android.content.Context;

import org.opensrp.domain.DownloadStatus;
import org.opensrp.domain.FetchStatus;
import org.opensrp.repository.AllSharedPreferences;
import org.opensrp.service.ActionService;
import org.opensrp.service.AllFormVersionSyncService;
import org.opensrp.service.FormSubmissionSyncService;
import org.opensrp.sync.AfterFetchListener;
import org.opensrp.sync.UpdateActionsTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.opensrp.domain.FetchStatus.fetched;
import static org.opensrp.domain.FetchStatus.nothingFetched;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class UpdateActionsTaskTest {
    @Mock
    private ActionService actionService;
    @Mock
    private ProgressIndicator progressIndicator;
    @Mock
    private Context androidContext;
    @Mock
    private org.opensrp.Context context;
    @Mock
    private FormSubmissionSyncService formSubmissionSyncService;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AllFormVersionSyncService allFormVersionSyncService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldShowProgressBarsWhileFetchingAlerts() throws Exception {
        progressIndicator = mock(ProgressIndicator.class);
        org.opensrp.Context.setInstance(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");
        when(actionService.fetchNewActions()).thenReturn(fetched);
        when(formSubmissionSyncService.sync()).thenReturn(fetched);

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(null, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
                assertEquals(fetched, status);
            }
        });

        InOrder inOrder = inOrder(actionService, progressIndicator);
        inOrder.verify(progressIndicator).setVisible();
        inOrder.verify(actionService).fetchNewActions();
        inOrder.verify(progressIndicator).setInvisible();
    }

    @Test
    public void shouldNotUpdateDisplayIfNothingWasFetched() throws Exception {
        org.opensrp.Context.setInstance(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");
        when(actionService.fetchNewActions()).thenReturn(nothingFetched);
        when(formSubmissionSyncService.sync()).thenReturn(nothingFetched);
        when(allFormVersionSyncService.pullFormDefinitionFromServer()).thenReturn(nothingFetched);
        when(allFormVersionSyncService.downloadAllPendingFormFromServer()).thenReturn(DownloadStatus.nothingDownloaded);

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(null, actionService, formSubmissionSyncService, progressIndicator,allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
                assertEquals(nothingFetched, status);
            }
        });
    }

    @Test
    public void shouldNotUpdateWhenUserIsNotLoggedIn() throws Exception {
        org.opensrp.Context.setInstance(context);
        when(context.IsUserLoggedOut()).thenReturn(true);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(androidContext, actionService, formSubmissionSyncService, progressIndicator,allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
                fail("Should not have updated from server as the user is not logged in.");
            }
        });

        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldSyncFormSubmissionsWithServer() throws Exception {
        org.opensrp.Context.setInstance(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(androidContext, actionService, formSubmissionSyncService, progressIndicator,allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
            }
        });

        verify(formSubmissionSyncService).sync();
    }
}
