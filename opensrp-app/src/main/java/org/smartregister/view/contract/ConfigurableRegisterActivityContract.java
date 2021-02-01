package org.smartregister.view.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
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

        default void saveLanguage(String language) {
            // Empty default
        }

        default void startForm(String formName, String entityId, String metadata, String currentLocationId) {
            // Empty default
        }

        default void startForm(String formName, String entityId, String metaData, String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
            // Empty default
        }

        default void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
            // Empty default
        }

        default Interactor createInteractor() {
            return null;
        }

        default HashMap<String, String> getInjectedFieldValues(CommonPersonObjectClient client) {
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

        default HashMap<String, String> getInjectedFieldValues(CommonPersonObjectClient client) {
            return null;
        }

        String getLocationId(String locationName);

        String getInitials();
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, ConfigurableRegisterActivityContract.InteractorCallBack callBack);

        // TODO: FIX THIS. This should be the method with the functionality and not the above. We can change this to throw an exception instead
        default void getNextUniqueId(Triple<String, String, String> triple, ConfigurableRegisterActivityContract.InteractorCallBack callBack, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
            getNextUniqueId(triple, callBack);
        }

        void saveRegistration(@Nullable HashMap<Client, List<Event>> clientList, final String jsonString, @NonNull RegisterParams registerParams, final ConfigurableRegisterActivityContract.InteractorCallBack callBack);

        void saveEvents(@NonNull List<Event> events, @NonNull InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        // TODO: FIX THIS. This should be the method with the functionality and not the above. We can change this to throw an exception instead
        default void onUniqueIdFetched(Triple<String, String, String> triple, String entityId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
            onUniqueIdFetched(triple, entityId);
        }

        void onNoUniqueId();

        void onRegistrationSaved(@NonNull RegisterParams registerParams, @Nullable HashMap<Client, List<Event>> clientList);

        void onEventSaved();
    }
}
