package org.smartregister.configuration;

import androidx.annotation.NonNull;

public class ModuleRegister {

    private String registrationFormName;

    private String tableName;

    private String registerEventType;

    private String updateEventType;

    private String config;

    public ModuleRegister(
            @NonNull
                    String registrationFormName,
            @NonNull String tableName,
            @NonNull String registerEventType,
            @NonNull String updateEventType,
            @NonNull String config) {
        this.registrationFormName = registrationFormName;
        this.tableName = tableName;
        this.registerEventType = registerEventType;
        this.updateEventType = updateEventType;
        this.config = config;
    }

    public String getRegistrationFormName() {
        return registrationFormName;
    }

    public void setRegistrationFormName(String registrationFormName) {
        this.registrationFormName = registrationFormName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRegisterEventType() {
        return registerEventType;
    }

    public void setRegisterEventType(String registerEventType) {
        this.registerEventType = registerEventType;
    }

    public String getUpdateEventType() {
        return updateEventType;
    }

    public void setUpdateEventType(String updateEventType) {
        this.updateEventType = updateEventType;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
