package org.smartregister.service;

import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.BeneficiaryType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.repository.AlertRepository;
import org.smartregister.util.ActionBuilder;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class AlertServiceTest {
    @Mock
    private AlertRepository alertRepository;

    private AlertService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new AlertService(alertRepository);
    }

    @Test
    public void shouldAddAnAlertIntoAlertRepositoryForMotherCreateAlertAction() throws Exception {
        Action actionForMother = ActionBuilder.actionForCreateAlert("Case X", AlertStatus.normal.value(), BeneficiaryType.mother.value(), "Schedule 1", "ANC 1", "2012-01-01", "2012-01-22", "0");

        service.create(actionForMother);

        Mockito.verify(alertRepository).createAlert(new Alert("Case X", "Schedule 1", "ANC 1", AlertStatus.normal, "2012-01-01", "2012-01-22"));
        Mockito.verifyNoMoreInteractions(alertRepository);
    }

    @Test
    public void shouldAddAnAlertIntoAlertRepositoryForECCreateAlertAction() throws Exception {
        Action actionForEC = ActionBuilder.actionForCreateAlert("Case X", AlertStatus.normal.value(), BeneficiaryType.ec.value(), "Schedule 1", "Milestone 1", "2012-01-01", "2012-01-22", "0");

        service.create(actionForEC);

        Mockito.verify(alertRepository).createAlert(new Alert("Case X", "Schedule 1", "Milestone 1", AlertStatus.normal, "2012-01-01", "2012-01-22"));
        Mockito.verifyNoMoreInteractions(alertRepository);
    }

    @Test
    public void shouldAddAnAlertIntoAlertRepositoryForECCreateAlertActionWhenThereIsNoMother() throws Exception {
        Action actionForEC = ActionBuilder.actionForCreateAlert("Case X", AlertStatus.normal.value(), BeneficiaryType.ec.value(), "Schedule 1", "Milestone 1", "2012-01-01", "2012-01-22", "0");

        service.create(actionForEC);

        Mockito.verify(alertRepository).createAlert(new Alert("Case X", "Schedule 1", "Milestone 1", AlertStatus.normal, "2012-01-01", "2012-01-22"));
        Mockito.verifyNoMoreInteractions(alertRepository);
    }

    @Test
    public void shouldNotCreateIfActionIsInactive() throws Exception {
        Action actionForMother = new Action("Case X", "alert", "createAlert", new HashMap<String, String>(), "0", false, new HashMap<String, String>());

        service.create(actionForMother);

        Mockito.verifyZeroInteractions(alertRepository);
    }

    @Test
    public void shouldAddAnAlertIntoAlertRepositoryForChildCreateAlertAction() throws Exception {
        Action actionForMother = ActionBuilder.actionForCreateAlert("Case X", AlertStatus.urgent.value(), BeneficiaryType.child.value(), "Schedule 1", "Milestone 1", "2012-01-01", "2012-01-22", "0");

        service.create(actionForMother);

        Mockito.verify(alertRepository).createAlert(new Alert("Case X", "Schedule 1", "Milestone 1", AlertStatus.urgent, "2012-01-01", "2012-01-22"));
        Mockito.verifyNoMoreInteractions(alertRepository);
    }

    @Test
    public void shouldMarkAlertAsClosedInRepositoryForCloseActions() throws Exception {
        Action firstAction = ActionBuilder.actionForCloseAlert("Case X", "ANC 1", "2012-01-01", "0");
        Action secondAction = ActionBuilder.actionForCloseAlert("Case Y", "ANC 2", "2012-01-01", "0");

        service.close(firstAction);
        service.close(secondAction);

        Mockito.verify(alertRepository).markAlertAsClosed("Case X", "ANC 1", "2012-01-01");
        Mockito.verify(alertRepository).markAlertAsClosed("Case Y", "ANC 2", "2012-01-01");
        Mockito.verifyNoMoreInteractions(alertRepository);
    }

    @Test
    public void shouldDeleteAllFromRepositoryForDeleteAllActions() throws Exception {
        Action firstAction = ActionBuilder.actionForDeleteAllAlert("Case X");
        Action secondAction = ActionBuilder.actionForDeleteAllAlert("Case Y");

        service.deleteAll(firstAction);
        service.deleteAll(secondAction);

        Mockito.verify(alertRepository).deleteAllAlertsForEntity("Case X");
        Mockito.verify(alertRepository).deleteAllAlertsForEntity("Case Y");
        Mockito.verifyNoMoreInteractions(alertRepository);
    }
}
