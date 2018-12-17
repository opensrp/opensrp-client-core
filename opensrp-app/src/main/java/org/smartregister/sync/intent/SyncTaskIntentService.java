package org.smartregister.sync.intent;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NoHttpResponseException;
import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.LocationTaskServiceHelper;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.Utils;

import java.util.List;

import static org.smartregister.AllConstants.CAMPAIGNS;

public class SyncTaskIntentService extends IntentService {
    private static final String TAG = "SyncTaskIntentService";

    public SyncTaskIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocationTaskServiceHelper locationTaskServiceHelper = new LocationTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository(), CoreLibrary.getInstance().context().getLocationRepository(), CoreLibrary.getInstance().context().getStructureRepository());

        locationTaskServiceHelper.syncTasks();
    }

}