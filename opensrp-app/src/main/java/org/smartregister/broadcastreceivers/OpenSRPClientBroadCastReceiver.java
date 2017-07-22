package org.smartregister.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.smartregister.AllConstants;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by onamacuser on 16/03/2016.
 */
public class OpenSRPClientBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = OpenSRPClientBroadCastReceiver.class.getCanonicalName();
    Activity activity;

    public OpenSRPClientBroadCastReceiver(Activity _activity) {
        activity = _activity;

    }

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();

            switch (action) {
                case Intent.ACTION_TIME_CHANGED:
                    ((SecuredActivity) activity).showToast("TIME CHANGED");
                    Log.d(TAG, "timechanged");
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    ((SecuredActivity) activity).showToast("TIMEZONE CHANGED");
                    Log.d(TAG, "timezonechanged");
                    break;
                case AllConstants.CloudantSync.ACTION_DATABASE_CREATED:
//                      ((SecuredActivity) activity).loadDatabase();
                    break;
                case AllConstants.CloudantSync.ACTION_REPLICATION_COMPLETED:
//                    Integer docsReplicated = intent.getIntExtra(
//                            AllConstants.CloudantSync.DOCUMENTS_REPLICATED, 0);
//                    Integer batchReplicated = intent.getIntExtra(
//                            AllConstants.CloudantSync.BATCHES_REPLICATED, 0);
//                    ((SecuredActivity) activity).showToast("Replication completed.");
                    break;
                case AllConstants.CloudantSync.ACTION_REPLICATION_ERROR:
                    ((SecuredActivity) activity).showToast("Replication error occurred");
                    break;
                default:
                    // Do nothing
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
