package org.smartregister.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.nothingFetched;

import android.content.Context;
import android.os.Handler;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.login.interactor.TestExecutorService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.service.FormSubmissionSyncService;
import org.smartregister.sync.UpdateActionsTask;

import java.util.concurrent.Executors;

public class UpdateActionsTaskTest extends BaseUnitTest {
    @Mock
    private ActionService actionService;
    @Mock
    private ProgressIndicator progressIndicator;
    @Mock
    private Context androidContext;
    @Mock
    private org.smartregister.Context context;
    @Mock
    private FormSubmissionSyncService formSubmissionSyncService;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AllFormVersionSyncService allFormVersionSyncService;
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private DristhiConfiguration configuration;
    @Mock
    private SyncConfiguration syncConfiguration;

    @Test
    public void shouldShowProgressBarsWhileFetchingAlerts() {

        try (MockedStatic<Executors> executor = Mockito.mockStatic(Executors.class); MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class);
             MockedConstruction<Handler> handlerMockedConstruction = Mockito.mockConstruction(Handler.class,
                     (mock, context) -> when(mock.post(ArgumentMatchers.any(Runnable.class))).thenAnswer(it -> {
                         ((Runnable) it.getArgument(0)).run();
                         return true;
                     })
             )) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            executor.when(Executors::newSingleThreadExecutor).thenReturn(new TestExecutorService());
            assertNotNull(handlerMockedConstruction);

            progressIndicator = mock(ProgressIndicator.class);
            when(coreLibrary.context()).thenReturn(context);
            when(context.IsUserLoggedOut()).thenReturn(false);
            when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
            when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");
            when(actionService.fetchNewActions()).thenReturn(fetched);
            when(formSubmissionSyncService.sync()).thenReturn(fetched);
            when(context.configuration()).thenReturn(configuration);
            when(coreLibrary.getSyncConfiguration()).thenReturn(syncConfiguration);
            when(actionService.fetchNewActions()).thenReturn(fetched);
            when(syncConfiguration.disableActionService()).thenReturn(false);

            UpdateActionsTask updateActionsTask = new UpdateActionsTask(null, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
            assertNotNull(updateActionsTask);
            updateActionsTask.updateFromServer(status -> assertEquals(fetched, status));

            InOrder inOrder = inOrder(actionService, progressIndicator);
            inOrder.verify(progressIndicator).setVisible();
            inOrder.verify(actionService).fetchNewActions();
            inOrder.verify(progressIndicator).setInvisible();
        }
    }

    @Test
    public void shouldNotUpdateDisplayIfNothingWasFetched() {
        CoreLibrary.init(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");
        when(actionService.fetchNewActions()).thenReturn(nothingFetched);
        when(formSubmissionSyncService.sync()).thenReturn(nothingFetched);
        when(allFormVersionSyncService.pullFormDefinitionFromServer()).thenReturn(nothingFetched);
        when(allFormVersionSyncService.downloadAllPendingFormFromServer()).thenReturn(DownloadStatus.nothingDownloaded);

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(null, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        assertNotNull(updateActionsTask);
        updateActionsTask.updateFromServer(status -> assertEquals(nothingFetched, status));
    }

    @Test
    public void shouldNotUpdateWhenUserIsNotLoggedIn() {
        when(context.configuration()).thenReturn(configuration);
        when(configuration.shouldSyncForm()).thenReturn(false);
        CoreLibrary.init(context);
        when(context.IsUserLoggedOut()).thenReturn(true);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(androidContext, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        assertNotNull(updateActionsTask);
        updateActionsTask.updateFromServer(status -> fail("Should not have updated from server as the user is not logged in."));

        verifyNoInteractions(actionService);
    }

    @Ignore
    @Test
    public void shouldSyncFormSubmissionsWithServer() {
        try (MockedStatic<CoreLibrary> coreLibraryMockedStatic = Mockito.mockStatic(CoreLibrary.class)) {
            coreLibraryMockedStatic.when(CoreLibrary::getInstance).thenReturn(coreLibrary);
            when(coreLibrary.context()).thenReturn(context);
            when(context.IsUserLoggedOut()).thenReturn(false);
            when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
            when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");

            UpdateActionsTask updateActionsTask = new UpdateActionsTask(androidContext, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
            assertNotNull(updateActionsTask);
            updateActionsTask.updateFromServer(status -> Assert.assertNull(status));

            verify(formSubmissionSyncService).sync();
        }
    }
}
