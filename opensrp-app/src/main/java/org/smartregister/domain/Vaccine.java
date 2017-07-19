package org.smartregister.domain;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by keyman on 3/1/17.
 */
public class Vaccine {
    private static final String ZEIR_ID = "ZEIR_ID";
    private Long id;
    private String baseEntityId;
    private String programClientId;
    private String name;
    private Integer calculation;
    private Date date;
    private String anmId;
    private String locationId;
    private String syncStatus;
    private String hia2Status;
    private Long updatedAt;
    private String eventId;
    private String formSubmissionId;
    private Integer outOfCatchment;


    public Vaccine() {
    }

    public Vaccine(Long id, String baseEntityId, String name, Integer calculation, Date date, String anmId, String locationId, String syncStatus, String hia2Status, Long updatedAt,String eventId,String formSubmissionId,Integer outOfCatchment) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.programClientId = null;
        this.name = name;
        this.calculation = calculation;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.hia2Status = hia2Status;
        this.updatedAt = updatedAt;
        this.eventId=eventId;
        this.formSubmissionId=formSubmissionId;
        this.outOfCatchment=outOfCatchment;
    }

    public Vaccine(Long id, String baseEntityId, String programClientId, String name, Integer calculation, Date date, String anmId, String locationId, String syncStatus, String hia2Status, Long updatedAt,String eventId,String formSubmissionId,Integer outOfCatchment) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.programClientId = programClientId;
        this.name = name;
        this.calculation = calculation;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.hia2Status = hia2Status;
        this.updatedAt = updatedAt;
        this.eventId=eventId;
        this.formSubmissionId=formSubmissionId;
        this.outOfCatchment=outOfCatchment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getProgramClientId() {
        return programClientId;
    }

    public void setProgramClientId(String programClientId) {
        this.programClientId = programClientId;
    }

    public HashMap<String, String> getIdentifiers() {
        HashMap<String, String> identifiers = null;
        if (programClientId != null) {
            identifiers = new HashMap<>();
            identifiers.put(ZEIR_ID, programClientId);
        }
        return identifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCalculation() {
        return calculation;
    }

    public void setCalculation(Integer calculation) {
        this.calculation = calculation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getHia2Status() {
        return hia2Status;
    }

    public void setHia2Status(String hia2Status) {
        this.hia2Status = hia2Status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }
    public Integer getOutOfCatchment() {
        return outOfCatchment;
    }

    public void setOutOfCatchment(Integer outOfCatchment) {
        this.outOfCatchment = outOfCatchment;
    }
}
