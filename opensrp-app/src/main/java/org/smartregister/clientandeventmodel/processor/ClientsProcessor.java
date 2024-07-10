package org.smartregister.clientandeventmodel.processor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.processor.model.Client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by raihan on 3/15/16.
 */
public class ClientsProcessor {
    public static final String TAG = "ClientsProcessor";
    String baseEntityID;
    Map<String, String> attributesDetailsMap = new HashMap<String, String>();
    Map<String, String> attributesColumnsMap = new HashMap<String, String>();
    Map<String, String> propertyColumnsMap = new HashMap<String, String>();
    Map<String, String> propertyDetailsMap = new HashMap<String, String>();

    public ClientsProcessor(JSONObject EventMapConfig, JSONObject ClientJson) {
        try {
            JSONArray attributes = EventMapConfig.getJSONArray("attributes");
            JSONArray property = EventMapConfig.getJSONArray("property");

            Iterator<?> keys = ClientJson.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equalsIgnoreCase("baseEntityID")) {
                    baseEntityID = ClientJson.getString(key);
                } else {
                    if (key.equalsIgnoreCase("attributes")) {
                        Processattributes(property, ClientJson.getJSONObject("attributes"));
//                    baseEntityID = EventJson.getString(key);
                    } else {
                        if (isPropertyColumn(key, property)) {
                            propertyColumnsMap.put(key, ClientJson.getString(key));
                        } else {
                            propertyDetailsMap.put(key, ClientJson.getString(key));
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public Client createClientObject() {
        Client client = new Client(baseEntityID, attributesDetailsMap, attributesColumnsMap,
                propertyColumnsMap, propertyDetailsMap);
        return client;
    }

    private void Processattributes(JSONArray attributeColumns, JSONObject attributesInForm) {
        try {
            Iterator<?> keys = attributesInForm.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (isInAttributeColumn(key, attributeColumns)) {
                    attributesColumnsMap.put(key, attributesInForm.getString(key));
                } else {
                    attributesDetailsMap.put(key, attributesInForm.getString(key));
                }
            }
        } catch (Exception e) {

        }

    }

    private boolean isInAttributeColumn(String key, JSONArray attributesColumns) {
        boolean returnboolean = false;
        if (attributesColumns.length() == 0) {
            return returnboolean;
        }
        for (int i = 0; i < attributesColumns.length(); i++) {
            try {
                JSONObject obscolumnsObject = attributesColumns.getJSONObject(i);
                if (obscolumnsObject.getString("name").equalsIgnoreCase(key)) {
                    returnboolean = true;
                    i = attributesColumns.length();
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString(), e);
            }

        }
        return returnboolean;
    }

    private boolean isPropertyColumn(String key, JSONArray properties) {
        boolean returnboolean = false;
        if (properties.length() == 0) {
            return returnboolean;
        }
        for (int i = 0; i < properties.length(); i++) {
            try {
                JSONObject propertiescolumns = properties.getJSONObject(i);
                if (propertiescolumns.getString("name").equalsIgnoreCase(key)) {
                    returnboolean = true;
                    i = propertiescolumns.length();
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString(), e);
            }

        }
        return returnboolean;
    }
}
