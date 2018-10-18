package org.smartregister.job.mock;

import org.smartregister.sync.intent.SyncIntentService;

import static org.smartregister.AllConstants.SyncFilters.FILTER_LOCATION_ID;

/**
 * Created by samuelgithengi on 10/16/18.
 */
public class MockSyncIntentService extends SyncIntentService {
    @Override
    public int getSyncMaxRetries() {
        return 3;
    }

    @Override
    public String getSyncFilterParam() {
        return FILTER_LOCATION_ID;
    }

    @Override
    public String getSyncFilterValue() {
        return "";
    }
}
