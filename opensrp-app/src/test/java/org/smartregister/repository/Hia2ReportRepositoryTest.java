package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class})
public class Hia2ReportRepositoryTest extends BaseUnitTest {
    private String baseEntityId = "baseEntityId";
    private String syncStatus = "syncStatus";

    @InjectMocks
    private Hia2ReportRepository hia2ReportRepository;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        hia2ReportRepository = new Hia2ReportRepository(repository);
    }

    @Test
    public void assertGetUnSyncedReportsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(hia2ReportRepository.getUnSyncedReports(1));
    }

    @Test
    public void assertGetUnValidatedReportFormSubmissionIdsReturnsList() throws Exception {
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        org.junit.Assert.assertNotNull(hia2ReportRepository.getUnValidatedReportFormSubmissionIds(1));
    }

    @Test
    public void assertAddReportCallsDatabaseInsertAndUpdate() throws Exception {
        String jsonReport = "{\"reportType\":\"reportType\", \"formSubmissionId\":\"formSubmissionId\"}";
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(getCursorSyncStatus());
        hia2ReportRepository.addReport(new JSONObject(jsonReport));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(null);
        hia2ReportRepository.addReport(new JSONObject(jsonReport));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));
    }

    @Test
    public void assertmarkReportsAsSyncedCallsDatabaseUpdate() throws Exception {
        String jsonReport = "{\"reportType\":\"reportType\", \"formSubmissionId\":\"formSubmissionId\"}";
        List<JSONObject> reports = new ArrayList<>();
        reports.add(new JSONObject(jsonReport));
        hia2ReportRepository.markReportsAsSynced(reports);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));
    }

    public MatrixCursor getCursorSyncStatus() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{"{\"json\":\"data\"}", syncStatus});
        return matrixCursor;
    }

}
