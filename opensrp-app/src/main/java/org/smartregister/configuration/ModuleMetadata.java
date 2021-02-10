package org.smartregister.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.FormActivity;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-09-2020.
 */
public class ModuleMetadata {

    private ModuleRegister moduleRegister;

    private Class<? extends FormActivity> formActivity;

    private Class<? extends BaseProfileActivity> profileActivity;

    private boolean formWizardValidateRequiredFieldsBefore;

    private ArrayList<String> locationLevels;

    private ArrayList<String> healthFacilityLevels;

    private Set<String> fieldsWithLocationHierarchy;

    private LocationTagsConfiguration locationTagsConfiguration;

    private String lookUpQueryForModuleClient;

    public ModuleMetadata(
            @NonNull ModuleRegister moduleRegister,
            @NonNull LocationTagsConfiguration locationTagsConfiguration,
            @NonNull Class<? extends FormActivity> formActivity,
            @Nullable Class<? extends BaseProfileActivity> profileActivity,
            boolean formWizardValidateRequiredFieldsBefore,
            @NonNull String lookUpQueryForModuleClient) {
        this.formActivity = formActivity;
        this.profileActivity = profileActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
        this.locationTagsConfiguration = locationTagsConfiguration;
        this.lookUpQueryForModuleClient = lookUpQueryForModuleClient;
    }

    public ModuleRegister getModuleRegister() {
        return moduleRegister;
    }

    public void setModuleRegister(ModuleRegister moduleRegister) {
        this.moduleRegister = moduleRegister;
    }

    public LocationTagsConfiguration getLocationTagsConfiguration() {
        return locationTagsConfiguration;
    }

    public void setLocationTagsConfiguration(LocationTagsConfiguration locationTagsConfiguration) {
        this.locationTagsConfiguration = locationTagsConfiguration;
    }

    public Class<? extends FormActivity> getFormActivity() {
        return formActivity;
    }

    public void setFormActivity(Class<? extends FormActivity> formActivity) {
        this.formActivity = formActivity;
    }

    public Class<? extends BaseProfileActivity> getProfileActivity() {
        return profileActivity;
    }

    public void setProfileActivity(Class<? extends BaseProfileActivity> profileActivity) {
        this.profileActivity = profileActivity;
    }

    public boolean isFormWizardValidateRequiredFieldsBefore() {
        return formWizardValidateRequiredFieldsBefore;
    }

    public void setFormWizardValidateRequiredFieldsBefore(boolean formWizardValidateRequiredFieldsBefore) {
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    @NonNull
    public ArrayList<String> getLocationLevels() {
        if (locationLevels == null) {
            locationLevels = locationTagsConfiguration.getLocationLevels();
        }

        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    @NonNull
    public ArrayList<String> getHealthFacilityLevels() {
        if (healthFacilityLevels == null) {
            healthFacilityLevels = locationTagsConfiguration.getHealthFacilityLevels();
        }

        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }

    public String getLookUpQueryForModuleClient() {
        return lookUpQueryForModuleClient;
    }

    public void setLookUpQueryForModuleClient(String lookUpQueryForModuleClient) {
        this.lookUpQueryForModuleClient = lookUpQueryForModuleClient;
    }

    public Set<String> getFieldsWithLocationHierarchy() {
        return fieldsWithLocationHierarchy;
    }

    public void setFieldsWithLocationHierarchy(Set<String> fieldsWithLocationHierarchy) {
        this.fieldsWithLocationHierarchy = fieldsWithLocationHierarchy;
    }
}