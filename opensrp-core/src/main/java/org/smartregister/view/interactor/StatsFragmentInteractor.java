package org.smartregister.view.interactor;

import org.smartregister.util.AppExecutors;
import org.smartregister.util.StatsUtils;
import org.smartregister.view.contract.StatsFragmentContract;

import java.util.Map;

import timber.log.Timber;

public class StatsFragmentInteractor implements StatsFragmentContract.Interactor {

    private final AppExecutors appExecutors;

    private final StatsFragmentContract.Presenter presenter;

    public StatsFragmentInteractor(StatsFragmentContract.Presenter presenter) {
        this.presenter = presenter;
        appExecutors = new AppExecutors();
    }

    @Override
    public void fetchECSyncInfo() {
        try {
            Map<String, String> syncInfoMap = new StatsUtils().fetchStatsInfo();
            appExecutors.mainThread().execute(() -> presenter.onECSyncInfoFetched(syncInfoMap));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

}
