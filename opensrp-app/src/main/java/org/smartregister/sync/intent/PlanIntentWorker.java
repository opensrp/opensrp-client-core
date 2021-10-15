package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.sync.helper.PlanIntentServiceHelper;

import java.net.SocketException;

/**
 * Created by Vincent Karuri on 08/05/2019
 */
public class PlanIntentWorker extends BaseSyncIntentWorker {

    private static final String TAG = "PlanIntentService";

    public PlanIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        PlanIntentServiceHelper.getInstance().syncPlans();
    }
}
