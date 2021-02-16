package org.smartregister.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.contract.RegisterParams;
import org.smartregister.view.fragment.BaseConfigurableRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;
import org.smartregister.view.model.BaseConfigurableRegisterActivityModel;
import org.smartregister.view.presenter.BaseConfigurableRegisterActivityPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.AllConstants.IntentExtra.JsonForm.ACTION;
import static org.smartregister.AllConstants.IntentExtra.JsonForm.ACTION_REGISTRATION;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-09-2020
 */

public class BaseConfigurableRegisterActivity extends BaseRegisterActivity {

    protected String moduleName;
    protected ModuleConfiguration moduleConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moduleName = Utils.extractModuleName(getIntent());
        fetchModuleConfiguration();

        super.onCreate(savedInstanceState);
        onStartActivityWithAction();
    }

    private void fetchModuleConfiguration() {
        moduleConfiguration = CoreLibrary.getInstance()
                .getModuleConfiguration(getModuleName());
    }

    public String getModuleName() {
        return moduleName;
    }

    public ModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    protected void onStartActivityWithAction() {
        String action = getIntent().getStringExtra(ACTION);
        if (action != null) {
            CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra(AllConstants.INTENT_KEY.COMMON_PERSON_CLIENT);
            ModuleMetadata moduleMetadata = getModuleConfiguration().getModuleMetadata();
            if (ACTION_REGISTRATION.equals(action) && moduleMetadata != null) {
                String baseEntityId = getIntent().getStringExtra(AllConstants.IntentExtra.JsonForm.BASE_ENTITY_ID);
                String table = getIntent().getStringExtra(AllConstants.IntentExtra.JsonForm.ENTITY_TABLE);

                startFormActivity(moduleMetadata.getModuleRegister().getRegistrationFormName(), baseEntityId, null, presenter().getInjectedFieldValues(client), table);
            }
        }
    }

    @Override
    protected void registerBottomNavigation() {
        try {
            View bottomNavGeneralView = findViewById(org.smartregister.R.id.bottom_navigation);
            if (bottomNavGeneralView instanceof BottomNavigationView) {
                BottomNavigationView bottomNavigationView = (BottomNavigationView) bottomNavGeneralView;
                if (!getModuleConfiguration().isBottomNavigationEnabled()) {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        } catch (NoSuchFieldError e) {
            // This error occurs because the ID cannot be found on some client applications because the layout
            // has been overriden
            Timber.e(e);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = createPresenter(this, createActivityModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        BaseConfigurableRegisterFragment baseConfigurableRegisterFragment = new BaseConfigurableRegisterFragment();
        baseConfigurableRegisterFragment.setModuleConfiguration(getModuleConfiguration());
        return baseConfigurableRegisterFragment;
    }


    public BaseRegisterContract.Presenter createPresenter(@NonNull BaseRegisterContract.View view, @NonNull BaseRegisterContract.Model model) {
        return new BaseConfigurableRegisterActivityPresenter(view, model);
    }


    protected BaseRegisterContract.Model createActivityModel() {
        return new BaseConfigurableRegisterActivityModel(getModuleConfiguration());
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    public void startFormActivity(String formName, String entityId, Map<String, String> metaData) {
        // Do nothing for now
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, getModuleConfiguration().getJsonFormActivity());
        // TODO: Add metadata & inject form values

        intent.putExtra(AllConstants.IntentExtra.JsonForm.JSON, jsonForm.toString());

        Form form = new Form();
        form.setActionBarBackground(R.color.form_actionbar);
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(AllConstants.IntentExtra.JsonForm.FORM, form);
        startActivityForResult(intent, AllConstants.RequestCode.START_JSON_FORM);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == AllConstants.RequestCode.START_JSON_FORM && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(AllConstants.IntentExtra.JsonForm.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(AllConstants.JSON.Property.ENCOUNTER_TYPE).equals(getModuleConfiguration().getModuleMetadata().getModuleRegister().getRegisterEventType())) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(JsonFormUtils.formTag(CoreLibrary.getInstance().context().allSharedPreferences()));

                    // showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void switchToBaseFragment() {
        // TODO: FIX THIS
        Intent intent = new Intent(this, BaseConfigurableRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startRegistration() {
        //do nothing
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        if (mBaseFragment instanceof BaseConfigurableRegisterFragment) {
            String locationId = CoreLibrary.getInstance().context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            // TODO: Finish this part adding
            presenter().startForm(formName, entityId, metaData, locationId, null, null);
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (mBaseFragment instanceof BaseConfigurableRegisterFragment) {
            String locationId = CoreLibrary.getInstance().context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, injectedFieldValues, entityTable);
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData) {
        ModuleMetadata moduleMetadata = getModuleConfiguration().getModuleMetadata();
        if (moduleMetadata != null) {
            Intent intent = new Intent(this, getModuleConfiguration().getJsonFormActivity());
            Form form = new Form();
            form.setActionBarBackground(R.color.form_actionbar);
            form.setWizard(false);
            form.setName("");

            /*String encounterType = jsonForm.optString(OpdJsonFormUtils.ENCOUNTER_TYPE);

            if (encounterType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
                form.setName(OpdConstants.EventType.DIAGNOSIS_AND_TREAT);
                form.setWizard(true);
            }*/

            form.setHideSaveLabel(true);
            form.setPreviousLabel("");
            form.setNextLabel("");
            form.setHideNextButton(false);
            form.setHidePreviousButton(false);

            intent.putExtra(AllConstants.IntentExtra.JsonForm.JSON, jsonForm.toString());
            intent.putExtra(AllConstants.IntentExtra.JsonForm.FORM, form);
            intent.putExtra(AllConstants.IntentExtra.MODULE_NAME, getModuleName());

            if (parcelableData != null) {
                for (String intentKey : parcelableData.keySet()) {
                    intent.putExtra(intentKey, parcelableData.get(intentKey));
                }
            }
            startActivityForResult(intent, AllConstants.RequestCode.START_JSON_FORM);
        } else {
            Timber.e(new Exception(), "FormActivity cannot be started because ModuleMetadata is NULL");
        }
    }
}