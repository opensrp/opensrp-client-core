package org.smartregister.view.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.BaseMemberProfileOptions;
import org.smartregister.configuration.ConfigurableMemberProfileRowDataProvider;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.domain.ConfigurableMemberProfileRowData;
import org.smartregister.util.ConfigurationInstancesHelper;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;
import org.smartregister.view.interactor.BaseConfigurableMemberProfileInteractor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import static org.smartregister.AllConstants.Client.BASE_ENTITY_ID;
import static org.smartregister.AllConstants.Client.DOB;
import static org.smartregister.AllConstants.Client.FIRST_NAME;
import static org.smartregister.AllConstants.Client.GENDER;
import static org.smartregister.AllConstants.Client.LAST_NAME;
import static org.smartregister.AllConstants.Client.PRIMARY_CAREGIVER;
import static org.smartregister.AllConstants.Client.VILLAGE_TOWN;

public class BaseConfigurableMemberProfilePresenter implements ConfigurableMemberProfileActivityContract.Presenter, ConfigurableMemberProfileActivityContract.InteractorCallBack {

    protected WeakReference<ConfigurableMemberProfileActivityContract.View> viewReference;
    protected ConfigurableMemberProfileActivityContract.Interactor interactor;
    private ModuleConfiguration moduleConfiguration;
    private ConfigurableMemberProfileRowDataProvider dataProvider;

    public BaseConfigurableMemberProfilePresenter(ModuleConfiguration moduleConfiguration, @NonNull ConfigurableMemberProfileActivityContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.moduleConfiguration = moduleConfiguration;
        getProfileMemberDataProvider();
        this.interactor = createInteractor();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;
        if (!isChangingConfiguration) {
            // WHAT TODO?
        }
    }

    @Override
    public void processJson(@NotNull Context context, String eventType, @Nullable String tableName, String jsonString) {

    }

    @Override
    public void startFormForEdit(String title, CommonPersonObjectClient client) {

    }

    @Override
    public ConfigurableMemberProfileActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public ConfigurableMemberProfileActivityContract.Interactor createInteractor() {
        return new BaseConfigurableMemberProfileInteractor(dataProvider);
    }

    private void getProfileMemberDataProvider(){
        BaseMemberProfileOptions memberProfileOptions = null;
        Class<? extends BaseMemberProfileOptions> memberProfileOptionsClass = moduleConfiguration.getMemberProfileOptionsClass();
        if (memberProfileOptionsClass != null) {
            memberProfileOptions = ConfigurationInstancesHelper.newInstance(memberProfileOptionsClass);
        }
        Class<? extends ConfigurableMemberProfileRowDataProvider> dataProviderClass = memberProfileOptions.getMemberProfileDataProvider();
        if (memberProfileOptionsClass != null) {
            this.dataProvider = ConfigurationInstancesHelper.newInstance(dataProviderClass);
        }
    }

    @Override
    public void fetchProfileData(CommonPersonObjectClient client) {
        interactor.refreshProfileView(client, false, this);
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        if (getView() != null) {
            String name = client.getDetails().get(FIRST_NAME) + " " + client.getDetails().get(LAST_NAME);
            int age = Years.yearsBetween(new DateTime(client.getDetails().get(DOB)), DateTime.now()).getYears();
            String nameAndAge = String.format(Locale.getDefault(), "%s, %d", name, age);
            getView().setProfileImage(client.getDetails().get(BASE_ENTITY_ID));
            getView().setProfileName(nameAndAge);
            getView().setAddress(client.getDetails().get(VILLAGE_TOWN));
            getView().setGender(client.getDetails().get(GENDER));
            getView().setPrimaryCaregiver(client.getDetails().get(PRIMARY_CAREGIVER));
        }
    }

    @Override
    public void refreshProfileBottomSection(List<ConfigurableMemberProfileRowData> rowDataList) {
        getView().updateBottomSection(rowDataList);
    }
}
