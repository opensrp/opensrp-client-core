package org.ei.opensrp.domain;

public enum SyncStatus {
    SYNCED("SYNCED"), PENDING("PENDING");
    private String value;

    private SyncStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
