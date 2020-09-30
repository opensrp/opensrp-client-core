package org.smartregister.sample.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.EventClient;
import org.smartregister.configuration.ModuleFormProcessor;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.sample.util.SampleAppJsonFormUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 30-09-2020.
 */
public class FormProcessor implements ModuleFormProcessor {

    @Override
    public HashMap<Client, List<Event>> extractEventClient(@NonNull String jsonString, @Nullable Intent data, @Nullable FormTag formTag) throws JSONException {
        EventClient eventClient = SampleAppJsonFormUtils.processRegistrationForm(jsonString, formTag);
        HashMap<Client, List<Event>> clientEvents = new HashMap<>();
        clientEvents.put(eventClient.getClient(), Arrays.asList(eventClient.getEvent()));
        return clientEvents;
    }

    @Override
    public JSONObject getFormAsJson(@NonNull JSONObject form, @NonNull String formName, @NonNull String entityId, @NonNull String currentLocationId, @Nullable HashMap<String, String> injectedFieldValues) throws JSONException {
        return SampleAppJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, injectedFieldValues);
    }
}
