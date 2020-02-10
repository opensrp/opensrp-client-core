package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.Task;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.SyncServiceHelper;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cozej4 on 2020-02-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class SyncClientEventsPerTaskIntentService extends BaseSyncIntentService {
    private static final String TAG = SyncClientEventsPerTaskIntentService.class.getSimpleName();
    private TaskRepository taskRepository;

    public SyncClientEventsPerTaskIntentService() {
        super(TAG);
        taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        SyncServiceHelper syncServiceHelper = SyncServiceHelper.getInstance();
        syncServiceHelper.fetchMissingEventsRetry(0,taskRepository.getTasksWithMissingClientsAndEvents());
    }


}