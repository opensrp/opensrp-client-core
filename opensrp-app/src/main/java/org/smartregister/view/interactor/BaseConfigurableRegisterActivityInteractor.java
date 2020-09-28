package org.smartregister.view.interactor;


import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.contract.ConfigurableRegisterActivityContract;
import org.smartregister.view.contract.RegisterParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseConfigurableRegisterActivityInteractor implements BaseRegisterContract.Interactor {

    protected AppExecutors appExecutors;

    public BaseConfigurableRegisterActivityInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    BaseConfigurableRegisterActivityInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final ConfigurableRegisterActivityContract.InteractorCallBack callBack) {
        // Do nothing for now, this will be handled by the class that extends this
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        // Do nothing for now, this will be handled by the class that extends this to nullify the presenter
    }

    @Override
    public void saveRegistration(List<EventClient> opdEventClientList, String jsonString, RegisterParams registerParams, ConfigurableRegisterActivityContract.InteractorCallBack callBack) {
        // Do nothing for now, this will be handled by the class that extends this
    }

    @Override
    public void saveEvents(@NonNull final List<Event> events, @NonNull final ConfigurableRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            List<String> formSubmissionsIds = new ArrayList<>();
            for (Event event : events) {
                formSubmissionsIds.add(event.getFormSubmissionId());
                saveEventInDb(event);
            }
            processLatestUnprocessedEvents(formSubmissionsIds);

            appExecutors.mainThread().execute(() -> callBack.onEventSaved());
        };

        appExecutors.diskIO().execute(runnable);
    }


    private void saveEventInDb(@NonNull Event event) {
        try {
            CoreLibrary.getInstance()
                    .context()
                    .getEventClientRepository()
                    .addEvent(event.getBaseEntityId()
                            , new JSONObject(JsonFormUtils.gson.toJson(event)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void processLatestUnprocessedEvents(@NonNull List<String> formSubmissionsIds) {
        // Process this event
        try {
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(formSubmissionsIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(new Date().getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @NonNull
    public ECSyncHelper getSyncHelper() {
        return ECSyncHelper.getInstance(CoreLibrary.getInstance().context().applicationContext());
    }

    @NonNull
    public AllSharedPreferences getAllSharedPreferences() {
        return CoreLibrary.getInstance().context().allSharedPreferences();
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        return CoreLibrary.getInstance().context().getUniqueIdRepository();
    }

}