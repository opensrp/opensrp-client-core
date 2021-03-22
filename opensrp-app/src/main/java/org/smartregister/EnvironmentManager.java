package org.smartregister;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.domain.Environment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvironmentManager {

    private static EnvironmentManager instance;
    private Map<String, Environment> environments;

    private EnvironmentManager() {
    }

    public static EnvironmentManager getInstance() {
        if (instance == null) {
            instance = new EnvironmentManager();
            instance.environments = loadEnvironments().stream().collect(
                    Collectors.toMap(Environment::getUrl, item -> item)
            );
        }
        return instance;
    }

    private static List<Environment> loadEnvironments() {
        Gson gson = new Gson();
        return gson.fromJson(BuildConfig.ENV_ARRAY, new TypeToken<List<Environment>>() {
        }.getType());
    }

    public Collection<Environment> getEnvironments() {
        return environments.values();
    }

    public Environment getEnvironment(String url) {
        return environments.get(url);
    }
}
