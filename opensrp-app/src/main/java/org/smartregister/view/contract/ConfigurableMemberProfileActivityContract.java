package org.smartregister.view.contract;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.BaseMemberProfileRowsDataProvider;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public interface ConfigurableMemberProfileActivityContract {
    interface View extends BaseProfileContract.View {
        Presenter presenter();

        void setProfileImage(String baseEntityId);

        void setProfileName(String fullName);

        void setGender(String gender);

        void setAddress(String address);

        void setId(String id);

        void setPrimaryCaregiver(String fullName);

        void startFormActivity(JSONObject formJson);

        default void openSickChildRegistration() {
        }

        void setLastVisit(Date lastVisitDate);

        void setUpComingServicesStatus(String service, org.smartregister.domain.AlertStatus status, Date date);

        void setFamilyStatus(AlertStatus status);

        void openMedicalHistory();

        void openUpcomingServices();

        void openFamilyDueServices();

    }

    interface Presenter extends BaseProfileContract.Presenter {

        View getView();

        default Interactor createInteractor() {
            return null;
        }

        void fetchProfileData(CommonPersonObjectClient client);

        default void startSickChildForm(CommonPersonObjectClient client) {
        }

        void processJson(@NotNull Context context, String eventType, @Nullable String tableName, String jsonString);

        void startFormForEdit(String title, CommonPersonObjectClient client);
    }

    interface Interactor {
        void refreshProfileView(CommonPersonObjectClient client, boolean isForEdit, InteractorCallBack callback);
    }

    interface InteractorCallBack {
        void refreshProfileTopSection(CommonPersonObjectClient client);

        void refreshLastVisit(Date lastVisitDate);

        void refreshUpComingServicesStatus(String service, AlertStatus status, Date date);

        void refreshFamilyStatus(AlertStatus status);

    }

}
