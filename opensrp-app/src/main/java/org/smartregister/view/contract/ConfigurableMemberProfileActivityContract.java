package org.smartregister.view.contract;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.ConfigurableMemberProfileRowData;

import java.util.List;

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

        void goToRowActivity(Class<?> rowClickedLaunchedClass);

        void showProgressBar(boolean show);

        void updateBottomSection(List<ConfigurableMemberProfileRowData> rowDataList);
    }

    interface Presenter extends BaseProfileContract.Presenter {

        View getView();

        default Interactor createInteractor() {
            return null;
        }

        void fetchProfileData(CommonPersonObjectClient client);

        void processJson(@NotNull Context context, String eventType, @Nullable String tableName, String jsonString);

        void startFormForEdit(String title, CommonPersonObjectClient client);
    }

    interface Interactor {
        void refreshProfileView(CommonPersonObjectClient client, boolean isForEdit, InteractorCallBack callback);
    }

    interface InteractorCallBack {
        void refreshProfileTopSection(CommonPersonObjectClient client);

        void refreshProfileBottomSection(List<ConfigurableMemberProfileRowData> rowDataList);
    }

}
