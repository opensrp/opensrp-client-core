package org.smartregister.view.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.contract.HomeContext;

public class NativeUpdateANMDetailsTaskTest extends BaseUnitTest {

    private NativeUpdateANMDetailsTask updateANMDetailsTask;
    @Mock
    private ANMController anmController;

    @Before
    public void setUp() throws Exception {
        updateANMDetailsTask = new NativeUpdateANMDetailsTask(anmController);
    }

    @Test
    public void fetchGetsHomeContext() {
        NativeAfterANMDetailsFetchListener listener = Mockito.mock(NativeAfterANMDetailsFetchListener.class);
        Mockito.when(anmController.getHomeContext()).thenReturn(Mockito.mock(HomeContext.class));
        updateANMDetailsTask.fetch(listener);
        Mockito.verify(anmController, Mockito.atLeastOnce()).getHomeContext();
        Mockito.verify(listener, Mockito.atLeastOnce()).afterFetch(ArgumentMatchers.any(HomeContext.class));
    }
}
