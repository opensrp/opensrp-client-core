package org.smartregister.view.contract;

import android.content.Context;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;

import java.util.List;

/**
 * Created by keyamn on 27/06/2018.
 */
public interface BaseRegisterContract {
    interface Presenter {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        void startForm(String formName, String entityId, String metatata, LocationPickerView locationPickerView) throws Exception;

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveForm(String jsonString, boolean isEditMode);

        void closeAncRecord(String jsonString);

        void onDestroy(boolean isChangingConfiguration);
        
	    void updateInitials();
    }

    interface View {

        Context getContext();

        void displaySyncNotification();

        void displayToast(int resourceId);

        void displayToast(String message);

        void displayShortToast(int resourceId);

        void showLanguageDialog(List<String> displayValues);

        void startFormActivity(JSONObject form);

        void refreshList(final FetchStatus fetchStatus);

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void showAttentionFlagsDialog(List<AttentionFlag> attentionFlags);
        
	    void updateInitialsText(String initials);
    }

    interface Model {
        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        Pair<Client, Event> processRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws Exception;
	
	    String getInitials();
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, BaseRegisterContract.InteractorCallBack callBack);

        void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final BaseRegisterContract.InteractorCallBack callBack);

        void removeWomanFromANCRegister(String closeFormJsonString, String providerId);

    }

    public interface InteractorCallBack {
        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEdit);
    }
}
