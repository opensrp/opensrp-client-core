package org.smartregister.sync.intent;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.sync.helper.TaskServiceHelper;

/**
 * Created by Vincent Karuri on 30/03/2021
 */
public class SyncTaskIntentServiceTest extends BaseUnitTest {

    @Test
    public void testOnHandleIntentShouldSyncTasks() {
        SyncTaskIntentWorker syncTaskIntentService = new SyncTaskIntentWorker();
        TaskServiceHelper taskServiceHelper = Mockito.mock(TaskServiceHelper.class);
        Whitebox.setInternalState(syncTaskIntentService, "taskServiceHelper", taskServiceHelper);
        try {
            syncTaskIntentService.onRunWork();
        } catch (java.net.SocketException e) {
            e.printStackTrace();
        }
        Mockito.verify(taskServiceHelper).syncTasks();
    }
}