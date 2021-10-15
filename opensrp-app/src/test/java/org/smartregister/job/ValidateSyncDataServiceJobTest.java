package org.smartregister.job;

/**
 * Created by ndegwamartin on 10/09/2018.
 */
public class ValidateSyncDataServiceJobTest extends ServiceJobTest {

    @Override
    protected String getServiceId() {
        return "org.smartregister.sync.intent.ValidateIntentService";
    }

    @Override
    protected BaseWorkRequest getJob() {
        return new ValidateSyncDataServiceWorkRequest();
    }
}
