package org.smartregister.view.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;

import java.util.List;

public interface ConfigurableRegisterActivityContract {

    interface View {
        Presenter presenter();
    }

    interface Presenter {

         default void saveLanguage(String language) {

         }

        default void startForm(String formName, String entityId, String metadata, String currentLocationId) {

        }

        default void saveForm(String jsonString, @NonNull RegisterParams registerParams) {

        }

        default Interactor createInteractor() {
             return null;
        }
    }

    interface Model {

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws JSONException;

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        List<EventClient> processRegistration(String jsonString, FormTag formTag);

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
