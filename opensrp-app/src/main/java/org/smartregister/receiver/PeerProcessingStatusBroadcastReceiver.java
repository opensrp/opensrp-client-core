package org.smartregister.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.AllConstants;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-06-27
 */

public class PeerProcessingStatusBroadcastReceiver extends BroadcastReceiver {

    private StatusUpdate statusUpdate;

    public PeerProcessingStatusBroadcastReceiver(@NonNull StatusUpdate statusUpdate) {
        this.statusUpdate = statusUpdate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(AllConstants.PeerToPeer.KEY_IS_PROCESSING)) {
            if (statusUpdate != null) {
                statusUpdate.onStatusUpdate(intent.getBooleanExtra(AllConstants.PeerToPeer.KEY_IS_PROCESSING, false));
            }
        }
    }

    public interface StatusUpdate {

        void onStatusUpdate(boolean isProcessing);
    }
}
