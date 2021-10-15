package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.sync.helper.LocationServiceHelper;

import java.net.SocketException;

import timber.log.Timber;

public class SyncLocationsByLevelAndTagsIntentWorker extends BaseSyncIntentWorker {

    private static final String TAG = "SyncLocationsByLevelAndTagsIntentService";

    public SyncLocationsByLevelAndTagsIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        LocationServiceHelper locationServiceHelper = LocationServiceHelper.getInstance();

        try {
            locationServiceHelper.fetchLocationsByLevelAndTags();
        } catch (Exception e){
            Timber.e(e);
        }
    }
}