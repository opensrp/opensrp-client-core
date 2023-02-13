package org.smartregister.util;

import static org.smartregister.AllConstants.DatabaseKeys.DB_VERSION;
import static org.smartregister.AllConstants.DatabaseKeys.SYNC_STATUS;
import static org.smartregister.AllConstants.DatabaseKeys.VALIDATION_STATUS;
import static org.smartregister.AllConstants.DeviceInfo.MANUFACTURER;
import static org.smartregister.AllConstants.DeviceInfo.MODEL;
import static org.smartregister.AllConstants.DeviceInfo.OS_VERSION;
import static org.smartregister.AllConstants.SyncInfo.APP_BUILD_DATE;
import static org.smartregister.AllConstants.SyncInfo.APP_VERSION_CODE;
import static org.smartregister.AllConstants.SyncInfo.APP_VERSION_NAME;
import static org.smartregister.AllConstants.SyncInfo.INVALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.INVALID_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.NULL_EVENT_SYNC_STATUS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.TASK_UNPROCESSED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_HEIGHT_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_VACCINE_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_WEIGHT_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.USER_LOCALITY;
import static org.smartregister.AllConstants.SyncInfo.USER_NAME;
import static org.smartregister.AllConstants.SyncInfo.USER_TEAM;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

import android.content.pm.PackageManager;
import android.os.Build;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class StatsUtils {

    private final Map<String, String> syncInfoMap = new HashMap<>();
    private final SQLiteDatabase database;

    public StatsUtils() {
        database = DrishtiApplication.getInstance().getRepository().getReadableDatabase();
    }

    public Map<String, String> fetchStatsInfo() {
        populateSyncStatistics();
        populateUserInfo();
        populateBuildInfo();
        populateDeviceInfo();
        return syncInfoMap;
    }

    private void populateSyncStatistics() {
        Cursor cursor = null;

        try {
            syncInfoMap.put(SYNCED_EVENTS, "-");
            syncInfoMap.put(SYNCED_CLIENTS, "-");
            syncInfoMap.put(UNSYNCED_EVENTS, "-");
            syncInfoMap.put(UNSYNCED_CLIENTS, "-");
            syncInfoMap.put(VALID_EVENTS, "-");
            syncInfoMap.put(INVALID_EVENTS, "-");
            syncInfoMap.put(VALID_CLIENTS, "-");
            syncInfoMap.put(INVALID_CLIENTS, "-");
            syncInfoMap.put(TASK_UNPROCESSED_EVENTS, "-");
            syncInfoMap.put(NULL_EVENT_SYNC_STATUS, "-");
            syncInfoMap.put(UNSYNCED_VACCINE_EVENTS, "-");
            syncInfoMap.put(UNSYNCED_WEIGHT_EVENTS, "-");
            syncInfoMap.put(UNSYNCED_HEIGHT_EVENTS, "-");

            String eventSyncSql = "select count(*), syncStatus from event group by syncStatus";
            String clientSyncSql = "select count(*), syncStatus from client group by syncStatus";

            String validatedEventsSql = "select count(*), validationStatus from event group by validationStatus";
            String validatedClientsSql = "select count(*), validationStatus from client group by validationStatus";

            String unsyncedVaccineEventsSQL = "SELECT COUNT(*) FROM vaccines WHERE sync_status = 'Unsynced'";
            String unsyncedWeightEventsSQL = "SELECT COUNT(*) FROM weights WHERE sync_status = 'Unsynced'";
            String unsyncedHeightEventsSQL = "SELECT COUNT(*) FROM heights WHERE sync_status = 'Unsynced'";

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

            cursor = database.rawQuery(unsyncedVaccineEventsSQL, new String[]{});
            while (cursor.moveToNext()) {
                syncInfoMap.put(UNSYNCED_VACCINE_EVENTS, String.valueOf(cursor.getInt(0)));
            }
            cursor.close();

            cursor = database.rawQuery(unsyncedWeightEventsSQL, new String[]{});
            while (cursor.moveToNext()) {
                syncInfoMap.put(UNSYNCED_WEIGHT_EVENTS, String.valueOf(cursor.getInt(0)));
            }
            cursor.close();

            if (CoreLibrary.getInstance().context().getAppProperties().isTrue("monitor.height")) { // Constant is defined in growth-monitoring module
                cursor = database.rawQuery(unsyncedHeightEventsSQL, new String[]{});
                while (cursor.moveToNext()) {
                    syncInfoMap.put(UNSYNCED_HEIGHT_EVENTS, String.valueOf(cursor.getInt(0)));
                }
                cursor.close();
            }
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
            syncInfoMap.put(SYNCED_EVENTS, String.valueOf(cursor.getInt(0)));
        } else if (BaseRepository.TYPE_Unsynced.equals(syncStatus)) {
            syncInfoMap.put(UNSYNCED_EVENTS, String.valueOf(cursor.getInt(0)));
        } else if (BaseRepository.TYPE_Task_Unprocessed.equals(syncStatus)) {
            syncInfoMap.put(TASK_UNPROCESSED_EVENTS, String.valueOf(cursor.getInt(0)));
        } else if (syncStatus == null) {
            syncInfoMap.put(NULL_EVENT_SYNC_STATUS, String.valueOf(cursor.getInt(0)));
        }
    }

    private void populateClientSyncInfo(Cursor cursor) {
        String syncStatus = cursor.getString(cursor.getColumnIndex(SYNC_STATUS));
        if (BaseRepository.TYPE_Synced.equals(syncStatus)) {
            syncInfoMap.put(SYNCED_CLIENTS, String.valueOf(cursor.getInt(0)));
        } else if (BaseRepository.TYPE_Unsynced.equals(syncStatus)) {
            syncInfoMap.put(UNSYNCED_CLIENTS, String.valueOf(cursor.getInt(0)));
        }
    }

    private void populateValidatedEventsInfo(Cursor cursor) {
        String syncStatus = cursor.getString(cursor.getColumnIndex(VALIDATION_STATUS));
        if (BaseRepository.TYPE_Valid.equals(syncStatus)) {
            syncInfoMap.put(VALID_EVENTS, String.valueOf(cursor.getInt(0)));
        } else if (BaseRepository.TYPE_InValid.equals(syncStatus)) {
            syncInfoMap.put(INVALID_EVENTS, String.valueOf(cursor.getInt(0)));
        }
    }

    private void populateValidatedClientsInfo(Cursor cursor) {
        String validationStatus = cursor.getString(cursor.getColumnIndex(VALIDATION_STATUS));
        if (BaseRepository.TYPE_Valid.equals(validationStatus)) {
            syncInfoMap.put(VALID_CLIENTS, String.valueOf(cursor.getInt(0)));
        } else if (BaseRepository.TYPE_InValid.equals(validationStatus)) {
            syncInfoMap.put(INVALID_CLIENTS, String.valueOf(cursor.getInt(0)));
        }
    }

    public void populateUserInfo() {
        AllSharedPreferences sharedPreferences = CoreLibrary.getInstance().context().userService().getAllSharedPreferences();
        String userName = sharedPreferences.fetchRegisteredANM();
        String userTeam = sharedPreferences.fetchDefaultTeam(sharedPreferences.fetchRegisteredANM());
        String userLocality = sharedPreferences.fetchCurrentLocality();
        syncInfoMap.put(USER_NAME, StringUtils.isNotBlank(userName) ? userName : "-");
        syncInfoMap.put(USER_TEAM, StringUtils.isNotBlank(userTeam) ? userTeam : "-");
        syncInfoMap.put(USER_LOCALITY, StringUtils.isNotBlank(userLocality) ? userLocality : "-");
    }

    private void populateBuildInfo() {
        try {
            syncInfoMap.put(APP_VERSION_NAME, Utils.getVersion(CoreLibrary.getInstance().context().applicationContext()));
        } catch (PackageManager.NameNotFoundException e) {
            syncInfoMap.put(APP_VERSION_NAME, "-");
            Timber.e(e);
        }
        try {
            syncInfoMap.put(APP_VERSION_CODE, String.valueOf(Utils.getVersionCode(CoreLibrary.getInstance().context().applicationContext())));
        } catch (PackageManager.NameNotFoundException e) {
            syncInfoMap.put(APP_VERSION_CODE, "-");
            Timber.e(e);
        }
        try {
            syncInfoMap.put(DB_VERSION, String.valueOf(Utils.getDatabaseVersion()));
        } catch (Exception e) {
            syncInfoMap.put(DB_VERSION, "-");
            Timber.e(e);
        }
    }

    private void populateDeviceInfo() {
        try {
            syncInfoMap.put(MANUFACTURER, Build.MANUFACTURER);
            syncInfoMap.put(MODEL, Build.MODEL);
            syncInfoMap.put(APP_BUILD_DATE, Utils.getBuildDate(true));

            String osName = Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
            syncInfoMap.put(OS_VERSION, osName);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
