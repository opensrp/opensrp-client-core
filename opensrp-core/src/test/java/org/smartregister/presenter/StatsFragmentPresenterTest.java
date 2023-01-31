package org.smartregister.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.view.contract.StatsFragmentContract;
import org.smartregister.view.interactor.StatsFragmentInteractor;
import org.smartregister.view.presenter.StatsFragmentPresenter;

import java.util.HashMap;
import java.util.Map;


public class StatsFragmentPresenterTest {

    @Mock
    private StatsFragmentInteractor interactor;

    private StatsFragmentPresenter presenter;

    @Mock
    StatsFragmentContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = Mockito.mock(StatsFragmentPresenter.class, Mockito.CALLS_REAL_METHODS);
        view = Mockito.spy(view);
        interactor = Mockito.spy(interactor);
        ReflectionHelpers.setField(presenter, "interactor", interactor);
        ReflectionHelpers.setField(presenter, "view", view);
    }

    @Test
    public void onECSyncInfoFetchedRefreshesECSyncInfo() {
        Map<String, String> syncInfoMap = new HashMap<>();
        presenter.onECSyncInfoFetched(syncInfoMap);
        Mockito.verify(view, Mockito.times(2)).refreshECSyncInfo(ArgumentMatchers.eq(syncInfoMap));
    }

    @Test
    public void fetchSyncInfoCallsInteractorFetch() {
        presenter.fetchSyncInfo();
        Mockito.verify(interactor).fetchECSyncInfo();
    }
}
