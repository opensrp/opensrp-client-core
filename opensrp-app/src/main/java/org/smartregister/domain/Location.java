package org.smartregister.domain;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class Location {

    private String type;

    private String id;

    private Geometry geometry;

    private LocationProperty properties;

    private String syncStatus;

    private Long serverVersion;

    private transient boolean isJurisdiction;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public LocationProperty getProperties() {
        return properties;
    }

    public void setProperties(LocationProperty properties) {
        this.properties = properties;
    }

    public Long getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(Long serverVersion) {
        this.serverVersion = serverVersion;
    }

    public boolean isJurisdiction() {
        return isJurisdiction;
    }

    public void setJurisdiction(boolean isJurisdiction) {
        this.isJurisdiction = isJurisdiction;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }
}
