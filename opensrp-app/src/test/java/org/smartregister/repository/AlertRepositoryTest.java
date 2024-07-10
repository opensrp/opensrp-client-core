package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.service.AlertService;
import org.smartregister.view.activity.DrishtiApplication;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by onaio on 29/08/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class, AlertRepository.class})
public class AlertRepositoryTest extends BaseUnitTest {

    public static final String ADDITIONALCOLUMN = "ADDITIONALCOLUMN";
    public static final String CUSTOMRELATIONALID = "CUSTOMRELATIONALID";
    public static final String[] alertColumns = {"caseID", "scheduleName", "visitCode", "status", "startDate", "expiryDate", "completionDate", "offline"};
    public static final Object[] alertRow = {"caseID", "scheduleName", "visitCode", "urgent", "startDate", "expiryDate", "completionDate", 1};

    @InjectMocks
    private AlertRepository alertRepository;

    @Mock
    private Repository repository;

    @Mock
    private CommonFtsObject commonFtsObject;

    @Mock
    private AlertService alertService;


    @Mock
    private Context context;

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Before
    public void setUp() {

        initMocks(this);

    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        Assert.assertNotNull(new AlertRepository());
    }

    @Test
    public void assertOnCreateCallsDatabaseExecSql() {
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.onCreate(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(5)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAllAlertsReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.allAlerts());
    }

    @Test
    public void createAlertsCallsInsert1TimeForNewALerts() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
//        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.urgent, "startDate", "expiryDate", true);
        alertRepository.createAlert(alert);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));

    }

    @Test
    public void createAlertsCallsUpdate1TimeForOldALerts() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.urgent, "startDate", "expiryDate", true);
        alertRepository.createAlert(alert);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void changeAlertStatusToInProcess1CallsUpdate1Time() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.changeAlertStatusToInProcess("caseID", "scheduleName");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void changeAlertStatusToCompleteCallsUpdate1Time() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.changeAlertStatusToComplete("caseID", "scheduleName");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void markAlertAsClosedCallsUpdate1Time() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.markAlertAsClosed("caseID", "scheduleName", "completionDate");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void filterActiveAlertsReturnsNotNull() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        LocalDate today = LocalDate.now();
        today = today.plusDays(5);
        Object[] alertRowForActiveALerts = {"caseID", "scheduleName", "visitCode", "urgent", "", today.toString(), "", 1};

        matrixCursor.addRow(alertRowForActiveALerts);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.allActiveAlertsForCase("caseID"));

    }

    @Test
    public void deleteAllAlertsForEntityCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteAllAlertsForEntity("caseID");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteAllOfflineAlertsForEntityCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteOfflineAlertsForEntity("caseID");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteAllOfflineAlertsForEntityAndNameCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteOfflineAlertsForEntity("caseID", "name1", "name2");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteAllAlertsCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteAllAlerts();
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class));

    }

    @Test
    public void findByEntityIDReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findByEntityId("caseID"));
    }

    @Test
    public void findByEntityIdAndAlertNamesReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findByEntityIdAndAlertNames("caseID", "names1", "names2"));
    }

    @Test
    public void findOfflineByEntityIdAndAlertNamesReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findOfflineByEntityIdAndName("caseID", "names1", "names2"));
    }

    @Test
    public void findByEntityIdAndScheduleNameReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findByEntityIdAndScheduleName("caseID", "Schedulenames"));
    }

}