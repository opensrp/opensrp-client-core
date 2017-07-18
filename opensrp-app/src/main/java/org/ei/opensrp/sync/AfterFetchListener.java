package org.ei.opensrp.sync;

import org.ei.opensrp.domain.FetchStatus;

public interface AfterFetchListener {
    void afterFetch(FetchStatus fetchStatus);
}
