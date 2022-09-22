package org.smartregister.domain;

public enum DuplicateZeirIdStatus {
    CLEANED("CLEANED"), PENDING("PENDING");
    private String value;

    DuplicateZeirIdStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
