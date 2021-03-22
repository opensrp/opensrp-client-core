package org.smartregister;

import org.smartregister.domain.Environment;
import org.smartregister.repository.AllSharedPreferences;

public abstract class PropertiesSyncConfiguration extends SyncConfiguration {

    private final EnvironmentManager environmentManager;
    private final AllSharedPreferences preferences;

    public PropertiesSyncConfiguration(EnvironmentManager environmentManager, AllSharedPreferences preferences) {
        this.environmentManager = environmentManager;
        this.preferences = preferences;
    }

    @Override
    public String getOauthClientId() {
        return getCurrentEnvironment().getId();
    }

    @Override
    public String getOauthClientSecret() {
        return getCurrentEnvironment().getSecret();
    }

    private Environment getCurrentEnvironment() {
        Environment environment =
                environmentManager.getEnvironment(preferences.fetchBaseURL(AllConstants.DRISHTI_BASE_URL));
        if (environment != null) {
            return environment;
        } else {
            throw new IllegalStateException("No client found for the given url");
        }
    }

}
