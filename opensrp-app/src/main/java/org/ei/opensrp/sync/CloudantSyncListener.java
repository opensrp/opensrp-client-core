package org.ei.opensrp.sync;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by keyman on 21/07/16.
 */
public class CloudantSyncListener {

    /**
     * Called by CloudantSyncHandler when it receives a replication complete callback.
     * CloudantSyncHandler takes care of calling this on the main thread.
     */
    void replicationComplete() {
    }

    /**
     * Called by TasksModel when it receives a replication error callback.
     * TasksModel takes care of calling this on the main thread.
     */
    void replicationError() {
    }
}
