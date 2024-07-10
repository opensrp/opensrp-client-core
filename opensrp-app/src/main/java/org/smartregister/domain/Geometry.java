package org.smartregister.domain;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class Geometry {

    public enum GeometryType {
        @SerializedName("Point")
        POINT,
        @SerializedName("Polygon")
        POLYGON,
        @SerializedName("MultiPolygon")
        MULTI_POLYGON
    };

    private GeometryType type;

    private JsonArray coordinates;

    public GeometryType getType() {
        return type;
    }

    public void setType(GeometryType type) {
        this.type = type;
    }

    public JsonArray getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(JsonArray coordinates) {
        this.coordinates = coordinates;
    }
}
