package org.smartregister.util;

import org.json.JSONObject;

import java.util.Map;

/***
 * A common interface to provide a method for population of complex field values.
 * Allows users to specify how to read values of a custom field data type
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
