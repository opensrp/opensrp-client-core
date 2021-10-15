package org.smartregister.sync.intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;

import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * A helper class for building the url request for intent services
 */
public class RequestParamsBuilder {

    private final Map<String, Object> paramMap;
    private final StringBuilder getSyncParamsBuilder;
    private final JSONObject postSyncParamsBuilder;

    public RequestParamsBuilder() {
        this.paramMap = new LinkedHashMap<>();
        this.getSyncParamsBuilder = new StringBuilder();
        this.postSyncParamsBuilder = new JSONObject();
    }

    public RequestParamsBuilder addServerVersion(long value) {
        paramMap.put(AllConstants.SERVER_VERSION, value);
        return this;
    }

    public RequestParamsBuilder addEventPullLimit(int value) {
        paramMap.put(AllConstants.LIMIT, value);
        return this;
    }

    public RequestParamsBuilder configureSyncFilter(String syncFilterParam, String syncFilterValue) {
        paramMap.put(syncFilterParam, syncFilterValue);
        return this;
    }

    public RequestParamsBuilder returnCount(boolean value) {
        paramMap.put(AllConstants.RETURN_COUNT, value);
        return this;
    }

    public RequestParamsBuilder addParam(String key, Object value) {
        paramMap.put(key, value);
        return this;
    }

    public RequestParamsBuilder removeParam(String key) {
        paramMap.remove(key);
        return this;
    }

    public String build() {

        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {

            if (CoreLibrary.getInstance().getSyncConfiguration().isSyncUsingPost()) {

                try {
                    postSyncParamsBuilder.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    Timber.e(e);
                }

            } else {

                if (0 != getSyncParamsBuilder.length()) {
                    getSyncParamsBuilder.append('&');
                }

                getSyncParamsBuilder.append(entry.getKey()).append('=').append(entry.getValue());
            }

        }

        return CoreLibrary.getInstance().getSyncConfiguration().isSyncUsingPost() ? postSyncParamsBuilder.toString() : getSyncParamsBuilder.toString();
    }

    @Override
    public String toString() {
        return build();
    }

}
