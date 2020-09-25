package org.smartregister.view.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;

import java.util.HashMap;
import java.util.List;

public interface ConfigurableRegisterActivityContract {

    interface View {
        Presenter presenter();

        void startFormActivity(String formName, String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String clientTable);

        void startFormActivity(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData);
    }

    interface Presenter {

         default void saveLanguage(String language) {}

        default void startForm(String formName, String entityId, String metadata, String currentLocationId) {}

        default void startForm(String formName, String entityId, String metaData, String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {}

        default void saveForm(String jsonString, @NonNull RegisterParams registerParams) {}

        default Interactor createInteractor() {
             return null;
        }
    }

    interface Model {

        default JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws JSONException {
            return getFormAsJson(formName, entityId, currentLocationId, null);
        }

        @Nullable
        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException;

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        HashMap<Client, List<Event>> processRegistration(String jsonString, FormTag formTag);

        String getLocationId(String locationName);

        String getInitials();
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, ConfigurableRegisterActivityContract.InteractorCallBack callBack);

        void saveRegistration(@Nullable final List<EventClient> clientList, final String jsonString, @NonNull RegisterParams registerParams, final ConfigurableRegisterActivityContract.InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(@NonNull RegisterParams registerParams, @Nullable List<EventClient> clientList);
    }
}
