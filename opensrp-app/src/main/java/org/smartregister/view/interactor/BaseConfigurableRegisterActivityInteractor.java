package org.smartregister.view.interactor;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
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
import java.util.HashMap;
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
        getNextUniqueId(triple, callBack, null, null);
    }

    @Override
    public void getNextUniqueId(Triple<String, String, String> triple, ConfigurableRegisterActivityContract.InteractorCallBack callBack, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        // Do nothing for now, this will be handled by the class that extends this
        appExecutors.diskIO()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                        final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (StringUtils.isBlank(entityId)) {
                                    callBack.onNoUniqueId();
                                } else {
                                    callBack.onUniqueIdFetched(triple, entityId, injectedFieldValues, entityTable);
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        // Do nothing for now, this will be handled by the class that extends this to nullify the presenter
    }

    @Override
    public void saveRegistration(HashMap<Client, List<Event>> opdEventClientList, String jsonString, RegisterParams registerParams, ConfigurableRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(opdEventClientList, jsonString, registerParams);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(registerParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
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



    private void saveRegistration(@NonNull HashMap<Client, List<Event>> opdEventClientList, @NonNull String jsonString,
                                  @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < opdEventClientList.size(); i++) {
                try {

                    OpdEventClient opdEventClient = opdEventClients.get(i);
                    Client baseClient = opdEventClient.getClient();
                    Event baseEvent = opdEventClient.getEvent();

                    if (baseClient != null) {
                        JSONObject clientJson = new JSONObject(OpdJsonFormUtils.gson.toJson(baseClient));
                        if (params.isEditMode()) {
                            try {
                                OpdJsonFormUtils.mergeAndSaveClient(baseClient);
                            } catch (Exception e) {
                                Timber.e(e, "OpdRegisterInteractor --> mergeAndSaveClient");
                            }
                        } else {
                            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                        }
                    }

                    addEvent(params, currentFormSubmissionIds, baseEvent);
                    updateOpenSRPId(jsonString, params, baseClient);
                    addImageLocation(jsonString, i, baseClient, baseEvent);
                } catch (Exception e) {
                    Timber.e(e, "OpdRegisterInteractor --> saveRegistration loop");
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "OpdRegisterInteractor --> saveRegistration");
        }
    }

}