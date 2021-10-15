package org.smartregister.job;

public class SyncAllLocationsServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.SyncAllLocationsIntentService";
    }

    @Override
    protected BaseWorkRequest getJob() {
        return new SyncAllLocationsServiceWorkRequest();
    }
}
