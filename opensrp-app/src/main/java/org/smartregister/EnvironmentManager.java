package org.smartregister;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.domain.Environment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvironmentManager {
    private final Map<String, Environment> environments;

    public EnvironmentManager(String json) {
        environments = loadEnvironments(json).stream().collect(
                Collectors.toMap(this::getKey, item -> item)
        );
    }

    private List<Environment> loadEnvironments(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<Environment>>() {
        }.getType());
    }

    public Collection<Environment> getEnvironments() {
        return environments.values();
    }

    public Environment getEnvironment(String url) {
        return environments.get(getKey(url));
    }

    private String getKey(Environment env) {
        return getKey(env.getUrl());
    }
    private String getKey(String url) {
        return url.replace("/", "");
    }
}
