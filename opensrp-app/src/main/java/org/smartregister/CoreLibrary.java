package org.smartregister;

import android.text.TextUtils;

import org.smartregister.authorizer.P2PSyncAuthorizationService;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.repository.AllSharedPreferences;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by keyman on 31/07/17.
 */
public class CoreLibrary {
    private final Context context;

    private static CoreLibrary instance;

    private final SyncConfiguration syncConfiguration;
    private static long buildTimeStamp;

    private String ecClientFieldsFile = "ec_client_fields.json";

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, SyncConfiguration syncConfiguration) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration);
        }
    }

    public static void init(Context context, SyncConfiguration syncConfiguration, long buildVersion) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration);
            buildTimeStamp = buildVersion;
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

        initP2pLibrary();
    }

    private void initP2pLibrary() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(context.applicationContext()));
        String username = allSharedPreferences.fetchRegisteredANM();

        if (!TextUtils.isEmpty(username)) {
            P2PLibrary.init(new P2PLibrary.Options(username
                    , new P2PSyncAuthorizationService(allSharedPreferences.fetchDefaultTeamId(username))));
        }
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
    public static void reset(Context context) {
        if (context != null) {
            instance = new CoreLibrary(context, null);
        }
    }

    public static void reset(Context context, SyncConfiguration syncConfiguration) {
        if (context != null) {
            instance = new CoreLibrary(context, syncConfiguration);
        }
    }

    public SyncConfiguration getSyncConfiguration() {
        if (syncConfiguration == null) {
            throw new IllegalStateException(" Instance does not exist!!! Call "
                    + CoreLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class ");
        }
        return syncConfiguration;
    }

    public static long getBuildTimeStamp() {
        return buildTimeStamp;
    }

    public String getEcClientFieldsFile() {
        return ecClientFieldsFile;
    }

    public void setEcClientFieldsFile(String ecClientFieldsFile) {
        this.ecClientFieldsFile = ecClientFieldsFile;
    }
}
