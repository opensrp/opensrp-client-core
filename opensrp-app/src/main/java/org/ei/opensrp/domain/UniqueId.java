package org.ei.opensrp.domain;

import java.util.Date;

/**
 * Created by coder on 2/15/17.
 */
public class UniqueId {
    String id;
    String openmrsId;
    String status;
    String usedBy;
    Date createdAt;
    Date updatedAt;

    public UniqueId(){}

    public UniqueId(String id, String openmrsId, String status, String usedBy, Date createdAt){
        this.id=id;
        this.openmrsId=openmrsId;
        this.status=status;
        this.usedBy=usedBy;
        this.createdAt=createdAt;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenmrsId() {
        return openmrsId;
    }

    public void setOpenmrsId(String openmrsId) {
        this.openmrsId = openmrsId;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


}
