package org.smartregister.service;

import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.BeneficiaryType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.repository.AlertRepository;
import org.smartregister.util.ActionBuilder;

import java.util.HashMap;

import static org.mockito.Mockito.spy;

public class AlertServiceTest extends BaseUnitTest {
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

    @Test
    public void testConstructorWithAlertParam() {
        Alert alert = new Alert("Case X", "Schedule 1", "ANC 1", AlertStatus.normal, "2012-01-01", "2012-01-22");

        service.create(alert);
        Mockito.verify(alertRepository).createAlert(alert);
    }

    @Test
    public void testFindEntityById() {
        service.findByEntityId("entity-id");
        Mockito.verify(alertRepository.findByEntityId("entity-id"));
    }

    @Test
    public void testFindByEntityIdAndAlertNames() {
        service.findByEntityIdAndAlertNames("Entity 1", "AncAlert");
        Mockito.verify(alertRepository.findByEntityIdAndAlertNames("Entity 1", "AncAlert"));
    }

    @Test
    public void testByEntityIdAndOffline() {
        service.findByEntityIdAndOffline("Entity 1", "PncAlert");
        Mockito.verify(alertRepository.findOfflineByEntityIdAndName("Entity 1", "AncAlert"));
    }

    @Test
    public void testFindByEntityIdAndScheduleName() {
        service.findByEntityIdAndScheduleName("Entity 1", "Schedule 1");
        Mockito.verify(alertRepository.findByEntityIdAndScheduleName("Entity 1", "Schedule 1"));
    }

    @Test
    public  void testChangeAlertStatusToInProcess() {
        service = spy(service);
        service.changeAlertStatusToInProcess("Entity 1", "AncAlert");
        Mockito.verify(alertRepository).changeAlertStatusToInProcess("Entity 1", "AncAlert");
        Mockito.verify(service).updateFtsSearchAfterStatusChange("Entity 1", "AncAlert");
    }

    @Test
    public void testChangeAlertStatusToComplete() {
        service = spy(service);
        service.changeAlertStatusToComplete("Entity 1", "AncAlert");
        Mockito.verify(alertRepository).changeAlertStatusToComplete("Entity 1", "AncAlert");
        Mockito.verify(service).updateFtsSearchAfterStatusChange("Entity 1", "AncAlert");
    }

    @Test
    public void testDeleteAlert() {
        service.deleteAlert("Entity 1", "Visit 1");
        Mockito.verify(alertRepository).deleteVaccineAlertForEntity("Entity 1", "Visit 1");
    }

    @Test
    public void testDeleteOfflineAlerts() {
        service.deleteOfflineAlerts("Entity 1");
        Mockito.verify(alertRepository).deleteOfflineAlertsForEntity("Entity 1");
    }

    @Test
    public void testDeleteOfflineAlertsWithNames() {
        service.deleteOfflineAlerts("Entity 1", "AncAlert");
        Mockito.verify(alertRepository).deleteOfflineAlertsForEntity("Entity 1", "AncAlert");
    }

}
