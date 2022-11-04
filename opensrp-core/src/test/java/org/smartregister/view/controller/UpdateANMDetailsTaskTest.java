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

public class UpdateANMDetailsTaskTest extends BaseUnitTest {

    private UpdateANMDetailsTask updateANMDetailsTask;
    @Mock
    private ANMController anmController;
    @Mock
    AppExecutorService appExecutorService;

    @Before
    public void setUp() throws Exception {
        updateANMDetailsTask = new UpdateANMDetailsTask(anmController);
        Whitebox.setInternalState(updateANMDetailsTask, "appExecutors", appExecutorService);
    }

    @Test
    public void fetchReturnsAnmDetailsJsonString() {
        Mockito.when(appExecutorService.executorService()).thenReturn(new TestExecutorService());
        Mockito.when(appExecutorService.mainThread()).thenReturn(new TestExecutorService());
        AfterANMDetailsFetchListener listener = Mockito.mock(AfterANMDetailsFetchListener.class);
        Mockito.when(anmController.get()).thenReturn("{TestString}");
        updateANMDetailsTask.fetch(listener);
        Mockito.verify(anmController, Mockito.atLeastOnce()).get();
        Mockito.verify(listener, Mockito.atLeastOnce()).afterFetch(ArgumentMatchers.any(String.class));
    }
}
