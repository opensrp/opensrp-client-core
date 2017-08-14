package org.smartregister.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.FormDefinitionVersion;
import org.smartregister.domain.Response;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.FormsVersionRepository;
import org.smartregister.util.EasyMap;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.nothingFetched;
import static org.smartregister.domain.ResponseStatus.success;

/**
 * Created by Dimas Ciputra on 3/31/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class AllFormVersionSyncServiceTest {

    @Mock
    HTTPAgent httpAgent;
    @Mock
    DristhiConfiguration configuration;
    @Mock
    FormsVersionRepository formsVersionRepository;
    @Mock
    FormPathService formPathService;
    @Mock
    File file;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private AllFormVersionSyncService service;
    private List<FormDefinitionVersion> expectedFormDefinitionVersion;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = Mockito.spy(new AllFormVersionSyncService(httpAgent,
                                                            configuration,
                                                            formsVersionRepository));
        expectedFormDefinitionVersion = asList(new FormDefinitionVersion("form_ec", "ec_dir", "2"));
        when(configuration.dristhiBaseURL()).thenReturn("http://opensrp_base_url");
    }

    @Test
    public void shouldNotDownloadIfThereIsNoPendingForms() throws Exception {
        when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                Collections.<FormDefinitionVersion>emptyList());

        DownloadStatus status = service.downloadAllPendingFormFromServer();

        assertEquals(status, DownloadStatus.nothingDownloaded);
        verify(formsVersionRepository).getAllFormWithSyncStatus(SyncStatus.PENDING);
        verifyNoMoreInteractions(formsVersionRepository);
        verifyZeroInteractions(httpAgent);
    }

    @Test
    public void shouldDownloadIfThereIsAPendingForms() throws Exception {
        when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                this.expectedFormDefinitionVersion);
        when(httpAgent.downloadFromUrl("http://opensrp_base_url/form/form-files?formDirName=ec_dir",
                                       "ec_dir.zip")).thenReturn(DownloadStatus.downloaded);

        DownloadStatus status = service.downloadAllPendingFormFromServer();

        assertEquals(status, DownloadStatus.downloaded);
        verify(formsVersionRepository).getAllFormWithSyncStatus(SyncStatus.PENDING);
        verify(httpAgent).downloadFromUrl(
                "http://opensrp_base_url/form/form-files?formDirName=ec_dir",
                "ec_dir.zip");
    }

    @Test
    public void shouldReturnWhenThereIsNoFileInFolder() throws Exception {
        when(service.listFormFiles()).thenReturn(null);

        service.verifyFormsInFolder();

        verify(formsVersionRepository).deleteAll();
        verifyNoMoreInteractions(formsVersionRepository);
        verifyNoMoreInteractions(file);
    }

    @Test
    public void shouldUpdateRepoWhenThereIsNoFileInFolder() throws Exception {
        final File file1 = temporaryFolder.newFile("ec_registration");

        File[] formFiles = new File[]{file1};

        Map<String, String> repoFile1 = EasyMap.create("formName", "ec_registration")
                                               .put("formDirName", "ec_registration")
                                               .put("formDataDefinitionVersion", "1")
                                               .put("id", "1")
                                               .put("syncStatus", "SYNCED")
                                               .map();

        Map<String, String> repoFile2 = EasyMap.create("formName", "anc_registration")
                                               .put("formDirName", "anc_registration")
                                               .put("formDataDefinitionVersion", "1")
                                               .put("id", "1")
                                               .put("syncStatus", "SYNCED")
                                               .map();

        when(service.listFormFiles()).thenReturn(formFiles);
        when(formsVersionRepository.getAllFormWithSyncStatusAsMap(SyncStatus.SYNCED)).thenReturn(
                asList(repoFile1, repoFile2));

        service.verifyFormsInFolder();

        verify(formsVersionRepository).updateSyncStatus("anc_registration", SyncStatus.PENDING);
    }

    @Test
    public void shouldAddToRepoWhenThereIsFilesInFolder() throws Exception {
        File file1 = temporaryFolder.newFile("ec_registration");
        File file2 = temporaryFolder.newFile("anc_registration");

        File[] formFiles = new File[]{file1, file2};

        FormDefinitionVersion f1 = new FormDefinitionVersion("ec_registration",
                                                             "ec_registration",
                                                             "1");
        FormDefinitionVersion f2 = new FormDefinitionVersion("anc_registration",
                                                             "anc_registration",
                                                             "1");

        when(service.listFormFiles()).thenReturn(formFiles);
        doReturn(f1).when(service).getFormDefinitionFromFile(file1);
        doReturn(f2).when(service).getFormDefinitionFromFile(file2);

        when(formsVersionRepository.formExists("ec_registration")).thenReturn(false);
        when(formsVersionRepository.formExists("anc_registration")).thenReturn(false);

        service.verifyFormsInFolder();

        verify(formsVersionRepository).formExists("ec_registration");
        verify(formsVersionRepository).formExists("anc_registration");
        verify(formsVersionRepository).addFormVersionFromObject(f1);
        verify(formsVersionRepository).addFormVersionFromObject(f2);
    }

    @Test
    public void shouldUpdateVersionIfThereIsNewerVersion() throws Exception {
        String jsonObject = "{\"formVersions\" : [{\"formName\": \"EC_ENGKAN\", \"formDirName\": "
                + "\"ec_dir\", \"formDataDefinitionVersion\": \"3\"}] }";
        when(httpAgent.fetch("http://opensrp_base_url/form/latest-form-versions")).thenReturn(
                new Response<String>(
                    success,
                    jsonObject));

        List<FormDefinitionVersion> repoForm = asList(new FormDefinitionVersion("form_ec",
                                                                                "ec_dir",
                                                                                "1"));

        when(formsVersionRepository.formExists("ec_dir")).thenReturn(true);
        when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                repoForm);
        when(formsVersionRepository.getFormByFormDirName("ec_dir")).thenReturn(
                    new FormDefinitionVersion(
                    "EC_ENGAN",
                    "ec_dir",
                    "1"));
        when(formsVersionRepository.getVersion("ec_dir")).thenReturn("1");

        FetchStatus fetchStatus = service.pullFormDefinitionFromServer();

        assertEquals(fetched, fetchStatus);

        verify(httpAgent).fetch("http://opensrp_base_url/form/latest-form-versions");
        verify(formsVersionRepository).updateFormName("ec_dir", "EC_ENGKAN");
        verify(formsVersionRepository).formExists("ec_dir");
        verify(formsVersionRepository).updateServerVersion("ec_dir", "3");
        verify(formsVersionRepository).updateSyncStatus("ec_dir", SyncStatus.PENDING);
    }

    @Test
    public void shouldNotUpdateIfLocalFormIsTheLatestVersion() throws Exception {
        String jsonObject = "{\"formVersions\" : [{\"formName\": \"EC_ENGKAN\", \"formDirName\":"
                + " \"ec_dir\", \"formDataDefinitionVersion\": \"2\"}] }";
        when(httpAgent.fetch("http://opensrp_base_url/form/latest-form-versions")).thenReturn(
                new Response<String>(
                    success,
                    jsonObject));

        List<FormDefinitionVersion> repoForm = asList(new FormDefinitionVersion("form_ec",
                                                                                "ec_dir",
                                                                                "3"));

        when(formsVersionRepository.formExists("ec_dir")).thenReturn(true);
        when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                repoForm);
        when(formsVersionRepository.getFormByFormDirName("ec_dir")).thenReturn(
                new FormDefinitionVersion(
                    "EC_ENGAN",
                    "ec_dir",
                    "3"));
        when(formsVersionRepository.getVersion("ec_dir")).thenReturn("3");

        FetchStatus fetchStatus = service.pullFormDefinitionFromServer();

        assertEquals(nothingFetched, fetchStatus);
    }
}
