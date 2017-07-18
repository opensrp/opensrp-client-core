package org.ei.opensrp.domain;

/**
 * Created by keyman on 3/1/17.
 */
public class ServiceType {
    Long id;
    String type;
    String name;
    String serviceNameEntity;
    String serviceNameEntityId;
    String dateEntity;
    String dateEntityId;
    String units;
    String serviceLogic;
    String prerequisite;
    String preOffset;
    String expiryOffset;
    String milestoneOffset;
    Long updatedAt;


    public ServiceType() {
    }

    public ServiceType(Long id, String type, String name, String serviceNameEntity, String serviceNameEntityId, String dateEntity, String dateEntityId, String units, String serviceLogic, String prerequisite, String preOffset, String expiryOffset, String milestoneOffset, Long updatedAt) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.serviceNameEntity = serviceNameEntity;
        this.serviceNameEntityId = serviceNameEntityId;
        this.dateEntity = dateEntity;
        this.dateEntityId = dateEntityId;
        this.units = units;
        this.serviceLogic = serviceLogic;
        this.prerequisite = prerequisite;
        this.preOffset = preOffset;
        this.expiryOffset = expiryOffset;
        this.milestoneOffset = milestoneOffset;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceNameEntity() {
        return serviceNameEntity;
    }

    public void setServiceNameEntity(String serviceNameEntity) {
        this.serviceNameEntity = serviceNameEntity;
    }

    public String getServiceNameEntityId() {
        return serviceNameEntityId;
    }

    public void setServiceNameEntityId(String serviceNameEntityId) {
        this.serviceNameEntityId = serviceNameEntityId;
    }

    public String getDateEntity() {
        return dateEntity;
    }

    public void setDateEntity(String dateEntity) {
        this.dateEntity = dateEntity;
    }

    public String getDateEntityId() {
        return dateEntityId;
    }

    public void setDateEntityId(String dateEntityId) {
        this.dateEntityId = dateEntityId;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getServiceLogic() {
        return serviceLogic;
    }

    public void setServiceLogic(String serviceLogic) {
        this.serviceLogic = serviceLogic;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getPreOffset() {
        return preOffset;
    }

    public void setPreOffset(String preOffset) {
        this.preOffset = preOffset;
    }

    public String getExpiryOffset() {
        return expiryOffset;
    }

    public void setExpiryOffset(String expiryOffset) {
        this.expiryOffset = expiryOffset;
    }

    public String getMilestoneOffset() {
        return milestoneOffset;
    }

    public void setMilestoneOffset(String milestoneOffset) {
        this.milestoneOffset = milestoneOffset;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
