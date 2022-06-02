package org.smartregister.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.contract.StatsFragmentContract;
import org.smartregister.view.interactor.StatsFragmentInteractor;
import org.smartregister.view.presenter.StatsFragmentPresenter;

import java.util.HashMap;
import java.util.Map;

public class StatsFragmentPresenterTest extends BaseUnitTest {

    @Spy
    StatsFragmentContract.View view;
    private StatsFragmentInteractor interactor;
    private StatsFragmentPresenter presenter;

    @Before
    public void setUp() {
        presenter = Mockito.mock(StatsFragmentPresenter.class, Mockito.CALLS_REAL_METHODS);
        interactor = Mockito.mock(StatsFragmentInteractor.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(presenter, "interactor", interactor);
        ReflectionHelpers.setField(presenter, "view", view);
    }

    @Test
    public void onECSyncInfoFetchedRefreshesECSyncInfo() {
        Map<String, Integer> syncInfoMap = new HashMap<>();
        presenter.onECSyncInfoFetched(syncInfoMap);
        Mockito.verify(view).refreshECSyncInfo(ArgumentMatchers.eq(syncInfoMap));
    }

    @Test
    public void fetchSyncInfoCallsInteractorFetch() {
        presenter.fetchSyncInfo();
        Mockito.verify(interactor).fetchECSyncInfo();
    }
}
