package org.smartregister.view.interactor;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.UniqueId;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
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
                        callBack.onRegistrationSaved(registerParams, opdEventClientList);
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



    private void saveRegistration(@NonNull HashMap<Client, List<Event>> clientEventsHashMap, @NonNull String jsonString,
                                  @NonNull RegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (Client client: clientEventsHashMap.keySet()) {
                try {

                    List<Event> clientEvents = clientEventsHashMap.get(client);

                    if (client != null) {
                        JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(client));
                        if (params.isEditMode()) {
                            try {
                                JsonFormUtils.mergeAndSaveClient(client);
                            } catch (Exception e) {
                                Timber.e(e, "OpdRegisterInteractor --> mergeAndSaveClient");
                            }
                        } else {
                            getSyncHelper().addClient(client.getBaseEntityId(), clientJson);
                        }
                    }
                    updateOpenSRPId(jsonString, params, client);

                    if (clientEvents != null) {
                        for (Event baseEvent : clientEvents) {
                            addEvent(params, currentFormSubmissionIds, baseEvent);
                            //addImageLocation(jsonString, i, client, baseEvent);
                        }
                    }
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

    // TODO: FIX SAVING IMAGE
/*
    private void addImageLocation(String jsonString, int i, Client baseClient, Event baseEvent) {
        if (baseClient != null && baseEvent != null) {
            String imageLocation = null;
            if (i == 0) {
                imageLocation = OpdJsonFormUtils.getFieldValue(jsonString, OpdConstants.KEY.PHOTO);
            } else if (i == 1) {
                imageLocation =
                        OpdJsonFormUtils.getFieldValue(jsonString, OpdJsonFormUtils.STEP2, OpdConstants.KEY.PHOTO);
            }

            if (StringUtils.isNotBlank(imageLocation)) {
                OpdJsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }
        }
    }*/

    private void updateOpenSRPId(@NonNull String jsonString, @NonNull RegisterParams params, @Nullable Client baseClient) {
        if (params.isEditMode()) {
            // Unassign current OPENSRP ID
            if (baseClient != null) {
                try {
                    String newOpenSRPId = baseClient.getIdentifier(AllConstants.JSON.FieldKey.OPENSRP_ID).replace("-", "");
                    String currentOpenSRPId = JsonFormUtils.getString(jsonString, AllConstants.JSON.FieldKey.CURRENT_OPENSRP_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        getUniqueIdRepository().open(currentOpenSRPId);
                    }
                } catch (Exception e) {//might crash if M_ZEIR
                    Timber.d(e, "RegisterInteractor --> unassign opensrp id");
                }
            }

        } else {
            if (baseClient != null) {
                String opensrpId = baseClient.getIdentifier(AllConstants.JSON.FieldKey.ZEIR_ID);
                //mark OPENSRP ID as used
                getUniqueIdRepository().close(opensrpId);
            }
        }
    }

    private void addEvent(RegisterParams params, List<String> currentFormSubmissionIds, @Nullable Event baseEvent) throws JSONException {
        if (baseEvent != null) {
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
            currentFormSubmissionIds
                    .add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
        }
    }

}