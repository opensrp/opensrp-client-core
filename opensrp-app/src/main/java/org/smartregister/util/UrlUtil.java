package org.smartregister.util;

import org.apache.commons.validator.routines.UrlValidator;

public class UrlUtil {
    public static boolean isValidUrl(String s){
        return new UrlValidator(new String[]{"http", "https"}).isValid(s);
    }
}
