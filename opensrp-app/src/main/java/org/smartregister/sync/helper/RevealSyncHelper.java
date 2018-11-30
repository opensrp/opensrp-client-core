package org.smartregister.sync.helper;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.CampaignRepository;


/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class RevealSyncHelper {

    protected final Context context;
    protected final CampaignRepository campaignRepository;


    protected AllSharedPreferences allSharedPreferences;

    protected static RevealSyncHelper instance;

    public static RevealSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RevealSyncHelper(context, CoreLibrary.getInstance().context().getCampaignRepository());
            instance.allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        }
        return instance;
    }

    @VisibleForTesting
    protected RevealSyncHelper(Context context, CampaignRepository campaignRepository) {
        this.context = context;
        this.campaignRepository = campaignRepository;
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        allSharedPreferences.saveLastSyncDate(lastSyncTimeStamp);
    }

    public void updateLastCheckTimeStamp(long lastCheckTimeStamp) {
        allSharedPreferences.updateLastCheckTimeStamp(lastCheckTimeStamp);
    }

    public long getLastCheckTimeStamp() {
        return allSharedPreferences.fetchLastCheckTimeStamp();
    }


}

