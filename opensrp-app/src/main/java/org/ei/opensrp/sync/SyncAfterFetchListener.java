package org.ei.opensrp.sync;

import org.ei.opensrp.domain.FetchStatus;

import static org.ei.opensrp.event.Event.ON_DATA_FETCHED;

public class SyncAfterFetchListener implements AfterFetchListener {
    public void afterFetch(FetchStatus status) {
        ON_DATA_FETCHED.notifyListeners(status);
    }
}
