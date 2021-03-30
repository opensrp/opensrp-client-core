package org.smartregister.configuration;

import java.util.List;

public class MockConfigViewsLib  implements ModuleConfiguration.ConfigurableViewsLibrary {
    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        // Do nothing for now
    }

    @Override
    public void unregisterViewConfigurations(List<String> viewIdentifiers) {
        // Do nothing for now
    }
}
