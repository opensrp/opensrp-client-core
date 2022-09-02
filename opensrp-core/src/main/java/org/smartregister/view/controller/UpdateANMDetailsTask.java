package org.smartregister.view.controller;

import static org.smartregister.util.Log.logWarn;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.util.AppExecutorService;

import java.util.concurrent.locks.ReentrantLock;

public class UpdateANMDetailsTask {
    private static final ReentrantLock lock = new ReentrantLock();
    private final ANMController anmController;
    private AppExecutorService appExecutors;

    public UpdateANMDetailsTask(ANMController anmController) {
        this.anmController = anmController;
        appExecutors = new AppExecutorService();
    }

    public void fetch(final AfterANMDetailsFetchListener afterFetchListener) {
        appExecutors.executorService().execute(() -> {
            if (!lock.tryLock()) {
                logWarn("Update ANM details is in progress, so going away.");
                appExecutors.executorService().shutdownNow();
            } else {
                String anm;
                try {
                    anm = anmController.get();
                } finally {
                    lock.unlock();
                }
                if (!StringUtils.isEmpty(anm))
                    appExecutors.mainThread().execute(() -> afterFetchListener.afterFetch(anm));
            }
        });
    }
}
