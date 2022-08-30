package org.smartregister.view.controller;

import static org.smartregister.util.Log.logWarn;

import org.smartregister.util.AppExecutorService;
import org.smartregister.view.contract.HomeContext;

import java.util.concurrent.locks.ReentrantLock;

public class NativeUpdateANMDetailsTask {
    private static final ReentrantLock lock = new ReentrantLock();
    private final ANMController anmController;
    private AppExecutorService appExecutors;

    public NativeUpdateANMDetailsTask(ANMController anmController) {
        this.anmController = anmController;
    }

    public void fetch(final NativeAfterANMDetailsFetchListener afterFetchListener) {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            if (!lock.tryLock()) {
                logWarn("Update ANM details is in progress, so going away.");
                appExecutors.executorService().shutdownNow();
            } else {
                HomeContext anm;
                try {
                    anm = anmController.getHomeContext();
                } finally {
                    lock.unlock();
                }
                if (anm != null)
                    afterFetchListener.afterFetch(anm);
            }
        });
    }
}
