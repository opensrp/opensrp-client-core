package org.opensrp.domain;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by keyman on 3/1/17.
 */
public class Weight {
    private static final String ZEIR_ID = "ZEIR_ID";
    Long id;
    String baseEntityId;
    String eventId;
    String formSubmissionId;
    String programClientId;
    Float kg;
    Date date;
    String anmId;
    String locationId;
    String syncStatus;
    Integer outOfCatchment;
    Long updatedAt;
    Double zScore;

    public Weight() {
    }

    public Weight(Long id, String baseEntityId, Float kg, Date date, String anmId, String locationId, String syncStatus, Long updatedAt,String eventId,String formSubmissionId,Integer outOfCatchment) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.programClientId = null;
        this.kg = kg;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.updatedAt = updatedAt;
        this.eventId=eventId;
        this.formSubmissionId=formSubmissionId;
        this.outOfCatchment=outOfCatchment;
    }

    public Weight(Long id, String baseEntityId, String programClientId, Float kg, Date date, String anmId, String locationId, String syncStatus, Long updatedAt,String eventId,String formSubmissionId, Double zScore,Integer outOfCatchment) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.programClientId = programClientId;
        this.kg = kg;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.updatedAt = updatedAt;
        this.eventId=eventId;
        this.formSubmissionId=formSubmissionId;
        this.outOfCatchment=outOfCatchment;
        this.zScore = zScore;
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
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put(ZEIR_ID, programClientId);
        return identifiers;
    }

    public Float getKg() {
        return kg;
    }

    public void setKg(Float kg) {
        this.kg = kg;
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
    public Double getZScore() {
        return zScore;
    }

    public void setZScore(Double zScore) {
        this.zScore = zScore;
    }
}
