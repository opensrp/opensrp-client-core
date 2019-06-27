package org.smartregister.repository;

import android.content.ContentValues;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.domain.Note;
import org.smartregister.domain.Task;
import org.smartregister.domain.TaskUpdate;
import org.smartregister.domain.db.Client;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.smartregister.domain.Task.TaskStatus;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class TaskRepository extends BaseRepository {

    private static final String ID = "_id";
    private static final String PLAN_ID = "plan_id";
    private static final String GROUP_ID = "group_id";
    private static final String STATUS = "status";
    private static final String BUSINESS_STATUS = "business_status";

    private static final String PRIORITY = "priority";
    private static final String CODE = "code";
    private static final String DESCRIPTION = "description";
    private static final String FOCUS = "focus";
    private static final String FOR = "for";

    private static final String START = "start";
    private static final String END = "end";

    private static final String AUTHORED_ON = "authored_on";
    private static final String LAST_MODIFIED = "last_modified";
    private static final String OWNER = "owner";
    private static final String SYNC_STATUS = "sync_status";
    private static final String SERVER_VERSION = "server_version";
    private static final String STRUCTURE_ID = "structure_id";
    private static final String REASON_REFERENCE = "reason_reference";

    private TaskNotesRepository taskNotesRepository;

    protected static final String[] COLUMNS = {ID, PLAN_ID, GROUP_ID, STATUS, BUSINESS_STATUS, PRIORITY, CODE, DESCRIPTION, FOCUS, FOR, START, END, AUTHORED_ON, LAST_MODIFIED, OWNER, SYNC_STATUS, SERVER_VERSION, STRUCTURE_ID, REASON_REFERENCE};

    protected static final String TASK_TABLE = "task";

    private static final String CREATE_TASK_TABLE =
            "CREATE TABLE " + TASK_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    PLAN_ID + " VARCHAR NOT NULL, " +
                    GROUP_ID + " VARCHAR NOT NULL, " +
                    STATUS + " VARCHAR  NOT NULL, " +
                    BUSINESS_STATUS + " VARCHAR,  " +
                    PRIORITY + " INTEGER,  " +
                    CODE + " VARCHAR , " +
                    DESCRIPTION + " VARCHAR , " +
                    FOCUS + " VARCHAR , " +
                    FOR + " VARCHAR NOT NULL, " +
                    START + " INTEGER , " +
                    END + " INTEGER , " +
                    AUTHORED_ON + " INTEGER NOT NULL, " +
                    LAST_MODIFIED + " INTEGER NOT NULL, " +
                    OWNER + " VARCHAR NOT NULL, " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    SERVER_VERSION + " INTEGER, " +
                    STRUCTURE_ID + " VARCHAR, " +
                    REASON_REFERENCE + " VARCHAR ) ";

    private static final String CREATE_TASK_CAMPAIGN_GROUP_INDEX = "CREATE INDEX "
            + TASK_TABLE + "_plan_group_ind  ON " + TASK_TABLE + "(" + PLAN_ID + "," + GROUP_ID + "," + SYNC_STATUS + ")";

    public TaskRepository(Repository repository, TaskNotesRepository taskNotesRepository) {
        super(repository);
        this.taskNotesRepository = taskNotesRepository;
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TASK_TABLE);
        database.execSQL(CREATE_TASK_CAMPAIGN_GROUP_INDEX);
    }

    public void addOrUpdate(Task task) {
        if (StringUtils.isBlank(task.getIdentifier())) {
            throw new IllegalArgumentException("identifier must be specified");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, task.getIdentifier());
        contentValues.put(PLAN_ID, task.getPlanIdentifier());
        contentValues.put(GROUP_ID, task.getGroupIdentifier());
        if (task.getStatus() != null) {
            contentValues.put(STATUS, task.getStatus().name());
        }
        contentValues.put(BUSINESS_STATUS, task.getBusinessStatus());
        contentValues.put(PRIORITY, task.getPriority());
        contentValues.put(CODE, task.getCode());
        contentValues.put(DESCRIPTION, task.getDescription());
        contentValues.put(FOCUS, task.getFocus());
        contentValues.put(FOR, task.getForEntity());
        contentValues.put(START, DateUtil.getMillis(task.getExecutionStartDate()));
        contentValues.put(END, DateUtil.getMillis(task.getExecutionEndDate()));
        contentValues.put(AUTHORED_ON, DateUtil.getMillis(task.getAuthoredOn()));
        contentValues.put(LAST_MODIFIED, DateUtil.getMillis(task.getLastModified()));
        contentValues.put(OWNER, task.getOwner());
        contentValues.put(SERVER_VERSION, task.getServerVersion());
        contentValues.put(SYNC_STATUS, task.getSyncStatus());
        contentValues.put(STRUCTURE_ID, task.getStructureId());
        contentValues.put(REASON_REFERENCE, task.getReasonReference());

        getWritableDatabase().replace(TASK_TABLE, null, contentValues);

        if (task.getNotes() != null) {
            for (Note note : task.getNotes())
                taskNotesRepository.addOrUpdate(note, task.getIdentifier());
        }

    }

    public Map<String, Set<Task>> getTasksByPlanAndGroup(String planId, String groupId) {
        Cursor cursor = null;
        Map<String, Set<Task>> tasks = new HashMap<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s != ?",
                    TASK_TABLE, PLAN_ID, GROUP_ID, STATUS), new String[]{planId, groupId, TaskStatus.CANCELLED.name()});
            while (cursor.moveToNext()) {
                Set<Task> taskSet;
                Task task = readCursor(cursor);
                if (tasks.containsKey(task.getStructureId()))
                    taskSet = tasks.get(task.getStructureId());
                else
                    taskSet = new HashSet<>();
                taskSet.add(task);
                tasks.put(task.getStructureId(), taskSet);
            }
        } catch (Exception e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return tasks;
    }


    public Task getTaskByIdentifier(String identifier) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TASK_TABLE +
                    " WHERE " + ID + " =?", new String[]{identifier});
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public Set<Task> getTasksByEntityAndCode(String planId, String groupId, String forEntity, String code) {
        Cursor cursor = null;
        Set<Task> taskSet = new HashSet<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s != ? AND %s =?  AND %s =? ",
                    TASK_TABLE, PLAN_ID, GROUP_ID, STATUS, FOR, CODE), new String[]{planId, groupId, TaskStatus.CANCELLED.name(), forEntity, code});
            while (cursor.moveToNext()) {
                Task task = readCursor(cursor);
                taskSet.add(task);
            }
        } catch (Exception e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return taskSet;
    }

    private Task readCursor(Cursor cursor) {
        Task task = new Task();
        task.setIdentifier(cursor.getString(cursor.getColumnIndex(ID)));
        task.setPlanIdentifier(cursor.getString(cursor.getColumnIndex(PLAN_ID)));
        task.setGroupIdentifier(cursor.getString(cursor.getColumnIndex(GROUP_ID)));
        if (cursor.getString(cursor.getColumnIndex(STATUS)) != null) {
            task.setStatus(TaskStatus.valueOf(cursor.getString(cursor.getColumnIndex(STATUS))));
        }
        task.setBusinessStatus(cursor.getString(cursor.getColumnIndex(BUSINESS_STATUS)));
        task.setPriority(cursor.getInt(cursor.getColumnIndex(PRIORITY)));
        task.setCode(cursor.getString(cursor.getColumnIndex(CODE)));
        task.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        task.setFocus(cursor.getString(cursor.getColumnIndex(FOCUS)));
        task.setForEntity(cursor.getString(cursor.getColumnIndex(FOR)));
        task.setExecutionStartDate(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(START))));
        task.setExecutionEndDate(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(END))));
        task.setAuthoredOn(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(AUTHORED_ON))));
        task.setLastModified(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(LAST_MODIFIED))));
        task.setOwner(cursor.getString(cursor.getColumnIndex(OWNER)));
        task.setSyncStatus(cursor.getString(cursor.getColumnIndex(SYNC_STATUS)));
        task.setServerVersion(cursor.getLong(cursor.getColumnIndex(SERVER_VERSION)));
        task.setStructureId(cursor.getString(cursor.getColumnIndex(STRUCTURE_ID)));
        task.setReasonReference(cursor.getString(cursor.getColumnIndex(REASON_REFERENCE)));

        return task;
    }

    public List<TaskUpdate> getUnSyncedTaskStatus() {
        Cursor cursor = null;
        List<TaskUpdate> taskUpdates = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT " + ID + "," + STATUS + "," + BUSINESS_STATUS + "," + SERVER_VERSION + "  FROM %s WHERE %s =?", TASK_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Unsynced});
            while (cursor.moveToNext()) {
                taskUpdates.add(readUpdateCursor(cursor));
            }
        } catch (Exception e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return taskUpdates;
    }

    public void markTaskAsSynced(String taskID) {
        try {
            ContentValues values = new ContentValues();
            values.put(TaskRepository.ID, taskID);
            values.put(TaskRepository.SYNC_STATUS, BaseRepository.TYPE_Synced);

            getWritableDatabase().update(TaskRepository.TASK_TABLE, values, TaskRepository.ID + " = ?",
                    new String[]{taskID});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TaskUpdate readUpdateCursor(Cursor cursor) {
        TaskUpdate taskUpdate = new TaskUpdate();
        taskUpdate.setIdentifier(cursor.getString(cursor.getColumnIndex(ID)));

        if (cursor.getString(cursor.getColumnIndex(STATUS)) != null) {
            taskUpdate.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
        }
        if (cursor.getString(cursor.getColumnIndex(BUSINESS_STATUS)) != null) {
            taskUpdate.setBusinessStatus(cursor.getString(cursor.getColumnIndex(BUSINESS_STATUS)));
        }
        if (cursor.getString(cursor.getColumnIndex(SERVER_VERSION)) != null) {
            taskUpdate.setServerVersion(cursor.getString(cursor.getColumnIndex(SERVER_VERSION)));
        }
        return taskUpdate;
    }

    public List<Task> getAllUnsynchedCreatedTasks() {
        Cursor cursor = null;
        List<Task> tasks = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT *  FROM %s WHERE %s =?", TASK_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Created});
            while (cursor.moveToNext()) {
                tasks.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return tasks;
    }

    /**
     * This method updates the task structureId field with a value from  the
     * residence attribute field of a given client
     * <p>
     * This is only done for tasks whose for field refers to a client's residence attribute field
     *
     * @param clients A list of clients
     * @return bolean indicating whether the update was successful or not
     */
    public boolean updateTaskStructureIdFromClient(List<Client> clients, String attribute) {
        if (clients == null || clients.isEmpty()) {
            return false;
        }

        SQLiteStatement updateStatement = null;
        try {
            getWritableDatabase().beginTransaction();

            String updateTaskSructureIdQuery = String.format("UPDATE %s  SET %s = ? WHERE %s = ?  AND %s IS NULL",
                    TASK_TABLE, STRUCTURE_ID, FOR, STRUCTURE_ID);
            updateStatement = getWritableDatabase().compileStatement(updateTaskSructureIdQuery);

            for (Client client : clients) {
                String taskFor = client.getBaseEntityId();
                if (client.getAttribute(attribute) == null) {
                    continue;
                }
                String structureId = client.getAttribute(attribute).toString();

                updateStatement.bindString(1, structureId);
                updateStatement.bindString(2, taskFor);
                updateStatement.executeUpdateDelete();

            }

            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;
        } catch (SQLException e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
            return false;
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }
    }

    /**
     * This method updates the task structureId field with a value from  the
     * location's id field of a given structure
     * <p>
     * This is only done for tasks whose for field refers to a structure's  id field
     *
     * @param locations A list of locations (structures)
     * @return bolean indicating whether the update was successful or not
     */
    public boolean updateTaskStructureIdFromStructure(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return false;
        }

        SQLiteStatement updateStatement = null;
        try {
            getWritableDatabase().beginTransaction();
            String updateTaskSructureIdQuery = String.format("UPDATE %s  SET %s = ? WHERE %s = ?   AND %s IS NULL",
                    TASK_TABLE, STRUCTURE_ID, FOR, STRUCTURE_ID);
            updateStatement = getWritableDatabase().compileStatement(updateTaskSructureIdQuery);

            for (Location location : locations) {

                updateStatement.bindString(1, location.getId());
                updateStatement.bindString(2, location.getId());
                updateStatement.executeUpdateDelete();
            }

            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;

        } catch (SQLException e) {
            Log.e(TaskRepository.class.getCanonicalName(), e.getMessage(), e);
            return false;
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }
    }

}
