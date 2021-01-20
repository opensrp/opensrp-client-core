package org.smartregister.repository.dao;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ibm.fhir.model.resource.QuestionnaireResponse;
import com.ibm.fhir.model.resource.Task;
import com.ibm.fhir.model.type.Uri;
import com.ibm.fhir.model.type.code.QuestionnaireResponseStatus;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.UUID;

import static com.ibm.fhir.model.type.code.TaskStatus.READY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED;
import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED_EVENT;
import static org.smartregister.domain.Task.TaskStatus.ARCHIVED;
import static org.smartregister.domain.Task.TaskStatus.CANCELLED;
import static org.smartregister.repository.TaskRepositoryTest.getCursor;
import static org.smartregister.repository.TaskRepositoryTest.javaTimeFormater;

/**
 * Created by samuelgithengi on 9/3/20.
 */

public class TaskDaoImplTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskDaoImpl taskDao;

    @Mock
    private Repository repository;
    @Mock
    private TaskNotesRepository taskNotesRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;


    @Before
    public void setUp() {

        taskDao = new TaskDaoImpl(taskNotesRepository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }


    @Test
    public void testFindTasksForEntity() {
        String query = "SELECT * FROM task WHERE plan_id=? AND for =? AND status  NOT IN (?,?)";
        String[] params = new String[]{"IRS_2018_S1", "location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", CANCELLED.name(), ARCHIVED.name()};
        when(sqLiteDatabase.rawQuery(query, params)).thenReturn(getCursor());

        List<Task> allTasks = taskDao.findTasksForEntity("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "IRS_2018_S1");
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(1, allTasks.size());
        Task task = allTasks.iterator().next();

        assertEquals("tsk11231jh22", task.getIdentifier().get(0).getValue().getValue());
        assertEquals("2018_IRS-3734", task.getGroupIdentifier().getValue().getValue());
        assertEquals(READY, task.getStatus());
        assertEquals("Not Visited", task.getBusinessStatus().getText().getValue());
        assertEquals("IRS", task.getCode().getText().getValue());
        assertEquals("Spray House", task.getDescription().getValue());
        assertEquals("IRS Visit", task.getFocus().getReference().getValue());
        assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getFor().getReference().getValue());
        assertEquals("2018-11-10T2200", javaTimeFormater.format(task.getExecutionPeriod().getStart().getValue()));
        assertNull(task.getExecutionPeriod().getEnd());
        assertEquals("2018-10-31T0700", javaTimeFormater.format(task.getAuthoredOn().getValue()));
        assertEquals("2018-10-31T0700", javaTimeFormater.format(task.getLastModified().getValue()));
        assertEquals("demouser", task.getOwner().getReference().getValue());

    }

    @Test
    public void testFindTasksByJurisdiction() {
        String jurisdictionId = "jurisdiction-id";

        String query = "SELECT * FROM task WHERE group_id = ?";
        when(sqLiteDatabase.rawQuery(query, new String[]{jurisdictionId})).thenReturn(getCursor());
        taskDao = Mockito.spy(taskDao);

        // Call the method under test
        List<Task> allTasks = taskDao.findTasksByJurisdiction(jurisdictionId);

        // Perform verifications and assertions
        verify(taskDao).getTasksByJurisdiction(jurisdictionId);
        assertEquals(1, allTasks.size());
    }

    @Test
    public void testUpdateTaskShouldInvokeExpectedMethods() {
        taskDao = Mockito.spy(taskDao);

        LocalBroadcastManager localBroadcastManager = mock(LocalBroadcastManager.class);

        doReturn(false).when(localBroadcastManager).sendBroadcast(any(Intent.class));

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", localBroadcastManager);

        org.smartregister.domain.Task task = new org.smartregister.domain.Task();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPriority(org.smartregister.domain.Task.TaskPriority.ROUTINE);
        task.setStatus(ARCHIVED);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        taskDao.updateTask(task);

        verify(taskDao).addOrUpdate(any(org.smartregister.domain.Task.class), eq(true));

        verify(localBroadcastManager).sendBroadcast(intentArgumentCaptor.capture());

        Intent intent = intentArgumentCaptor.getValue();

        assertNotNull(intent);

        assertEquals(intent.getAction(), TASK_GENERATED_EVENT);

        assertTrue(intent.hasExtra(TASK_GENERATED));

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", null);
    }

    @Test
    public void testSaveTaskShouldInvokeExpectedMethods() {
        taskDao = Mockito.spy(taskDao);

        LocalBroadcastManager localBroadcastManager = mock(LocalBroadcastManager.class);

        doReturn(false).when(localBroadcastManager).sendBroadcast(any(Intent.class));

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", localBroadcastManager);

        org.smartregister.domain.Task task = new org.smartregister.domain.Task();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPriority(org.smartregister.domain.Task.TaskPriority.ROUTINE);
        task.setStatus(ARCHIVED);

        String locationId = "b8a7998c";

        QuestionnaireResponse questionnaireResponse = QuestionnaireResponse.builder()
                .item(QuestionnaireResponse.Item.builder()
                        .linkId(com.ibm.fhir.model.type.String.of("location_id"))
                        .definition(Uri.of("details"))
                        .answer(QuestionnaireResponse.Item.Answer.builder().value(com.ibm.fhir.model.type.String.of(locationId)).build())
                        .build())
                .status(QuestionnaireResponseStatus.COMPLETED)
                .build();

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        ArgumentCaptor<org.smartregister.domain.Task> taskArgumentCaptor = ArgumentCaptor.forClass(org.smartregister.domain.Task.class);

        taskDao.saveTask(task, questionnaireResponse);

        verify(taskDao).addOrUpdate(taskArgumentCaptor.capture());

        verify(localBroadcastManager).sendBroadcast(intentArgumentCaptor.capture());

        org.smartregister.domain.Task resultTask = taskArgumentCaptor.getValue();

        assertNotNull(resultTask);

        assertEquals(locationId, resultTask.getStructureId());

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", null);

    }
}
