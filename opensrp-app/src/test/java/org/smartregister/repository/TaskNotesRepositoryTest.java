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
import org.smartregister.domain.Note;
import org.smartregister.domain.Task;
import org.smartregister.util.DateTimeTypeConverter;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.Task.TaskStatus.READY;
import static org.smartregister.repository.TaskNotesRepository.TASK_NOTES_TABLE;
import static org.smartregister.repository.TaskRepository.TASK_TABLE;

/**
 * Created by samuelgithengi on 11/26/18.
 */

@RunWith(RobolectricTestRunner.class)
public class TaskNotesRepositoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskNotesRepository taskNotesRepository;

    @Mock
    private static Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    ArgumentCaptor<String[]> argsCaptor;

    private String taskJson = "{\"identifier\":\"tsk11231jh22\",\"campaignIdentifier\":\"IRS_2018_S1\",\"groupIdentifier\":\"2018_IRS-3734\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T0700\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}],\"serverVersion\":0}";

    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .serializeNulls().create();

    protected static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HHmm");

    @Before
    public void setUp() {
        taskNotesRepository = new TaskNotesRepository(repository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {
        Note note = new Note();
        note.setText("Should be completed by End of November");
        Long now = System.currentTimeMillis();
        note.setTime(new DateTime(now));
        note.setAuthorString("jdoe");
        taskNotesRepository.addOrUpdate(note, "task22132");

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(TASK_NOTES_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(4, contentValues.size());

        assertEquals("task22132", contentValues.getAsString("task_id"));
        assertEquals("Should be completed by End of November", contentValues.getAsString("text"));
        assertEquals("jdoe", contentValues.getAsString("author"));
        assertEquals(now, contentValues.getAsLong("time"));


    }


    @Test
    public void tesGetTasksAllTasks() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM task_note WHERE task_id =?",
                new String[]{"task22132"})).thenReturn(getCursor());
        List<Note> notes = taskNotesRepository.getNotesByTask("task22132");
        verify(sqLiteDatabase).rawQuery("SELECT * FROM task_note WHERE task_id =?", new String[]{"task22132"});
        assertEquals(1, notes.size());
        assertEquals("Should be completed by End of November", notes.get(0).getText());
        assertEquals("jdoe", notes.get(0).getAuthorString());
        assertEquals(1543232476345l, notes.get(0).getTime().getMillis());


    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(TaskNotesRepository.COLUMNS);
        cursor.addRow(new Object[]{"task22132", "jdoe", "1543232476345", "Should be completed by End of November"});
        return cursor;
    }


}
