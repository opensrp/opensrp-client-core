package org.opensrp.sync;

import org.opensrp.view.ProgressIndicator;

import static org.opensrp.event.Event.SYNC_COMPLETED;
import static org.opensrp.event.Event.SYNC_STARTED;

public class SyncProgressIndicator implements ProgressIndicator {
    @Override
    public void setVisible() {
        org.opensrp.Context.getInstance().allSharedPreferences().saveIsSyncInProgress(true);
        SYNC_STARTED.notifyListeners(true);
    }

    @Override
    public void setInvisible() {
        org.opensrp.Context.getInstance().allSharedPreferences().saveIsSyncInProgress(false);
        SYNC_COMPLETED.notifyListeners(true);
    }
}
