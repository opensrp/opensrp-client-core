package org.smartregister;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.authorizer.P2PSyncAuthorizationService;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.P2PReceiverTransferDao;
import org.smartregister.repository.P2PSenderTransferDao;

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
    private boolean enableP2pLibrary = false;
    private P2PAuthorizationService p2PAuthorizationService;
    private ReceiverTransferDao receiverTransferDao;
    private SenderTransferDao senderTransferDao;


    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, SyncConfiguration syncConfiguration) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration, false, null, null, null);
        }
    }

    public static void init(Context context, SyncConfiguration syncConfiguration, long buildTimestamp) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration, false, null, null, null);
            buildTimeStamp = buildTimestamp;
        }
    }

    public static void init(Context context, SyncConfiguration syncConfiguration, long buildTimestamp, @NonNull P2POptions options) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration, options.isEnableP2PLibrary(), options.getAuthorizationService(), options.getReceiverTransferDao(), options.getSenderTransferDao());
            buildTimeStamp = buildTimestamp;
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

    private CoreLibrary(Context contextArg, SyncConfiguration syncConfiguration, boolean enableP2pLibrary
            , @Nullable P2PAuthorizationService authorizationService
            , @Nullable ReceiverTransferDao receiverTransferDao, @Nullable SenderTransferDao senderTransferDao) {
        context = contextArg;
        this.syncConfiguration = syncConfiguration;
        this.enableP2pLibrary = enableP2pLibrary;
        this.p2PAuthorizationService = authorizationService;
        this.receiverTransferDao = receiverTransferDao;
        this.senderTransferDao = senderTransferDao;

        initP2pLibrary(null);
    }

    public void initP2pLibrary(@Nullable String username) {
        if (enableP2pLibrary) {
            String p2pUsername = username;
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(context.applicationContext()));
            if (p2pUsername == null) {
                p2pUsername = allSharedPreferences.fetchRegisteredANM();
            }

            if (!TextUtils.isEmpty(p2pUsername)) {
                String teamId = allSharedPreferences.fetchDefaultTeamId(p2pUsername);

                if (p2PAuthorizationService == null) {
                    p2PAuthorizationService = new P2PSyncAuthorizationService(teamId);
                }

                if (receiverTransferDao == null) {
                    receiverTransferDao = new P2PReceiverTransferDao();
                }

                if (senderTransferDao == null) {
                    senderTransferDao = new P2PSenderTransferDao();
                }

                P2PLibrary.Options options = new P2PLibrary.Options(context.applicationContext()
                        , teamId, p2pUsername, p2PAuthorizationService, receiverTransferDao, senderTransferDao);
                options.setBatchSize(250);
                P2PLibrary.init(options);
            }
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
            instance = new CoreLibrary(context, null, false, null, null, null);
        }
    }

    public static void reset(Context context, SyncConfiguration syncConfiguration) {
        if (context != null) {
            instance = new CoreLibrary(context, syncConfiguration, false, null, null, null);
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

    public static class P2POptions {

        private P2PAuthorizationService authorizationService;
        private ReceiverTransferDao receiverTransferDao;
        private SenderTransferDao senderTransferDao;

        private boolean enableP2PLibrary;

        public P2POptions(boolean enableP2PLibrary) {
            this.enableP2PLibrary = enableP2PLibrary;
        }

        public void setAuthorizationService(P2PAuthorizationService authorizationService) {
            this.authorizationService = authorizationService;
        }

        public void setReceiverTransferDao(ReceiverTransferDao receiverTransferDao) {
            this.receiverTransferDao = receiverTransferDao;
        }

        public void setSenderTransferDao(SenderTransferDao senderTransferDao) {
            this.senderTransferDao = senderTransferDao;
        }

        public P2PAuthorizationService getAuthorizationService() {
            return authorizationService;
        }

        public ReceiverTransferDao getReceiverTransferDao() {
            return receiverTransferDao;
        }

        public SenderTransferDao getSenderTransferDao() {
            return senderTransferDao;
        }

        public boolean isEnableP2PLibrary() {
            return enableP2PLibrary;
        }
    }
}
