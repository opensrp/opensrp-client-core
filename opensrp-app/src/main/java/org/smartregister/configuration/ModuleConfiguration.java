package org.smartregister.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.client.utils.contract.ClientFormContract;

import java.util.HashMap;
import java.util.List;

/**
 * This is the object used to configure any configurations added to a module. We mostly use objects that are
 * instantiated using {@link org.smartregister.util.ConfigurationInstancesHelper} which means
 * that the constructors of any of the classes should not have any parameters
 * <p>
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020
 */

public class ModuleConfiguration {

    private Builder builder;

    private ModuleConfiguration(@NonNull Builder builder) {
        this.builder = builder;

        setDefaults();
    }

    private void setDefaults() {
        if (builder.registerProviderMetadata == null) {
            builder.registerProviderMetadata = BaseRegisterProviderMetadata.class;
        }
    }

    @Nullable
    public ModuleMetadata getModuleMetadata() {
        return builder.moduleMetadata;
    }

    @NonNull
    public Class<? extends RegisterProviderMetadata> getRegisterProviderMetadata() {
        return builder.registerProviderMetadata;
    }

    @Nullable
    public Class<? extends BaseRegisterRowOptions> getRegisterRowOptions() {
        return builder.registerRowOptions;
    }

    @NonNull
    public Class<? extends ModuleRegisterQueryProviderContract> getRegisterQueryProvider() {
        return builder.registerQueryProvider;
    }

    @NonNull
    public ConfigurableViewsLibrary getConfigurableViewsLibrary() {
        return builder.configurableViewsLibrary;
    }


    @Nullable
    public Class<? extends ModuleFormProcessor> getFormProcessingClass(String eventType) {
        return builder.formProcessingMap.get(eventType);
    }

    @NonNull
    public Class<? extends ModuleFormProcessor> getFormProcessorClass() {
        return builder.moduleFormProcessorClass;
    }

    @NonNull
    public Class<? extends ClientFormContract.View> getJsonFormActivity() {
        return builder.jsonFormActivity;
    }

    @NonNull
    public String getRegisterTitle() {
        return builder.registerTitle;
    }

    public int getMaxCheckInDurationInMinutes() {
        return builder.maxCheckInDurationInMinutes;
    }

    public boolean isBottomNavigationEnabled() {
        return builder.isBottomNavigationEnabled;
    }

    public boolean isNewLayoutEnabled() {
        return builder.isNewLayoutEnabled;
    }

    @NonNull
    public Class<? extends ActivityStarter> getActivityStarter() {
        return builder.activityStarter;
    }

    public static class Builder {

        @Nullable
        private Class<? extends RegisterProviderMetadata> registerProviderMetadata;

        @Nullable
        private Class<? extends BaseRegisterRowOptions> registerRowOptions;

        @NonNull
        private Class<? extends ModuleRegisterQueryProviderContract> registerQueryProvider;

        // TODO: FIX THIS
        @Nullable
        private Class<? extends ModuleFormProcessor> moduleFormProcessorClass;

        @NonNull
        private HashMap<String, Class<? extends ModuleFormProcessor>> formProcessingMap = new HashMap<>();

        @NonNull
        private Class<? extends ActivityStarter> activityStarter;

        @NonNull
        private Class<? extends ClientFormContract.View> jsonFormActivity;

        private boolean isBottomNavigationEnabled;
        private boolean isNewLayoutEnabled;

        private ModuleMetadata moduleMetadata;
        private int maxCheckInDurationInMinutes = 24 * 60;
        private ConfigurableViewsLibrary configurableViewsLibrary;
        private String registerTitle;

        public Builder(@NonNull String registerTitle, @NonNull Class<? extends ModuleRegisterQueryProviderContract> registerQueryProvider, @NonNull ConfigurableViewsLibrary configurableViewsLibrary, @NonNull Class<? extends ActivityStarter> activityStarter) {
            this.registerQueryProvider = registerQueryProvider;
            this.configurableViewsLibrary = configurableViewsLibrary;
            this.registerTitle = registerTitle;
            this.activityStarter = activityStarter;
        }

        public Builder setRegisterProviderMetadata(@Nullable Class<? extends RegisterProviderMetadata> registerProviderMetadata) {
            this.registerProviderMetadata = registerProviderMetadata;
            return this;
        }

        public Builder setRegisterRowOptions(@Nullable Class<? extends BaseRegisterRowOptions> registerRowOptions) {
            this.registerRowOptions = registerRowOptions;
            return this;
        }

        public Builder setNewLayoutEnabled(boolean isNewLayoutEnabled) {
            this.isNewLayoutEnabled = isNewLayoutEnabled;
            return this;
        }

        public Builder setBottomNavigationEnabled(boolean isBottomNavigationEnabled) {
            this.isBottomNavigationEnabled = isBottomNavigationEnabled;
            return this;
        }

        public Builder setModuleMetadata(@NonNull ModuleMetadata moduleMetadata) {
            this.moduleMetadata = moduleMetadata;
            return this;
        }

        public Builder setMaxCheckInDurationInMinutes(int durationInMinutes) {
            this.maxCheckInDurationInMinutes = durationInMinutes;
            return this;
        }

        public Builder addModuleFormProcessingClass(String eventType, Class<? extends ModuleFormProcessor> moduleFormProcessor) {
            this.formProcessingMap.put(eventType, moduleFormProcessor);
            return this;
        }

        public Builder setModuleFormProcessorClass(@Nullable Class<? extends ModuleFormProcessor> moduleFormProcessorClass) {
            this.moduleFormProcessorClass = moduleFormProcessorClass;
            return this;
        }

        public Builder setJsonFormActivity(@NonNull Class<? extends ClientFormContract.View> jsonFormActivity) {
            this.jsonFormActivity = jsonFormActivity;
            return this;
        }

        public ModuleConfiguration build() {
            return new ModuleConfiguration(this);
        }

    }

    public interface ConfigurableViewsLibrary {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfigurations(List<String> viewIdentifiers);
    }

}
