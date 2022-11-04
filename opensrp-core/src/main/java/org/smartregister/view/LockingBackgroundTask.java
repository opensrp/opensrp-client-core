package org.smartregister.view;

import static org.smartregister.util.Log.logVerbose;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class LockingBackgroundTask {
    private static final ReentrantLock lock = new ReentrantLock();
    private ProgressIndicator indicator;

    public LockingBackgroundTask(ProgressIndicator progressIndicator) {
        this.indicator = progressIndicator;
    }

    public <T> void doActionInBackground(final BackgroundAction<T> backgroundAction) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> indicator.setVisible());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            T result;
            if (!lock.tryLock()) {
                logVerbose("Going away. Something else is holding the lock.");
                return;
            }
            try {
                result = backgroundAction.actionToDoInBackgroundThread();

            } finally {
                lock.unlock();
            }

            handler.post(() -> {
                backgroundAction.postExecuteInUIThread(result);
                indicator.setInvisible();
            });

        });
    }
}
