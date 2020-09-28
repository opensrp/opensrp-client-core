package org.smartregister.view.presenter;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.db.EventClient;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.contract.ConfigurableRegisterActivityContract;
import org.smartregister.view.contract.RegisterParams;
import org.smartregister.view.interactor.BaseConfigurableRegisterActivityInteractor;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseConfigurableRegisterActivityPresenter implements BaseRegisterContract.Presenter, ConfigurableRegisterActivityContract.InteractorCallBack {

    protected WeakReference<BaseRegisterContract.View> viewReference;
    protected ConfigurableRegisterActivityContract.Interactor interactor;
    protected BaseRegisterContract.Model model;
    private JSONObject form;

    public BaseConfigurableRegisterActivityPresenter(@NonNull BaseRegisterContract.View view, @NonNull BaseRegisterContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = createInteractor();
        this.model = model;
    }

    @NonNull
    @Override
    public ConfigurableRegisterActivityContract.Interactor createInteractor() {
        return new BaseConfigurableRegisterActivityInteractor();
    }

    public void setModel(BaseRegisterContract.Model model) {
        this.model = model;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy\
        if (!isChangingConfiguration) {
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null && getView() != null) {
            getView().updateInitialsText(initials);
        }
    }

    @Nullable
    private BaseRegisterContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);

        if (getView() != null) {
            getView().displayToast(language + " selected");
        }
    }

    @Override
    public void onEventSaved() {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    @Override
    public HashMap<String, String> getInjectedFields(@NonNull String formName, @NonNull String entityId) {
        return OpdUtils.getInjectableFields(formName, entityId);
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull String entityId, String metaData
            , @NonNull String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metaData, locationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }
        form = null;
        try {
            form = model.getFormAsJson(formName, entityId, locationId, getInjectedFields(formName, entityId));
            // TODO: FIX THIS
            /*if (formName.equals(OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT)) {
                interactor.fetchSavedDiagnosisAndTreatmentForm(entityId, entityTable, this);
                return;
            }*/


        } catch (JSONException e) {
            Timber.e(e);
        }
        startFormActivity(entityId, entityTable, form);
    }

    private void startFormActivity(@NonNull String entityId, @Nullable String entityTable, @Nullable JSONObject form) {
        if (getView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(OpdConstants.IntentKey.BASE_ENTITY_ID, entityId);
            intentKeys.put(OpdConstants.IntentKey.ENTITY_TABLE, entityTable);

            getView().startFormActivity(form, intentKeys);
        }
    }

    @Override
    public void onUniqueIdFetched(@NonNull Triple<String, String, String> triple, @NonNull String entityId) {
        startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), null, null);
    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onRegistrationSaved(@NonNull RegisterParams registerParams, @Nullable List<EventClient> clientList) {

    }
}