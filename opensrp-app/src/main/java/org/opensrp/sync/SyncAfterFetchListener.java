package org.opensrp.sync;

import org.opensrp.domain.FetchStatus;

import static org.opensrp.event.Event.ON_DATA_FETCHED;

public class SyncAfterFetchListener implements AfterFetchListener {
    public void afterFetch(FetchStatus status) {
        ON_DATA_FETCHED.notifyListeners(status);
    }
}
