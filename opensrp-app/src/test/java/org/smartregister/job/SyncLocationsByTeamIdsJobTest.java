package org.smartregister.job;

public class SyncLocationsByTeamIdsJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.SyncLocationsByTeamIdsIntentService";
    }

    @Override
    protected BaseWorkRequest getJob() {
        return new SyncLocationsByTeamIdsWorkRequest();
    }
}
