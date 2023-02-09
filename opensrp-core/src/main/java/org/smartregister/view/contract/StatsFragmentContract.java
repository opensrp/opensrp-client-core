package org.smartregister.view.contract;

import android.content.Context;

import java.util.Map;

public interface StatsFragmentContract {
    interface Presenter {

        void onECSyncInfoFetched(Map<String, String> syncInfoMap);

        void fetchSyncInfo();

    }

    interface View {
        Context getContext();

        void refreshECSyncInfo(Map<String, String> syncInfoMap);
    }

    interface Interactor {
        void fetchECSyncInfo();
    }
}
