package org.smartregister.sync.helper;

import android.content.Context;

import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.TaskRepository;

/**
 * Created by Richard Kareko on 6/23/20.
 */

class TaskServiceHelperTest extends BaseUnitTest {

    @Mock
    private TaskRepository taskRepository;

    private Context context = RuntimeEnvironment.application;

    private TaskServiceHelper taskServiceHelper;
}
