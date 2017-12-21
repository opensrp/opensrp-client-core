package org.smartregister.commonregistry;


import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.commonregistry.shared.FakeRepository;
import org.smartregister.repository.Repository;
import org.smartregister.service.AlertService;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

/**
 * Created by onaio on 29/08/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class, CommonRepository.class})
public class CommonRepositoryTest extends BaseUnitTest {

    public static final String ADDITIONALCOLUMN = "ADDITIONALCOLUMN";
    public static final String CUSTOMRELATIONALID = "CUSTOMRELATIONALID";

    @InjectMocks
    private CommonRepository commonRepository;

    @InjectMocks
    private Repository repository;

    @InjectMocks
    private FakeRepository fakerepository;

    @Mock
    private CommonFtsObject commonFtsObject;

    @Mock
    private AlertService alertService;


    @Mock
    private Context context;

    @Mock
    private SQLiteDatabase sqliteDatabase;

//    @Before
//    public void setUp() {
//
//        initMocks(this);
//        assertNotNull(commonRepository);
//    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(tablename, tableColumns);
        assertNotNull(commonRepository);
        Assert.assertNotNull(new CommonRepository(commonFtsObject, tablename, tableColumns));
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCallWithAdditionalColumns() throws Exception {
        String tablename = "";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        when(commonFtsObject.getCustomRelationalId(anyString())).thenReturn(CUSTOMRELATIONALID);
        Assert.assertNotNull(new CommonRepository(commonFtsObject, tablename, tableColumns));
    }

    @Test
    public void addCallsDatabaseInsert1times() throws Exception {
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        commonRepository.add(new CommonPersonObject("", "", new HashMap<String, String>(), ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(anyString(), isNull(String.class), any(ContentValues.class));
    }

    @Test
    public void findByCaseIDCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByCaseID(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void findByBaseEntityIdCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByBaseEntityId(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void findByGOBHHIDCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findHHByGOBHHID(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void allcommonCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.allcommon());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void findByCaseIDsCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByCaseIDs("caseID1", "caseID2"));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void findByRelationalIDsCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByRelationalIDs("relationalID", "relationalID2"));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void findByRelational_IDsCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByRelational_IDs("relationalID", "relationalID2"));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void customQueryReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.customQuery("", new String[]{}, ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void customQueryForCompleteRowReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.customQueryForCompleteRow("", new String[]{}, ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void rawCustomQueryForAdapterRowReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.rawCustomQueryForAdapter(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String[].class));

    }

    @Test
    public void readAllcommonforCursorAdapterReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"_id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        matrixCursor.moveToFirst();
        Assert.assertNotNull(commonRepository.readAllcommonforCursorAdapter(matrixCursor));

    }

    @Test
    public void updateDetailsCallsDatabaseUpdate1Times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        commonRepository.updateDetails("caseID", new HashMap<String, String>());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void mergeDetailsCallsDatabaseUpdate1Times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        commonRepository.mergeDetails("caseID", new HashMap<String, String>());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }


}