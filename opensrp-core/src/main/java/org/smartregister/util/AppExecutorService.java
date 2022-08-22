package org.smartregister.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Global ExecutorService for the whole application.
 *
 * This provides a single thread Executor and main thread Executor for use
 */
public class AppExecutorService {

    private ExecutorService executorService;
    private Executor mainThread;

    public AppExecutorService(ExecutorService executorService, Executor mainThread) {
        this.executorService = executorService;
        this.mainThread = mainThread;
    }

    public AppExecutorService() {
        this(Executors.newSingleThreadExecutor(),
                new AppExecutorService.MainThreadExecutor());
    }

    public ExecutorService executorService() {
        return executorService;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
