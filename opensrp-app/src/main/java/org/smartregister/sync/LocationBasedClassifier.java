package org.smartregister.sync;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.p2p.model.DataType;

import timber.log.Timber;

public class LocationBasedClassifier implements P2PClassifier<JSONObject> {

    @Override
    public boolean isForeign(JSONObject jsonObject, DataType dataType) {
        if (dataType.getName().equals(AllConstants.P2PDataTypes.CLIENT) || dataType.getName().equals(AllConstants.P2PDataTypes.FOREIGN_CLIENT))
            return isForeignClient(jsonObject);

        if (dataType.getName().equals(AllConstants.P2PDataTypes.EVENT) || dataType.getName().equals(AllConstants.P2PDataTypes.FOREIGN_EVENT))
            return isForeignEvent(jsonObject);

        return false;
    }

    private boolean isForeignEvent(JSONObject jsonObject) {
        try {
            String locationID = jsonObject.getString("locationId");
            return isChildLocation(locationID);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return false;
    }

    private boolean isForeignClient(JSONObject jsonObject) {
        try {
            String locationID = jsonObject.getString("locationId");
            jsonObject.remove("locationId");
            return isChildLocation(locationID);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return false;
    }

    // TODO check if param location is under the current logged in location
    private boolean isChildLocation(String locationID) {
        return false;
    }
}
