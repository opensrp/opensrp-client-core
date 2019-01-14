package org.smartregister;

/**
 * Created by samuelgithengi on 10/19/18.
 */
public abstract class SyncConfiguration {

    public abstract int getSyncMaxRetries();

    public abstract String getSyncFilterParam();

    public abstract String getSyncFilterValue();

    public abstract int getUniqueIdSource();

    public abstract int getUniqueIdBatchSize();

    public abstract int getUniqueIdInitialBatchSize();

    public abstract boolean isSyncSettings();// determines whether to sync settings from server side. return false if not

}
