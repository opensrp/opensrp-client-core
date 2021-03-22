package org.smartregister.util;

import org.apache.commons.validator.routines.UrlValidator;
import org.smartregister.EnvironmentManager;

public class UrlUtil {
    public static boolean isValidUrl(String s){
        return new UrlValidator(new String[]{"http", "https"}).isValid(s);
    }

    public static boolean isValidEnvironment(String url) {
        EnvironmentManager manager = EnvironmentManager.getInstance();
        return manager.getEnvironments().isEmpty() || manager.getEnvironment(url) != null;
    }
}
