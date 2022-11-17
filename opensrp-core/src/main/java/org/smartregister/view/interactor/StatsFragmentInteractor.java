package org.smartregister.view.interactor;

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

import android.content.Context;
import android.os.Build;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.StatsFragmentContract;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class StatsFragmentInteractor implements StatsFragmentContract.Interactor {

    private AppExecutors appExecutors;

    private SQLiteDatabase database;

    private StatsFragmentContract.Presenter presenter;

    private Map<String, String> syncInfoMap = new HashMap<>();

    public StatsFragmentInteractor(StatsFragmentContract.Presenter presenter) {
        this.presenter = presenter;
        appExecutors = new AppExecutors();
        database = DrishtiApplication.getInstance().getRepository().getReadableDatabase();
    }

    @Override
    public void fetchECSyncInfo() {
        syncInfoMap = new HashMap<>();
        syncInfoMap.put(SYNCED_EVENTS, "0");
        syncInfoMap.put(SYNCED_CLIENTS, "0");
        syncInfoMap.put(UNSYNCED_EVENTS, "0");
        syncInfoMap.put(UNSYNCED_CLIENTS, "0");
        syncInfoMap.put(VALID_EVENTS, "0");
        syncInfoMap.put(INVALID_EVENTS, "0");
        syncInfoMap.put(VALID_CLIENTS, "0");
        syncInfoMap.put(INVALID_CLIENTS, "0");
        syncInfoMap.put(TASK_UNPROCESSED_EVENTS, "0");
        syncInfoMap.put(NULL_EVENT_SYNC_STATUS, "0");
        syncInfoMap.put(UNSYNCED_VACCINE_EVENTS, "0");
        syncInfoMap.put(UNSYNCED_WEIGHT_EVENTS, "0");
        syncInfoMap.put(UNSYNCED_HEIGHT_EVENTS, "0");

        String eventSyncSql = "select count(*), syncStatus from event group by syncStatus";
        String clientSyncSql = "select count(*), syncStatus from client group by syncStatus";

        String validatedEventsSql = "select count(*), validationStatus from event group by validationStatus";
        String validatedClientsSql = "select count(*), validationStatus from client group by validationStatus";

        String unsyncedVaccineEventsSQL = "SELECT COUNT(*) FROM vaccines WHERE sync_status = 'Unsynced'";
        String unsyncedWeightEventsSQL = "SELECT COUNT(*) FROM weights WHERE sync_status = 'Unsynced'";
        String unsyncedHeightEventsSQL = "SELECT COUNT(*) FROM heights WHERE sync_status = 'Unsynced'";

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

            cursor = database.rawQuery(unsyncedVaccineEventsSQL, new String[]{});
            while (cursor.moveToNext()) {
                syncInfoMap.put(UNSYNCED_VACCINE_EVENTS, String.valueOf(cursor.getInt(0)));
            }

            cursor = database.rawQuery(unsyncedWeightEventsSQL, new String[]{});
            while (cursor.moveToNext()) {
                syncInfoMap.put(UNSYNCED_WEIGHT_EVENTS, String.valueOf(cursor.getInt(0)));
            }
            if (CoreLibrary.getInstance().context().getAppProperties().isTrue(AllConstants.PROPERTY.MONITOR_HEIGHT)) {
                cursor = database.rawQuery(unsyncedHeightEventsSQL, new String[]{});
                while (cursor.moveToNext()) {
                    syncInfoMap.put(UNSYNCED_HEIGHT_EVENTS, String.valueOf(cursor.getInt(0)));
                }
            }

            cursor.close();

            populateUserInfo();
            populateBuildInfo();
            populateDeviceInfo();

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
        syncInfoMap.put(USER_NAME, sharedPreferences.fetchRegisteredANM());
        syncInfoMap.put(USER_TEAM, sharedPreferences.fetchDefaultTeam(sharedPreferences.fetchRegisteredANM()));
        syncInfoMap.put(USER_LOCALITY, sharedPreferences.fetchCurrentLocality());
    }


    private void populateBuildInfo() {
        syncInfoMap.put(APP_VERSION_NAME, getBuildConfigValue("VERSION_NAME"));
        syncInfoMap.put(APP_VERSION_CODE, getBuildConfigValue("VERSION_CODE"));
        syncInfoMap.put(DB_VERSION, getBuildConfigValue("DATABASE_VERSION"));
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

    private String getBuildConfigValue(String fieldName) {
        try {
            Context context = CoreLibrary.getInstance().context().applicationContext();
            Class<?> clazz = Class.forName(context.getPackageName() + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return String.valueOf(field.get(null));
        } catch (Exception e) {
            Timber.e(e);
            return "0";
        }
    }
}
