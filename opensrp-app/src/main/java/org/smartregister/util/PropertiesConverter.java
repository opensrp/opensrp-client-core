package org.smartregister.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.smartregister.domain.LocationProperty;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by samuelgithengi on 11/30/18.
 */
public class PropertiesConverter implements JsonSerializer<LocationProperty> {

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    @Override
    public JsonElement serialize(LocationProperty src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = gson.toJsonTree(src).getAsJsonObject();
        if (src.getCustomProperties() != null) {
            for (Map.Entry<String, String> entryset : src.getCustomProperties().entrySet()) {
                object.addProperty(entryset.getKey(), entryset.getValue());
            }
        }
        return object;
    }
}
