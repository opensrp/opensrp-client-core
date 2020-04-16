package org.smartregister.multitenant.check;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.smartregister.CoreLibrary;
import org.smartregister.sync.helper.LocationServiceHelper;
import org.smartregister.sync.intent.LocationIntentService;
import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class StructuresSync {

    private Context context;

    public StructuresSync(@NonNull Context context) {
        this.context = context;
    }

    protected void performSync() {
        org.smartregister.Context context = CoreLibrary.getInstance().context();
        LocationServiceHelper locationServiceHelper = new LocationServiceHelper(
                context.getLocationRepository(),
                context.getLocationTagRepository(),
                context.getStructureRepository());
        locationServiceHelper.syncCreatedStructureToServer();
    }

    public void sendBroadcast(Intent intent) {

    }
}
