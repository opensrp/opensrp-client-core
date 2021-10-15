package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.CoreLibrary;
import org.smartregister.service.HTTPAgent;

import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Vincent Karuri on 26/08/2019
 */
public abstract class BaseSyncIntentWorker extends Worker {
    public static final String ERROR = "error";

    abstract void onRunWork() throws SocketException;

    public BaseSyncIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    protected final void setUpHttpAgent() {
        CoreLibrary coreLibrary = CoreLibrary.getInstance();
        HTTPAgent httpAgent = coreLibrary.context().httpAgent();
        httpAgent.setConnectTimeout(coreLibrary.getSyncConfiguration().getConnectTimeout());
        httpAgent.setReadTimeout(coreLibrary.getSyncConfiguration().getReadTimeout());
    }

    @NonNull
    @NotNull
    @Override
    public final ListenableWorker.Result doWork() {
        setUpHttpAgent();
        try {
            onRunWork();
            return Result.success(Data.EMPTY);
        } catch (SocketException e) {
            return Result.retry();
        } catch (Exception exception) {
            return Result.failure(new Data.Builder().putString(ERROR, exception.getMessage()).build());
        }
    }

}
