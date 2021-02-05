package org.smartregister.sync.helper;

import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Task;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.TaskRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Richard Kareko on 2/5/21.
 */

public class TaskServiceProcessor {

    protected static TaskServiceProcessor instance;
    private TaskRepository taskRepository;
    private EventClientRepository eventClientRepository;

    public static TaskServiceProcessor getInstance() {
        if (instance == null) {
            instance = new TaskServiceProcessor(CoreLibrary.getInstance().context().getTaskRepository(),
                    CoreLibrary.getInstance().context().getEventClientRepository() );
        }
        return instance;
    }

    public  TaskServiceProcessor(TaskRepository taskRepository, EventClientRepository eventClientRepository) {
        this.taskRepository = taskRepository;
        this.eventClientRepository = eventClientRepository;
    }

    public void ProcessDuplicateTasks() {
        List<String> entityIds = taskRepository.getEntityIdsWithDuplicateTasks();
        for (String entityId: entityIds) {
            processTasks(taskRepository.getDuplicateTasksForEntity(entityId));
        }
    }

    protected void processTasks(Set<Task> duplicateTasks) {
        Task localTask = null;
        Task serverTask = null;


        populateTaskDetails(serverTask, localTask);

    }

    protected void populateTaskDetails(Task serverTask, Task localTask) {
        serverTask.setAuthoredOn(localTask.getAuthoredOn());
        serverTask.setSyncStatus(BaseRepository.TYPE_Unsynced);
        serverTask.setStatus(localTask.getStatus());
        serverTask.setSyncStatus(localTask.getSyncStatus());
        serverTask.setLastModified(new DateTime());
        serverTask.setBusinessStatus(localTask.getBusinessStatus());
        serverTask.setCode(localTask.getCode());
        serverTask.setDescription(localTask.getDescription());
        serverTask.setExecutionPeriod(localTask.getExecutionPeriod());
        serverTask.setFocus(localTask.getFocus());
        serverTask.setForEntity(localTask.getForEntity());
        serverTask.setGroupIdentifier(localTask.getIdentifier());
        serverTask.setLocation(localTask.getLocation());
        serverTask.setIdentifier(localTask.getIdentifier());
        serverTask.setNotes(localTask.getNotes());
        serverTask.setOwner(localTask.getOwner());
        serverTask.setPlanIdentifier(localTask.getPlanIdentifier());
        serverTask.setPriority(localTask.getPriority());
        serverTask.setReasonReference(localTask.getReasonReference());
        serverTask.setRequester(localTask.getRequester());
        serverTask.setRestriction(localTask.getRestriction());
        serverTask.setStructureId(localTask.getStructureId());
    }

}
