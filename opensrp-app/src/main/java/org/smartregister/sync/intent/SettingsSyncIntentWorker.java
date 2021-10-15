package org.smartregister.sync.intent;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.job.SyncServiceWorkRequest;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;

import java.net.SocketException;

import timber.log.Timber;

import static org.smartregister.util.Log.logError;

/**
 * Created by ndegwamartin on 14/09/2018.
 */
public class SettingsSyncIntentWorker extends BaseSyncIntentWorker {
    public static final String SETTINGS_URL = "/rest/settings/sync";

    private static final String TAG = SettingsSyncIntentWorker.class.getCanonicalName();

    protected SyncSettingsServiceHelper syncSettingsServiceHelper;

    public static final String EVENT_SYNC_COMPLETE = "event_sync_complete";

    public SettingsSyncIntentWorker(@NonNull android.content.@NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        Context context = CoreLibrary.getInstance().context();
        syncSettingsServiceHelper = new SyncSettingsServiceHelper(context.configuration().dristhiBaseURL(), context.getHttpAgent());
        boolean isSuccessfulSync = processSettings(new Intent());
        if (isSuccessfulSync) {
            SyncServiceWorkRequest.scheduleJobImmediately(SyncIntentWorker.class);
        }
    }

    protected boolean processSettings(Intent intent) {
        Timber.d("In Settings Sync Intent Service...");
        boolean isSuccessfulSync = true;
        if (intent != null) {
            try {
                int count = syncSettingsServiceHelper.processIntent();
                if (count > 0) {
                    intent.putExtra(AllConstants.INTENT_KEY.SYNC_TOTAL_RECORDS, count);
                }
            } catch (JSONException e) {
                isSuccessfulSync = false;
                logError(TAG + " Error fetching client settings");
            }
        }
        return isSuccessfulSync;
    }
}

