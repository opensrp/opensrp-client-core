package org.smartregister.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;

import org.smartregister.CoreLibrary;
import org.smartregister.p2p.callback.SyncFinishedCallback;
import org.smartregister.sync.wm.worker.P2pProcessRecordsWorker;
import org.smartregister.sync.wm.workerrequest.WorkRequest;

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
        WorkRequest.runImmediately(CoreLibrary.getInstance().context().applicationContext(), P2pProcessRecordsWorker.class, P2pProcessRecordsWorker.TAG, Data.EMPTY);
    }
}
