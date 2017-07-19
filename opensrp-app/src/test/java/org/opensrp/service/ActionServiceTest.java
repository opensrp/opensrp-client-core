package org.opensrp.service;

import org.opensrp.repository.AllSharedPreferences;
import org.robolectric.RobolectricTestRunner;
import org.opensrp.domain.Response;
import org.opensrp.domain.ResponseStatus;
import org.ei.drishti.dto.Action;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllReports;
import org.opensrp.repository.AllSettings;
import org.opensrp.router.ActionRouter;
import org.opensrp.util.ActionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.opensrp.domain.FetchStatus.*;
import static org.opensrp.domain.ResponseStatus.failure;
import static org.opensrp.domain.ResponseStatus.success;
import static org.opensrp.util.ActionBuilder.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class ActionServiceTest {
    @Mock
    private DrishtiService drishtiService;
    @Mock
    private AllSettings allSettings;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllReports allReports;
    @Mock
    private ActionRouter actionRouter;

    private ActionService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ActionService(drishtiService, allSettings, allSharedPreferences, allReports, actionRouter);
    }

    @Test
    public void shouldFetchAlertActionsAndNotSaveAnythingIfThereIsNothingNewToSave() throws Exception {
        setupActions(success, new ArrayList<Action>());

        assertEquals(nothingFetched, service.fetchNewActions());

        verify(drishtiService).fetchNewActions("ANM X", "1234");
        verifyNoMoreInteractions(drishtiService);
        verifyNoMoreInteractions(actionRouter);
    }

    @Test
    public void shouldNotSaveAnythingIfTheDrishtiResponseStatusIsFailure() throws Exception {
        setupActions(failure, asList(actionForCloseAlert("Case X", "ANC 1", "2012-01-01", "0")));

        assertEquals(fetchedFailed, service.fetchNewActions());

        verify(drishtiService).fetchNewActions("ANM X", "1234");
        verifyNoMoreInteractions(drishtiService);
        verifyNoMoreInteractions(actionRouter);
    }

    @Test
    public void shouldFetchAlertActionsAndSaveThemToRepository() throws Exception {
        Action action = ActionBuilder.actionForCreateAlert("Case X", "normal", "mother", "Ante Natal Care - Normal", "ANC 1", "2012-01-01", null, "0");
        setupActions(success, asList(action));

        assertEquals(fetched, service.fetchNewActions());

        verify(drishtiService).fetchNewActions("ANM X", "1234");
        verify(actionRouter).directAlertAction(action);
    }

    @Test
    public void shouldUpdatePreviousIndexWithIndexOfEachActionThatIsHandled() throws Exception {

        Action firstAction = actionForCreateAlert("Case X", "normal", "mother", "Ante Natal Care - Normal", "ANC 1", "2012-01-01", "2012-01-22", "11111");
        Action secondAction = actionForCreateAlert("Case Y", "normal", "mother", "Ante Natal Care - Normal", "ANC 2", "2012-01-01", "2012-01-11", "12345");

        setupActions(success, asList(firstAction, secondAction));

        service.fetchNewActions();

        InOrder inOrder = inOrder(actionRouter, allSettings);
        inOrder.verify(actionRouter).directAlertAction(firstAction);
        inOrder.verify(allSettings).savePreviousFetchIndex("11111");
        inOrder.verify(actionRouter).directAlertAction(secondAction);
        inOrder.verify(allSettings).savePreviousFetchIndex("12345");
    }

    @Test
    public void shouldHandleDifferentKindsOfActions() throws Exception {
        Action reportAction = actionForReport("Case X", "annual target");
        Action alertAction = actionForCreateAlert("Case X", "normal", "mother", "Ante Natal Care - Normal", "ANC 1", "2012-01-01", null, "0");
        Action closeMotherAction = actionForCloseMother("Case X");
        setupActions(success, asList(reportAction, alertAction, closeMotherAction));

        service.fetchNewActions();

        verify(allReports).handleAction(reportAction);
        verify(actionRouter).directAlertAction(alertAction);
        verify(actionRouter).directMotherAction(closeMotherAction);
    }

    private void setupActions(ResponseStatus status, List<Action> list) {
        when(allSettings.fetchPreviousFetchIndex()).thenReturn("1234");
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn("ANM X");
        when(drishtiService.fetchNewActions("ANM X", "1234")).thenReturn(new Response<List<Action>>(status, list));
    }
}
