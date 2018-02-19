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
        Assert.assertNotNull(commonRepository);
        Assert.assertNotNull(new CommonRepository(commonFtsObject, tablename, tableColumns));
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCallWithAdditionalColumns() throws Exception {
        String tablename = "";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        Assert.assertNotNull(new CommonRepository(commonFtsObject, tablename, tableColumns));
    }

    @Test
    public void addCallsDatabaseInsert1times() throws Exception {
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        commonRepository.add(new CommonPersonObject("", "", new HashMap<String, String>(), ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
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
    public void readAllcommonforFieldReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"_id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{0, 0, 0, 0});
        matrixCursor.addRow(new Object[]{1, 1, 1, 1});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        matrixCursor.moveToFirst();
        Assert.assertNotNull(commonRepository.readAllcommonForField(matrixCursor, ""));

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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
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
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        commonRepository.mergeDetails("caseID", new HashMap<String, String>());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void assertOnCreateCallsDatabaseExec() {
        String tablename = "";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.onCreate(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(6)).execSQL(Mockito.anyString());
    }


    @Test
    public void assertExecuteInsertStatementReturnsId() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        cursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});

        String tablename = "table";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        String baseEntityId = "1";
        String query =
                "SELECT  * FROM " + tablename + " WHERE base_entity_id = '" + baseEntityId + "'";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor);
        ContentValues cv = new ContentValues();
        cv.put("base_entity_id", "1");
        commonRepository.updateMasterRepository(repository);
        Long id = 0l;
        Assert.assertEquals(commonRepository.executeInsertStatement(cv, tablename), id);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insertWithOnConflict(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyInt());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(query, null);
    }

    @Test
    public void assertQueryTableReturnsCursor() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        cursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});

        String tablename = "table";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        String baseEntityId = "1";
        String query =
                "SELECT  * FROM " + tablename + " WHERE base_entity_id = '" + baseEntityId + "'";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor);
        commonRepository.updateMasterRepository(repository);
        org.junit.Assert.assertEquals(commonRepository.queryTable(query), cursor);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(query, null);
    }

    @Test
    public void assertCloseCaseCallsDatabaseExec() {
        String tablename = "table";
        String baseEntityId = "1";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.closeCase(baseEntityId, tablename);
        commonRepository.updateMasterRepository(repository);
        commonRepository.closeCase(baseEntityId, tablename);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertDeleteCaseCallsDatabaseExec() {
        String tablename = "table";
        String baseEntityId = "1";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Assert.assertEquals(commonRepository.deleteCase(baseEntityId, tablename), false);
        Mockito.when(sqliteDatabase.delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        commonRepository.updateMasterRepository(repository);
        Assert.assertEquals(commonRepository.deleteCase(baseEntityId, tablename), true);

    }

    @Test
    public void assertRawQueryReturnsMap() {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", "dd1", "0"});
        cursor.addRow(new Object[]{"caseID2", "relationalID2", "dd2", "0"});

        String tablename = "table";
        String[] tableColumns = new String[]{CommonRepository.Relational_Underscore_ID, CommonRepository.BASE_ENTITY_ID_COLUMN, ADDITIONALCOLUMN, CUSTOMRELATIONALID};
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        String baseEntityId = "1";
        String query =
                "SELECT  * FROM " + tablename + " WHERE base_entity_id = '" + baseEntityId + "'";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor);
        commonRepository.updateMasterRepository(repository);
        Assert.assertNotNull(commonRepository.rawQuery(query));
    }

    @Test
    public void assertPopulateSearchValuesByCaseIdReturnsContentValue() {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tableName = "common";
        String[] tableColumns = new String[]{};
        String[] tables = {"common", "common2"};
        String[] mainConditions = {"details"};
        String[] shortFields = {"id", "alerts.relationalid", "alerts.details", "is_closed"};

        String[] columns2 = new String[]{"id", "name"};
        MatrixCursor cursor2 = new MatrixCursor(columns2);
        cursor2.addRow(new Object[]{"caseID", "details"});


        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tableName, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);

        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(cursor);
        Mockito.when(commonFtsObject.getSearchFields(Mockito.anyString())).thenReturn(columns);
        Mockito.when(commonFtsObject.getTables()).thenReturn(tables);
        Mockito.when(commonFtsObject.getMainConditions(Mockito.anyString())).thenReturn(mainConditions);
        Mockito.when(commonFtsObject.getSortFields(Mockito.anyString())).thenReturn(shortFields);
        tableName = "common2";
        String query = "PRAGMA table_info(" + tableName + ")";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor2);
        Assert.assertNotNull(commonRepository.populateSearchValues("caseID"));
    }

    @Test
    public void assertPopulateSearchValuesReturnsContentBoolean() {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tableName = "common";
        String[] tableColumns = new String[]{};
        String[] tables = {"common"};
        String[] mainConditions = {"details"};
        String[] shortFields = {"id", "alerts.relationalid", "alerts.details", "is_closed"};

        String[] columns2 = new String[]{"id", "name", CommonFtsObject.phraseColumn};
        MatrixCursor cursor2 = new MatrixCursor(columns2);
        cursor2.addRow(new Object[]{"caseID", "details", "| hello| world"});


        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tableName, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);

        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(cursor);
        Mockito.when(commonFtsObject.getSearchFields(Mockito.anyString())).thenReturn(columns);
        Mockito.when(commonFtsObject.getTables()).thenReturn(tables);
        Mockito.when(commonFtsObject.getMainConditions(Mockito.anyString())).thenReturn(mainConditions);
        Mockito.when(commonFtsObject.getSortFields(Mockito.anyString())).thenReturn(shortFields);

        String query = "SELECT object_id, field FROM common_search WHERE  object_id = 'caseID'";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor2);
        Assert.assertEquals(commonRepository.populateSearchValues("caseID", "field", "value", new String[]{"details"}), false);
        Mockito.when(sqliteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        query = "SELECT object_id, phrase FROM common_search WHERE  object_id = 'caseID'";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor2);
        Assert.assertEquals(commonRepository.populateSearchValues("caseID", CommonFtsObject.phraseColumn, "hello_world", new String[]{"hello"}), true);
    }

}
