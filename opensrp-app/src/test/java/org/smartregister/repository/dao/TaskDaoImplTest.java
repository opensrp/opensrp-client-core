package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.Task;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.ParseException;
import java.util.List;

import static com.ibm.fhir.model.type.code.TaskStatus.READY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.Task.TaskStatus.ARCHIVED;
import static org.smartregister.domain.Task.TaskStatus.CANCELLED;
import static org.smartregister.repository.TaskRepositoryTest.getCursor;
import static org.smartregister.repository.TaskRepositoryTest.javaTimeFormater;

/**
 * Created by samuelgithengi on 9/3/20.
 */
public class TaskDaoImplTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();


    private TaskDaoImpl taskRepository;

    @Mock
    private Repository repository;
    @Mock
    private TaskNotesRepository taskNotesRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;


    @Before
    public void setUp() {

        taskRepository = new TaskDaoImpl(taskNotesRepository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }


    @Test
    public void testFindTasksForEntity() throws ParseException {
        String query = "SELECT * FROM task WHERE plan_id=? AND for =? AND status  NOT IN (?,?)";
        String[] params = new String[]{"IRS_2018_S1", "location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", CANCELLED.name(), ARCHIVED.name()};
        when(sqLiteDatabase.rawQuery(query, params)).thenReturn(getCursor());

        List<Task> allTasks = taskRepository.findTasksForEntity("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "IRS_2018_S1");
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(1, allTasks.size());
        com.ibm.fhir.model.resource.Task task = allTasks.iterator().next();

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
}
