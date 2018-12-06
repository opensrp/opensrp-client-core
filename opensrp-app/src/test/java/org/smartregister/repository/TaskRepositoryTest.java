package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.domain.Task;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.Task.TaskStatus.READY;
import static org.smartregister.repository.TaskRepository.TASK_TABLE;

/**
 * Created by samuelgithengi on 11/26/18.
 */

@RunWith(RobolectricTestRunner.class)
public class TaskRepositoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskRepository taskRepository;

    @Mock
    private static Repository repository;
    @Mock
    private TaskNotesRepository taskNotesRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<String[]> argsCaptor;

    private String taskJson = "{\"identifier\":\"tsk11231jh22\",\"campaignIdentifier\":\"IRS_2018_S1\",\"groupIdentifier\":\"2018_IRS-3734\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T0700\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}],\"serverVersion\":0}";

    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .serializeNulls().create();

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HHmm");

    @Before
    public void setUp() {
        taskRepository = new TaskRepository(repository, taskNotesRepository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        Task task = gson.fromJson(taskJson, Task.class);
        taskRepository.addOrUpdate(task);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(TASK_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(16, contentValues.size());

        assertEquals("tsk11231jh22", contentValues.getAsString("_id"));
        assertEquals("IRS_2018_S1", contentValues.getAsString("campaign_id"));
        assertEquals("2018_IRS-3734", contentValues.getAsString("group_id"));

        verify(taskNotesRepository).addOrUpdate(task.getNotes().get(0), task.getIdentifier());


    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowException() {

        Task task = new Task();
        taskRepository.addOrUpdate(task);

    }

    @Test
    public void testGetTasksByCampaignAndGroup() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM task WHERE campaign_id=? AND group_id =?",
                new String[]{"IRS_2018_S1", "2018_IRS-3734"})).thenReturn(getCursor());
        Map<String, Task> allTasks = taskRepository.getTasksByCampaignAndGroup("IRS_2018_S1", "2018_IRS-3734");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM task WHERE campaign_id=? AND group_id =?", stringArgumentCaptor.getValue());

        assertEquals("IRS_2018_S1", argsCaptor.getValue()[0]);
        assertEquals("2018_IRS-3734", argsCaptor.getValue()[1]);

        assertEquals(1, allTasks.size());
        Task task = allTasks.get("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc");

        assertEquals("tsk11231jh22", task.getIdentifier());
        assertEquals("2018_IRS-3734", task.getGroupIdentifier());
        assertEquals(READY, task.getStatus());
        assertEquals("Not Visited", task.getBusinessStatus());
        assertEquals(3, task.getPriority());
        assertEquals("IRS", task.getCode());
        assertEquals("Spray House", task.getDescription());
        assertEquals("IRS Visit", task.getFocus());
        assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
        assertEquals("2018-11-10T2200", task.getExecutionStartDate().toString(formatter));
        assertNull(task.getExecutionEndDate());
        assertEquals("2018-10-31T0700", task.getAuthoredOn().toString(formatter));
        assertEquals("2018-10-31T0700", task.getLastModified().toString(formatter));
        assertEquals("demouser", task.getOwner());


    }

    @Test
    public void testGetTaskByIdentifier() {

        when(sqLiteDatabase.rawQuery("SELECT * FROM task WHERE _id =?", new String[]{"tsk11231jh22"})).thenReturn(getCursor());
        Task task = taskRepository.getTaskByIdentifier("tsk11231jh22");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());
        assertEquals("SELECT * FROM task WHERE _id =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("tsk11231jh22", argsCaptor.getValue()[0]);


        assertEquals("tsk11231jh22", task.getIdentifier());
        assertEquals("2018_IRS-3734", task.getGroupIdentifier());
        assertEquals(READY, task.getStatus());
        assertEquals("Not Visited", task.getBusinessStatus());
        assertEquals(3, task.getPriority());
        assertEquals("IRS", task.getCode());
        assertEquals("Spray House", task.getDescription());
        assertEquals("IRS Visit", task.getFocus());
        assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
        assertEquals("2018-11-10T2200", task.getExecutionStartDate().toString(formatter));
        assertNull(task.getExecutionEndDate());
        assertEquals("2018-10-31T0700", task.getAuthoredOn().toString(formatter));
        assertEquals("2018-10-31T0700", task.getLastModified().toString(formatter));
        assertEquals("demouser", task.getOwner());


    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(TaskRepository.COLUMNS);
        Task task = gson.fromJson(taskJson, Task.class);

        cursor.addRow(new Object[]{task.getIdentifier(), task.getCampaignIdentifier(), task.getGroupIdentifier(),
                task.getStatus().name(), task.getBusinessStatus(), task.getPriority(), task.getCode(),
                task.getDescription(), task.getFocus(), task.getForEntity(),
                task.getExecutionStartDate().getMillis(),
                null,
                task.getAuthoredOn().getMillis(), task.getLastModified().getMillis(),
                task.getOwner(), task.getServerVersion()});
        return cursor;
    }


}
