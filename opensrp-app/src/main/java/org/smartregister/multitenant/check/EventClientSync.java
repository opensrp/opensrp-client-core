package org.smartregister.multitenant.check;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.sync.intent.SyncIntentWorker;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class EventClientSync extends SyncIntentWorker {

    private Context context;

    public EventClientSync(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    protected void performSync() {
        init(context);
        handleSync();
    }

    @Override
    protected void pullECFromServer() {
        // Do not pull from EC Server
    }
}
