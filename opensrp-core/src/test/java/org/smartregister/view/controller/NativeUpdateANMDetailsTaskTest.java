package org.smartregister.view.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.login.interactor.TestExecutorService;
import org.smartregister.util.AppExecutorService;
import org.smartregister.view.contract.HomeContext;

public class NativeUpdateANMDetailsTaskTest extends BaseUnitTest {

    private NativeUpdateANMDetailsTask updateANMDetailsTask;
    @Mock
    private ANMController anmController;
    @Mock
    AppExecutorService appExecutorService;

    @Before
    public void setUp() throws Exception {
        updateANMDetailsTask = new NativeUpdateANMDetailsTask(anmController);
        Whitebox.setInternalState(updateANMDetailsTask, "appExecutors", appExecutorService);
    }

    @Test
    public void fetchGetsHomeContext() {
        Mockito.when(appExecutorService.executorService()).thenReturn(new TestExecutorService());
        NativeAfterANMDetailsFetchListener listener = Mockito.mock(NativeAfterANMDetailsFetchListener.class);
        Mockito.when(anmController.getHomeContext()).thenReturn(Mockito.mock(HomeContext.class));
        updateANMDetailsTask.fetch(listener);
        Mockito.verify(anmController, Mockito.atLeastOnce()).getHomeContext();
        Mockito.verify(listener, Mockito.atLeastOnce()).afterFetch(ArgumentMatchers.any(HomeContext.class));
    }
}
