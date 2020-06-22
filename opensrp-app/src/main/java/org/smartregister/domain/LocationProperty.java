package org.smartregister.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class LocationProperty {

    public enum PropertyStatus {
        @SerializedName("Active")
        ACTIVE,
        @SerializedName("Inactive")
        INACTIVE,
        @SerializedName("Pending Review")
        PENDING_REVIEW,
        @SerializedName("Not Eligible")
        NOT_ELIGIBLE

    }

    private String uid;

    private String code;

    private String type;

    private PropertyStatus status;

    private String parentId;

    private String name;

    private int geographicLevel;

    private Date effectiveStartDate;

    private Date effectiveEndDate;

    private int version;

    private transient Map<String, String> customProperties = new HashMap<>();

    private String username;

    private Date updatedAt;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGeographicLevel() {
        return geographicLevel;
    }

    public void setGeographicLevel(int geographicLevel) {
        this.geographicLevel = geographicLevel;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
