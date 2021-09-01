package org.smartregister.util;

import org.json.JSONObject;

import java.util.Map;

/***
 * A common interface to provide a method for population of complex field values
 */
public interface NativeFormProcessorFieldSource {

    /***
     *
     * @param step
     * @param fieldJson
     * @param dictionary
     * @param <T>
     */
    <T> void populateValue(String stepName, JSONObject step, JSONObject fieldJson, Map<String, T> dictionary);
}
