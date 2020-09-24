package org.smartregister.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public interface ModuleFormProcessor<T> {
    T processForm(@NonNull JSONObject jsonObject, @NonNull Intent data) throws JSONException;
}
