package org.smartregister.sync.helper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.repository.TaskRepository;

import static org.mockito.Mockito.verify;

/**
 * Created by Richard Kareko on 6/23/20.
 */

public class TaskServiceHelperTest extends BaseRobolectricUnitTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceHelper taskServiceHelper = Mockito.spy(TaskServiceHelper.getInstance());

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(taskServiceHelper, "taskRepository", taskRepository);
    }

    @Test
    public void testSyncTasks() {
        taskServiceHelper.syncTasks();
        verify(taskServiceHelper).syncCreatedTaskToServer();
        verify(taskServiceHelper).syncTaskStatusToServer();
    }
}
