package org.smartregister.job;

import org.smartregister.sync.intent.SyncTaskIntentWorker;

public class SyncTaskServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.SyncTaskIntentService";
    }

    @Override
    protected BaseWorkRequest getJob() {
        return new SyncTaskServiceWorkRequest(SyncTaskIntentWorker.class);
    }
}
