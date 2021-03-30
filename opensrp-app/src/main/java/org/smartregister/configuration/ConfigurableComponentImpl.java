package org.smartregister.configuration;

import androidx.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020.
 */
public class ConfigurableComponentImpl implements ConfigurableComponent {

    public String moduleName;

    @Override
    public void setModuleName(@NonNull String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }
}
