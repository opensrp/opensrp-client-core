package org.smartregister;

/**
 * Created by keyman on 31/07/17.
 */
public class CoreLibrary {
    private final Context context;

    private static CoreLibrary instance;

    private final SyncConfiguration syncConfiguration;

    public static void init(Context context, SyncConfiguration syncConfiguration) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration);
        }
    }

    public static CoreLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call "
                    + CoreLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class ");
        }
        return instance;
    }

    private CoreLibrary(Context contextArg, SyncConfiguration syncConfiguration) {
        context = contextArg;
        this.syncConfiguration = syncConfiguration;
    }

    public Context context() {
        return context;
    }

    /**
     * Use this method when testing.
     * It should replace org.smartregister.Context#setInstance(org.smartregister.Context) which has been removed
     *
     * @param context
     */
    public static void reset(Context context, SyncConfiguration syncConfiguration) {
        if (context != null) {
            instance = new CoreLibrary(context, syncConfiguration);
        }
    }

    public SyncConfiguration getSyncConfiguration() {
        return syncConfiguration;
    }
}
