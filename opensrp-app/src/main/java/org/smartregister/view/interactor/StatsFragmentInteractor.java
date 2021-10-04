package org.smartregister.view.interactor;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.StatsFragmentContract;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.AllConstants.DatabaseKeys.SYNC_STATUS;
import static org.smartregister.AllConstants.DatabaseKeys.VALIDATION_STATUS;
import static org.smartregister.AllConstants.SyncInfo.CACHED_HEIGHTS;
import static org.smartregister.AllConstants.SyncInfo.CACHED_RECURRING_SERVICE_RECORDS;
import static org.smartregister.AllConstants.SyncInfo.CACHED_VACCINES;
import static org.smartregister.AllConstants.SyncInfo.CACHED_WEIGHTS;
import static org.smartregister.AllConstants.SyncInfo.INVALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.INVALID_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.NULL_EVENT_SYNC_STATUS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.TASK_UNPROCESSED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

public class StatsFragmentInteractor implements StatsFragmentContract.Interactor {

    private AppExecutors appExecutors;

    private SQLiteDatabase database;

    private StatsFragmentContract.Presenter presenter;

    private Map<String, Integer> syncInfoMap = new HashMap<>();

    public StatsFragmentInteractor(StatsFragmentContract.Presenter presenter) {
        this.presenter = presenter;
        appExecutors = new AppExecutors();
        database = DrishtiApplication.getInstance().getRepository().getReadableDatabase();
    }

    @Override
    public void fetchECSyncInfo() {
        syncInfoMap = new HashMap<>();
        syncInfoMap.put(SYNCED_EVENTS, 0);
        syncInfoMap.put(SYNCED_CLIENTS, 0);
        syncInfoMap.put(UNSYNCED_EVENTS, 0);
        syncInfoMap.put(UNSYNCED_CLIENTS, 0);
        syncInfoMap.put(VALID_EVENTS, 0);
        syncInfoMap.put(INVALID_EVENTS, 0);
        syncInfoMap.put(VALID_CLIENTS, 0);
        syncInfoMap.put(INVALID_CLIENTS, 0);
        syncInfoMap.put(TASK_UNPROCESSED_EVENTS, 0);
        syncInfoMap.put(CACHED_VACCINES, 0);
        syncInfoMap.put(CACHED_RECURRING_SERVICE_RECORDS, 0);
        syncInfoMap.put(CACHED_HEIGHTS, 0);
        syncInfoMap.put(CACHED_WEIGHTS, 0);

        String eventSyncSql = "select count(*), syncStatus from event group by syncStatus";
        String clientSyncSql = "select count(*), syncStatus from client group by syncStatus";

        String validatedEventsSql = "select count(*), validationStatus from event group by validationStatus";
        String validatedClientsSql = "select count(*), validationStatus from client group by validationStatus";

        String cachedVaccinesSql = "select count(*) vaccines WHERE sync_status != 'Synced'";
        String cachedRecurringServiceRecordsSql = "select count(*) from recurring_service_records where WHERE sync_status != 'Synced'";
        String cachedHeightsSql = "select count(*)  from heights WHERE sync_status != 'Synced'";
        String cachedWeightsSql = "select count(*) from weights WHERE sync_status != 'Synced'";

        Cursor cursor = null;

        try {
            cursor = database.rawQuery(eventSyncSql, new String[]{});
            while (cursor.moveToNext()) {
                populateEventSyncInfo(cursor);
            }
            cursor.close();

            cursor = database.rawQuery(clientSyncSql, new String[]{});
            while (cursor.moveToNext()) {
                populateClientSyncInfo(cursor);
            }
            cursor.close();

            cursor = database.rawQuery(validatedEventsSql, new String[]{});
            while (cursor.moveToNext()) {
                populateValidatedEventsInfo(cursor);
            }
            cursor.close();

            cursor = database.rawQuery(validatedClientsSql, new String[]{});
            while (cursor.moveToNext()) {
                populateValidatedClientsInfo(cursor);
            }

            cursor.close();


            cursor = database.rawQuery(cachedVaccinesSql, new String[]{});
            while (cursor.moveToNext()) {

            }
            cursor.close();


            cursor = database.rawQuery(cachedRecurringServiceRecordsSql, new String[]{});
            while (cursor.moveToNext()) {

            }
            cursor.close();


            cursor = database.rawQuery(cachedHeightsSql, new String[]{});
            while (cursor.moveToNext()) {

            }
            cursor.close();


            cursor = database.rawQuery(cachedWeightsSql, new String[]{});
            while (cursor.moveToNext()) {

            }
            cursor.close();

            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    presenter.onECSyncInfoFetched(syncInfoMap);
                }
            });
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void populateEventSyncInfo(Cursor cursor) {
        String syncStatus = cursor.getString(cursor.getColumnIndex(SYNC_STATUS));
        if (BaseRepository.TYPE_Synced.equals(syncStatus)) {
            syncInfoMap.put(SYNCED_EVENTS, cursor.getInt(0));
        } else if (BaseRepository.TYPE_Unsynced.equals(syncStatus)) {
            syncInfoMap.put(UNSYNCED_EVENTS, cursor.getInt(0));
        } else if (BaseRepository.TYPE_Task_Unprocessed.equals(syncStatus)) {
            syncInfoMap.put(TASK_UNPROCESSED_EVENTS, cursor.getInt(0));
        } else if (syncStatus == null) {
            syncInfoMap.put(NULL_EVENT_SYNC_STATUS, cursor.getInt(0));
        }
    }

    private void populateClientSyncInfo(Cursor cursor) {
        String syncStatus = cursor.getString(cursor.getColumnIndex(SYNC_STATUS));
        if (BaseRepository.TYPE_Synced.equals(syncStatus)) {
            syncInfoMap.put(SYNCED_CLIENTS, cursor.getInt(0));
        } else if (BaseRepository.TYPE_Unsynced.equals(syncStatus)) {
            syncInfoMap.put(UNSYNCED_CLIENTS, cursor.getInt(0));
        }
    }

    private void populateValidatedEventsInfo(Cursor cursor) {
        String syncStatus = cursor.getString(cursor.getColumnIndex(VALIDATION_STATUS));
        if (BaseRepository.TYPE_Valid.equals(syncStatus)) {
            syncInfoMap.put(VALID_EVENTS, cursor.getInt(0));
        } else if (BaseRepository.TYPE_InValid.equals(syncStatus)) {
            syncInfoMap.put(INVALID_EVENTS, cursor.getInt(0));
        }
    }

    private void populateValidatedClientsInfo(Cursor cursor) {
        String validationStatus = cursor.getString(cursor.getColumnIndex(VALIDATION_STATUS));
        if (BaseRepository.TYPE_Valid.equals(validationStatus)) {
            syncInfoMap.put(VALID_CLIENTS, cursor.getInt(0));
        } else if (BaseRepository.TYPE_InValid.equals(validationStatus)) {
            syncInfoMap.put(INVALID_CLIENTS, cursor.getInt(0));
        }
    }

}
