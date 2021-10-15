package org.smartregister.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.job.P2PServiceWorkRequest;
import org.smartregister.p2p.callback.SyncFinishedCallback;
import org.smartregister.sync.intent.P2PProcessRecordsWorker;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/05/2019
 */

public class P2PSyncFinishCallback implements SyncFinishedCallback {

    @Override
    public void onSuccess(@NonNull HashMap<String, Integer> hashMap) {
        scheduleProcessJob();
    }

    @Override
    public void onFailure(@NonNull Exception e, @Nullable HashMap<String, Integer> hashMap) {
        scheduleProcessJob();
    }

    private void scheduleProcessJob(){
        P2PServiceWorkRequest.scheduleJobImmediately(P2PProcessRecordsWorker.class);
    }
}
