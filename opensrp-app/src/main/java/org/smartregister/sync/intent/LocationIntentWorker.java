package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.smartregister.domain.LocationProperty;
import org.smartregister.sync.helper.LocationServiceHelper;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;

import java.net.SocketException;

public class LocationIntentWorker extends BaseSyncIntentWorker {
    private static final String TAG = "LocationIntentService";
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();

    public LocationIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void onRunWork() throws SocketException {
        LocationServiceHelper locationServiceHelper = LocationServiceHelper.getInstance();
        locationServiceHelper.fetchLocationsStructures();
    }
}