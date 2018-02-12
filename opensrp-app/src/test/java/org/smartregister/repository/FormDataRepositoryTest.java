package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.mock.SQLiteDatabaseMock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
@PrepareForTest({CoreLibrary.class})
public class FormDataRepositoryTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private Context context;
    private FormDataRepository formDataRepository;

    @Mock
    private SQLiteDatabaseMock sqLiteDatabase;
    @Mock
    private DristhiConfiguration dristhiConfiguration;
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private Repository repository;
    private static final String FORM_NAME_COLUMN = "formName";
    private static final String INSTANCE_COLUMN = "instance";
    private static final String VERSION_COLUMN = "version";
    private static final String SERVER_VERSION_COLUMN = "serverVersion";
    private static final String SYNC_STATUS_COLUMN = "syncStatus";
    private static final String FORM_DATA_DEFINITION_VERSION_COLUMN = "formDataDefinitionVersion";
    public static final String INSTANCE_ID_COLUMN = "instanceId";
    public static final String ENTITY_ID_COLUMN = "entityId";
    private static final String DETAILS_COLUMN_NAME = "details";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CoreLibrary.class);
        CoreLibrary.init(context);
        Context.bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        CommonRepositoryInformationHolder bt = new CommonRepositoryInformationHolder("BINDTYPENAME", new String[]{"A", "B"});
        Context.bindtypes.add(bt);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.configuration()).thenReturn(dristhiConfiguration);
        PowerMockito.when(dristhiConfiguration.appName()).thenReturn("NULL");
        PowerMockito.when(context.commonrepository(Mockito.anyString())).thenReturn(Mockito.mock(CommonRepository.class));
        formDataRepository = new FormDataRepository();
        formDataRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getCursor());
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getCursor());
    }

    @Test
    public void assertqueryUniqueResult() {
        Assert.assertNotNull(formDataRepository.queryUniqueResult("sql"));
    }

    @Test
    public void assertqueryList() {
        Assert.assertNotNull(formDataRepository.queryList("sql"));
    }

    @Test
    public void assertqueryListWithdetails() {
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getCursor2());
        Assert.assertNotNull(formDataRepository.queryList("sql"));
    }

    @Test
    public void assertsaveFormSubmission() {
        Assert.assertEquals(formDataRepository.saveFormSubmission(getJsonObject(), "data", "1.0"), "1");
    }

    @Test
    public void assertsaveFormSubmissionCallsDatabaseInsert() {
        formDataRepository.saveFormSubmission(getFormSubmission());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertfetchFromSubmission() {
        Assert.assertNotNull(formDataRepository.fetchFromSubmission(""));
    }

    @Test
    public void assertgetPendingFormSubmissions() {
        Assert.assertNotNull(formDataRepository.getPendingFormSubmissions());
    }

    @Test
    public void assertmarkFormSubmissionsAsSynced() {
        List<FormSubmission> list = new ArrayList<FormSubmission>();
        list.add(getFormSubmission());
        formDataRepository.markFormSubmissionsAsSynced(list);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertsubmissionExists() {
        Assert.assertEquals(formDataRepository.submissionExists("1"), true);
    }

    @Test
    public void assertsaveEntity() {

        Assert.assertEquals(formDataRepository.saveEntity(EligibleCoupleRepository.EC_TABLE_NAME, "{\"id\":\"1\"}"), "1");
    }

    @Test
    public void assertgetMapFromSQLQuery() {
        Assert.assertNotNull(formDataRepository.getMapFromSQLQuery(""));
    }

    @Test
    public void assertupdateServerVersion() {
        formDataRepository.updateServerVersion("", "");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertOnCreateCallsDatabaseExec() {
        formDataRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    public String getJsonObject() {
        String object = "{\"serverVersion\":\"1.1\", \"formDataDefinitionVersion\":\"0.1\", \"instanceId\":\"1\", \"instance\":\"3\", \"formName\":\"FORM\", \"entityId\":\"2\", \"version\":\"1.0\", \"syncStatus\":\"PENDING\"}";
        return object;
    }

    public FormSubmission getFormSubmission() {
        return new FormSubmission("1", "2", "FORM", getJsonObject(), "1.0", SyncStatus.PENDING, "1");
    }

    public MatrixCursor getCursor() {
        String[] columns = {INSTANCE_ID_COLUMN,
                ENTITY_ID_COLUMN, FORM_NAME_COLUMN, INSTANCE_COLUMN, VERSION_COLUMN,
                SERVER_VERSION_COLUMN, FORM_DATA_DEFINITION_VERSION_COLUMN, SYNC_STATUS_COLUMN};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"1", "2", "FORM", getJsonObject(), "1.0", "1.1", "0.1", SyncStatus.PENDING.value()});
        return cursor;
    }

    public MatrixCursor getCursor2() {
        String[] columns = {INSTANCE_ID_COLUMN,
                ENTITY_ID_COLUMN, FORM_NAME_COLUMN, INSTANCE_COLUMN, VERSION_COLUMN,
                SERVER_VERSION_COLUMN, FORM_DATA_DEFINITION_VERSION_COLUMN, SYNC_STATUS_COLUMN, DETAILS_COLUMN_NAME};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"1", "2", "FORM", getJsonObject(), "1.0", "1.1", "0.1", SyncStatus.PENDING.value(), "{\"details\":\"form_submission_failed\"}"});
        return cursor;
    }

    @Test
    public void assertFormDataRepositoryInitiaization() throws Exception {
        org.junit.Assert.assertNotNull(formDataRepository);
    }
}
