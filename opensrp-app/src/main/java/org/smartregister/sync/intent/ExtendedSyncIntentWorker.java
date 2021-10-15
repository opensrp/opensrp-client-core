package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.CoreLibrary;
import org.smartregister.job.ValidateSyncDataServiceWorkRequest;
import org.smartregister.service.ActionService;
import org.smartregister.util.NetworkUtils;

import java.net.SocketException;

import timber.log.Timber;


public class ExtendedSyncIntentWorker extends BaseSyncIntentWorker {

    private ActionService actionService = CoreLibrary.getInstance().context().actionService();

    public ExtendedSyncIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        try {
            if (NetworkUtils.isNetworkAvailable()) {
                if (!CoreLibrary.getInstance().getSyncConfiguration().disableActionService()) {
                    actionService.fetchNewActions();
                }
                startSyncValidation();
            }

        } catch (Exception e) {
            Timber.e(e);
            throw new RuntimeException(e);
        }
    }

    private void startSyncValidation() {
        ValidateSyncDataServiceWorkRequest.scheduleJobImmediately(ValidateIntentWorker.class);
    }
}
