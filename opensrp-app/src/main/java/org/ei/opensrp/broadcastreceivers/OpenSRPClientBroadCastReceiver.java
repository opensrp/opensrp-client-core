package org.ei.opensrp.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.view.activity.SecuredActivity;


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

            if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                ((SecuredActivity) activity).showToast("TIME CHANGED");
                Log.d(TAG, "timechanged");
            } else if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                ((SecuredActivity) activity).showToast("TIMEZONE CHANGED");
                Log.d(TAG, "timezonechanged");
            } else if (action.equals(AllConstants.CloudantSync.ACTION_DATABASE_CREATED)) {
                //  ((SecuredActivity) activity).loadDatabase();
            } else if (action.equals(AllConstants.CloudantSync.ACTION_REPLICATION_COMPLETED)) {
                Integer docsReplicated = intent.getIntExtra(AllConstants.CloudantSync.DOCUMENTS_REPLICATED, 0);
                Integer batchReplicated = intent.getIntExtra(AllConstants.CloudantSync.BATCHES_REPLICATED, 0);
                //((SecuredActivity) activity).showToast("Replication completed.");
            } else if (action.equals(AllConstants.CloudantSync.ACTION_REPLICATION_ERROR)) {
                ((SecuredActivity) activity).showToast("Replicaton error occurred");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
