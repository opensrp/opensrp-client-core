package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.sync.helper.PractitionerSyncHelper;

import timber.log.Timber;

public class SyncPractitionersByIdAndRoleIntentService extends BaseSyncIntentService {

    private static final String TAG = SyncPractitionersByIdAndRoleIntentService.class.getCanonicalName();

    public SyncPractitionersByIdAndRoleIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        PractitionerSyncHelper practitionerSyncHelper = PractitionerSyncHelper.getInstance();

        try {
            practitionerSyncHelper.syncChwPractitionersByIdAndRoleFromServer();
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }
}
