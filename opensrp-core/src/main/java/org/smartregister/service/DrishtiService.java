package org.smartregister.service;

import static org.smartregister.domain.ResponseStatus.failure;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.ei.drishti.dto.Action;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Response;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DrishtiService {
    private HTTPAgent agent = null;
    private String drishtiBaseURL;
    private final String ACTIONS_URI = "/actions";
    private final String ANM_IDENTIFIER = "anmIdentifier";
    private final String TIMESTAMP = "timeStamp";

    public DrishtiService(HTTPAgent agent, String drishtiBaseURL) {
        this.agent = agent;
        this.drishtiBaseURL = drishtiBaseURL;
    }

    public Response<List<Action>> fetchNewActions(String anmIdentifier, String previouslyFetchedIndex) {
        JSONObject requestBody = null;
        try {
            requestBody = new JSONObject();
            requestBody.put(ANM_IDENTIFIER, anmIdentifier);
            requestBody.put(TIMESTAMP, previouslyFetchedIndex);
        } catch (JSONException e) {
            Timber.e(e);
        }

        Response<String> response = agent.post(
                MessageFormat.format("{0}{1}", drishtiBaseURL, ACTIONS_URI),
                requestBody.toString()
        );

        Type collectionType = new TypeToken<List<Action>>() {
        }.getType();

        List<Action> actions;
        try {
            actions = new Gson().fromJson(response.payload(), collectionType);
        } catch (JsonSyntaxException e) {
            return new Response<List<Action>>(failure, new ArrayList<Action>());
        } catch (Exception e) {
            return new Response<List<Action>>(failure, new ArrayList<Action>());
        }

        return new Response<List<Action>>(
                response.status(),
                actions == null ? new ArrayList<Action>() : actions
        );
    }
}
