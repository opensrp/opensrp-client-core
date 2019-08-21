package org.smartregister.util;

import android.util.Patterns;

public class UrlUtil {
    public static boolean isValidUrl(String s){
        return Patterns.WEB_URL.matcher(s).matches();
    }
}
