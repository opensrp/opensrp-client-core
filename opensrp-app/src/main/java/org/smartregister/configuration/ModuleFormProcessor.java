package org.smartregister.configuration;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;

import java.util.HashMap;
import java.util.List;

public interface ModuleFormProcessor {

    HashMap<Client, List<Event>> extractEventClient(@NonNull String jsonString, @Nullable Intent data, @Nullable FormTag formTag) throws JSONException;

    default HashMap<String, String> getInjectableFields() {
        return null;
    }

    JSONObject getFormAsJson(@NonNull JSONObject form, @NonNull String formName, @NonNull String entityId, @NonNull String currentLocationId, @Nullable HashMap<String, String> injectedFieldValues) throws JSONException;

    boolean saveFormImages(Client client, List<Event> events, String formJsonString);

}
