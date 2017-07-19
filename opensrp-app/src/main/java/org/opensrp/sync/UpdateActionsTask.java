package org.opensrp.sync;

import android.content.Context;
import android.widget.Toast;

import org.opensrp.domain.DownloadStatus;
import org.opensrp.domain.FetchStatus;
import org.opensrp.service.ActionService;
import org.opensrp.service.AllFormVersionSyncService;
import org.opensrp.service.FormSubmissionSyncService;
import org.opensrp.view.BackgroundAction;
import org.opensrp.view.LockingBackgroundTask;
import org.opensrp.view.ProgressIndicator;

import static org.opensrp.domain.FetchStatus.fetched;
import static org.opensrp.domain.FetchStatus.nothingFetched;
import static org.opensrp.util.Log.logInfo;

public class UpdateActionsTask {
    private final LockingBackgroundTask task;
    private ActionService actionService;
    private Context context;
    private FormSubmissionSyncService formSubmissionSyncService;
    private AllFormVersionSyncService allFormVersionSyncService;
    private AdditionalSyncService additionalSyncService;

    public UpdateActionsTask(Context context, ActionService actionService, FormSubmissionSyncService formSubmissionSyncService, ProgressIndicator progressIndicator,
                             AllFormVersionSyncService allFormVersionSyncService) {
        this.actionService = actionService;
        this.context = context;
        this.formSubmissionSyncService = formSubmissionSyncService;
        this.allFormVersionSyncService = allFormVersionSyncService;
        this.additionalSyncService = null;
        task = new LockingBackgroundTask(progressIndicator);
    }

    public void setAdditionalSyncService(AdditionalSyncService additionalSyncService) {
        this.additionalSyncService = additionalSyncService;
    }

    public void updateFromServer(final AfterFetchListener afterFetchListener) {
        if (org.opensrp.Context.getInstance().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }

        task.doActionInBackground(new BackgroundAction<FetchStatus>() {
            public FetchStatus actionToDoInBackgroundThread() {

                FetchStatus fetchStatusForForms = formSubmissionSyncService.sync();
                FetchStatus fetchStatusForActions = actionService.fetchNewActions();
                FetchStatus fetchStatusAdditional = additionalSyncService == null ? nothingFetched : additionalSyncService.sync();

                if(org.opensrp.Context.getInstance().configuration().shouldSyncForm()) {

                    allFormVersionSyncService.verifyFormsInFolder();
                    FetchStatus fetchVersionStatus = allFormVersionSyncService.pullFormDefinitionFromServer();
                    DownloadStatus downloadStatus = allFormVersionSyncService.downloadAllPendingFormFromServer();

                    if(downloadStatus == DownloadStatus.downloaded) {
                        allFormVersionSyncService.unzipAllDownloadedFormFile();
                    }

                    if(fetchVersionStatus == fetched || downloadStatus == DownloadStatus.downloaded) {
                        return fetched;
                    }
                }


                if(fetchStatusForActions == fetched || fetchStatusForForms == fetched || fetchStatusAdditional == fetched)
                    return fetched;

                return fetchStatusForForms;
            }

            public void postExecuteInUIThread(FetchStatus result) {
                if (result != null && context != null && result != nothingFetched) {
                    Toast.makeText(context, result.displayValue(), Toast.LENGTH_SHORT).show();
                }
                afterFetchListener.afterFetch(result);
            }
        });
    }
}