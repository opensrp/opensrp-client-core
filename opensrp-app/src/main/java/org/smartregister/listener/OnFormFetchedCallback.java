package org.smartregister.listener;

import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-05-2020.
 */
public interface OnFormFetchedCallback {

    void onFormFetched(@Nullable JSONObject jsonObject);
}
