package org.opensrp.domain;

/**
 * Created by Dimas Ciputra on 3/23/15.
 */
public class FormDefinitionVersion {

    private String formName;
    private String formDataDefinitionVersion;
    private String formDirName;
    private SyncStatus syncStatus;
    private String entityId;

    public FormDefinitionVersion(String formName, String formDirName, String formDataDefinitionVersion) {
        this.formName = formName;
        this.formDirName = formDirName;
        this.formDataDefinitionVersion = formDataDefinitionVersion;
    }

    public FormDefinitionVersion withSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }

    public FormDefinitionVersion withFormId(String id) {
        this.entityId = id;
        return this;
    }

    public String getFormName() {
        return formName;
    }

    public String getVersion() {
        return formDataDefinitionVersion;
    }

    public String getFormDirName() { return formDirName; }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public String getEntityId() { return entityId; }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setVersion(String formDataDefinitionVersion) {
        this.formDataDefinitionVersion = formDataDefinitionVersion;
    }

    public void setFormDirName(String formDirName) {
        this.formDirName = formDirName;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }
}
