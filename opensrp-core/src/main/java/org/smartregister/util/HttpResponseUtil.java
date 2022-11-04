package org.smartregister.util;

import static org.smartregister.util.Log.logError;
import static java.text.MessageFormat.format;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.smartregister.domain.jsonmapping.LoginResponseData;

public class HttpResponseUtil {

    public static LoginResponseData getResponseBody(String responseString) {
        try {
            if (StringUtils.isBlank(responseString)) {
                return null;
            }
            return AssetHandler.jsonStringToJava(responseString, LoginResponseData.class);
        } catch (Exception e) {
            logError(format("Cannot read data from response due to exception: {0}. Stack "
                    + "trace: {1}", e.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return null;
    }

}
