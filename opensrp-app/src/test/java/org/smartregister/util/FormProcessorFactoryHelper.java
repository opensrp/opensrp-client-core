package org.smartregister.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.view.activity.DrishtiApplication;

public class FormProcessorFactoryHelper {

    public static NativeFormProcessor createInstance(String jsonString) throws JSONException {
        return NativeFormProcessor.createInstance(jsonString, 1, ClientProcessorForJava.getInstance(DrishtiApplication.getInstance().getApplicationContext()));
    }

    public static NativeFormProcessor createInstance(JSONObject jsonObject) {
        return NativeFormProcessor.createInstance(jsonObject, 1, ClientProcessorForJava.getInstance(DrishtiApplication.getInstance().getApplicationContext()));
    }

    public static NativeFormProcessor createInstanceFromAsset(String filePath) throws JSONException {
        return NativeFormProcessor.createInstanceFromAsset(filePath, 1, ClientProcessorForJava.getInstance(DrishtiApplication.getInstance().getApplicationContext()));
    }
}
