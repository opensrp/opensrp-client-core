package org.smartregister;

/**
 * Created by samuelgithengi on 10/19/18.
 */
public abstract class SyncConfiguration {

    public abstract int getSyncMaxRetries();

    public abstract SyncFilter getSyncFilterParam();

    public abstract String getSyncFilterValue();

    public abstract int getUniqueIdSource();

    public abstract int getUniqueIdBatchSize();

    public abstract int getUniqueIdInitialBatchSize();

    // determines whether to sync settings from server side. return false if not
    public boolean isSyncSettings() {
        return false;
    }

    /**
     * Flag that determines whether to sync the data that is on the device to the server if user's account is disabled
     *
     * @return true to disable sync or false to sync data before logout
     */
    public boolean disableSyncToServerIfUserIsDisabled() {
        return false;
    }

    public abstract SyncFilter getEncryptionParam();

    public abstract boolean updateClientDetailsTable();
}
