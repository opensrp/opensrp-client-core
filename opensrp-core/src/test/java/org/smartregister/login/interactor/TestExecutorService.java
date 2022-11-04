package org.smartregister.login.interactor;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TestExecutorService extends AbstractExecutorService implements Executor {

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void shutdown() {
        //Do nothing
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return false;
    }
}
