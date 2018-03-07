package org.smartregister.service;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.util.EasyMap;
import org.smartregister.util.FormSubmissionBuilder;

import java.util.Arrays;
import java.util.List;

public class FormSubmissionServiceTest {
    @Mock
    private ZiggyService ziggyService;
    @Mock
    private AllSettings allSettings;
    @Mock
    private FormDataRepository formDataRepository;

    private FormSubmissionService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new FormSubmissionService(ziggyService, formDataRepository, allSettings);
    }

    @Test
    public void shouldDelegateProcessingToZiggyServiceAndMarkAsSynced() throws Exception {
        List<FormSubmission> submissions = Arrays.asList(FormSubmissionBuilder.create().withInstanceId("instance id 1").withVersion("122").build(),
                FormSubmissionBuilder.create().withInstanceId("instance id 2").withVersion("123").build());

        service.processSubmissions(submissions);

        String paramsForFirstSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 1")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("version", "122")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .map());
        String paramsForSecondSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 2")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .put("version", "123")
                        .map());
        InOrder inOrder = Mockito.inOrder(ziggyService, allSettings, formDataRepository);
        inOrder.verify(ziggyService).saveForm(paramsForFirstSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 1", "0");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("0");
        inOrder.verify(ziggyService).saveForm(paramsForSecondSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 2", "0");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("0");
    }

    @Test
    public void shouldNotDelegateProcessingToZiggyServiceForProcessedSubmissions() throws Exception {
        FormSubmission firstFormSubmission = FormSubmissionBuilder.create().withInstanceId("instance id 1").withVersion("122").build();
        FormSubmission secondFormSubmission = FormSubmissionBuilder.create().withInstanceId("instance id 2").withVersion("123").withServerVersion("1").build();
        List<FormSubmission> submissions = Arrays.asList(firstFormSubmission, secondFormSubmission);
        Mockito.when(formDataRepository.submissionExists("instance id 1")).thenReturn(true);
        Mockito.when(formDataRepository.submissionExists("instance id 2")).thenReturn(false);

        service.processSubmissions(submissions);

        String paramsForFirstSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 1")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("version", "122")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .map());
        String paramsForSecondSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 2")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("version", "123")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .map());
        InOrder inOrder = Mockito.inOrder(ziggyService, allSettings, formDataRepository);
        inOrder.verify(ziggyService, Mockito.times(0)).saveForm(paramsForFirstSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 1", "0");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("0");
        inOrder.verify(ziggyService).saveForm(paramsForSecondSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 2", "1");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("1");
    }
}
