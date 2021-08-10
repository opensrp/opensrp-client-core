package org.smartregister.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.view.presenter.StatsFragmentPresenter;


public class StatsFragmentTest extends BaseUnitTest {

    private StatsFragment statsFragment;

    private StatsFragmentPresenter presenter;

    @Mock
    private Bundle savedInstanceState;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        statsFragment = Mockito.mock(StatsFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getInstanceReturnsNonNullFragmentInstance() {
        Assert.assertNotNull(StatsFragment.newInstance(Mockito.mock(Bundle.class)));
    }

    @Test
    public void onCreateInitializesPresenter() {
        FragmentManager mChildFragmentManager = Mockito.mock(FragmentManager.class);
        Whitebox.setInternalState(statsFragment, "mChildFragmentManager", mChildFragmentManager);
        statsFragment.onCreate(savedInstanceState);
        presenter = ReflectionHelpers.getField(statsFragment, "presenter");
        Assert.assertNotNull(presenter);
    }

    @Test
    public void onCreateViewInitsViewsAndReturnsCorrectNullView() {
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        ViewGroup container = Mockito.mock(ViewGroup.class);

        Button btnRefreshStats = Mockito.mock(Button.class);
        View rootView = Mockito.spy(Mockito.mock(View.class));
        statsFragment = Mockito.spy(statsFragment);

        presenter = Mockito.spy(Mockito.mock(StatsFragmentPresenter.class));
        ReflectionHelpers.setField(statsFragment, "presenter", presenter);

        Mockito.doReturn(btnRefreshStats).when(rootView).findViewById(R.id.refresh_button);
        Mockito.doReturn(rootView).when(inflater).inflate(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ViewGroup.class), ArgumentMatchers.anyBoolean());

        View returnedView = statsFragment.onCreateView(inflater, container, savedInstanceState);
        Assert.assertNotNull(returnedView);
        Mockito.verify(presenter).fetchSyncInfo();
    }

}
