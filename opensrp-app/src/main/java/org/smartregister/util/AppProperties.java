package org.smartregister.util;

import java.util.Properties;

/**
 * Helper class for working with the settings in app.properties file
 * <p>
 * Created by ndegwamartin on 2019-07-23.
 */
public class AppProperties extends Properties {

    /**
     * @param key key as present in the properties file
     * @return Boolean value of the key's string value, returns false if no entry exists
     */
    public Boolean getPropertyBoolean(String key) {

        return Boolean.valueOf(getProperty(key));
    }

    /**
     * @param key key as present in the properties file
     * @return returns true if a property with the provided key exists, false otherwise
     */
    public Boolean hasProperty(String key) {

        return getProperty(key) != null;
    }
}
