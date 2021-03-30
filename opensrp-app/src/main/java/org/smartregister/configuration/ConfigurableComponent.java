package org.smartregister.configuration;

import androidx.annotation.NonNull;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020.
 */
public interface ConfigurableComponent {

    void setModuleName(@NonNull String moduleName);

    String getModuleName();
}
