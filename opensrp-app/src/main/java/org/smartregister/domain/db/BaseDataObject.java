package org.smartregister.domain.db;

import org.joda.time.DateTime;

public abstract class BaseDataObject {

    private String creator;

    private DateTime dateCreated;

    private String editor;

    private DateTime dateEdited;

    private Boolean voided;

    private DateTime dateVoided;

    private String voider;

    private String voidReason;

    private long serverVersion;

    private Integer clientApplicationVersion;

    private Integer clientDatabaseVersion;

    public BaseDataObject() {
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public DateTime getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(DateTime dateEdited) {
        this.dateEdited = dateEdited;
    }

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public DateTime getDateVoided() {
        return dateVoided;
    }

    public void setDateVoided(DateTime dateVoided) {
        this.dateVoided = dateVoided;
    }

    public String getVoider() {
        return voider;
    }

    public void setVoider(String voider) {
        this.voider = voider;
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(String voidReason) {
        this.voidReason = voidReason;
    }

    public long getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(long serverVersion) {
        this.serverVersion = serverVersion;
    }

    public Integer getClientApplicationVersion() {
        return clientApplicationVersion;
    }

    public void setClientApplicationVersion(Integer clientApplicationVersion) {
        this.clientApplicationVersion = clientApplicationVersion;
    }

    public Integer getClientDatabaseVersion() {
        return clientDatabaseVersion;
    }

    public void setClientDatabaseVersion(Integer clientDatabaseVersion) {
        this.clientDatabaseVersion = clientDatabaseVersion;
    }

    public BaseDataObject withCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public BaseDataObject withDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public BaseDataObject withEditor(String editor) {
        this.editor = editor;
        return this;
    }

    public BaseDataObject withDateEdited(DateTime dateEdited) {
        this.dateEdited = dateEdited;
        return this;
    }

    public BaseDataObject withVoided(Boolean voided) {
        this.voided = voided;
        return this;
    }

    public BaseDataObject withDateVoided(DateTime dateVoided) {
        this.dateVoided = dateVoided;
        return this;
    }

    public BaseDataObject withVoider(String voider) {
        this.voider = voider;
        return this;
    }

    public BaseDataObject withVoidReason(String voidReason) {
        this.voidReason = voidReason;
        return this;
    }

    public BaseDataObject withServerVersion(long serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    public BaseDataObject withClientApplicationVersion(Integer clientApplicationVersion) {
        this.clientApplicationVersion = clientApplicationVersion;
        return this;
    }

    public BaseDataObject withClientDatabaseVersion(Integer clientDatabaseVersion) {
        this.clientDatabaseVersion = clientDatabaseVersion;
        return this;
    }
}
