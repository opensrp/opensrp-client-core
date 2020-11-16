package org.smartregister;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.authorizer.P2PSyncAuthorizationService;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.P2PReceiverTransferDao;
import org.smartregister.repository.P2PSenderTransferDao;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.dao.ClientDaoImpl;
import org.smartregister.repository.dao.EventDaoImpl;
import org.smartregister.repository.dao.LocationDaoImpl;
import org.smartregister.repository.dao.TaskDaoImpl;
import org.smartregister.sync.P2PSyncFinishCallback;
import org.smartregister.util.CredentialsHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseConfigurableRegisterActivity;

import java.util.HashMap;

import timber.log.Timber;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by keyman on 31/07/17.
 */
public class CoreLibrary implements OnAccountsUpdateListener {

    private final Context context;

    private static CoreLibrary instance;

    private final SyncConfiguration syncConfiguration;
    private static long buildTimeStamp;

    private boolean isPeerToPeerProcessing = false;

    private String ecClientFieldsFile = "ec_client_fields.json";

    private P2POptions p2POptions;

    private AccountManager accountManager;

    private AccountAuthenticatorXml authenticatorXml;

    private HashMap<String, ModuleConfiguration> moduleConfigurations = new HashMap<>();
    private String defaultModule;
    private String currentModule;

    private int databaseVersion;
    private int applicationVersion;

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, SyncConfiguration syncConfiguration) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration, null, -1, -1);
        }
    }

    public static void init(Context context, SyncConfiguration syncConfiguration, long buildTimestamp) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration, null, -1, -1);
            buildTimeStamp = buildTimestamp;
        }
    }

    public static void init(Context context, SyncConfiguration syncConfiguration, long buildTimestamp, @NonNull P2POptions options) {
        init(context, syncConfiguration, buildTimestamp, options, -1, -1);
    }


    public static void init(Context context, SyncConfiguration syncConfiguration, long buildTimestamp, @NonNull P2POptions options, int databaseVersion, int applicationVersion) {
        if (instance == null) {
            instance = new CoreLibrary(context, syncConfiguration, options, databaseVersion, applicationVersion);
            buildTimeStamp = buildTimestamp;
            checkPlatformMigrations();
        }
    }


    private static void checkPlatformMigrations() {
        boolean shouldMigrate = CredentialsHelper.shouldMigrate();
        if (shouldMigrate && StringUtils.isNotBlank(instance.context().userService().getAllSharedPreferences().fetchPioneerUser())) {//Force remote login
            Utils.logoutUser(instance.context(), instance.context().applicationContext().getString(R.string.new_db_encryption_version_migration));
        }
        instance.context().userService().getAllSharedPreferences().migratePassphrase();
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

    protected CoreLibrary(Context contextArg, SyncConfiguration syncConfiguration, @Nullable P2POptions p2POptions, int databaseVersion, int applicationVersion) {
        context = contextArg;
        this.syncConfiguration = syncConfiguration;
        this.p2POptions = p2POptions;

        initP2pLibrary(null);
        if (syncConfiguration != null && syncConfiguration.runPlanEvaluationOnClientProcessing()) {
            PathEvaluatorLibrary.init(new LocationDaoImpl(), new ClientDaoImpl(), new TaskDaoImpl(new TaskNotesRepository()), new EventDaoImpl());
        }
    }

    public void initP2pLibrary(@Nullable String username) {
        if (p2POptions != null && p2POptions.isEnableP2PLibrary()) {
            String p2pUsername = username;
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(context.applicationContext()));
            if (p2pUsername == null) {
                p2pUsername = allSharedPreferences.fetchRegisteredANM();
            }

            if (!TextUtils.isEmpty(p2pUsername)) {
                String teamId = allSharedPreferences.fetchDefaultTeamId(p2pUsername);

                if (p2POptions.getAuthorizationService() == null) {
                    p2POptions.setAuthorizationService(new P2PSyncAuthorizationService(teamId));
                }

                if (p2POptions.getReceiverTransferDao() == null) {
                    p2POptions.setReceiverTransferDao(new P2PReceiverTransferDao());
                }

                if (p2POptions.getSenderTransferDao() == null) {
                    p2POptions.setSenderTransferDao(new P2PSenderTransferDao());
                }

                if (p2POptions.getSyncFinishedCallback() == null) {
                    p2POptions.setSyncFinishedCallback(new P2PSyncFinishCallback());
                }

                P2PLibrary.Options options = new P2PLibrary.Options(context.applicationContext()
                        , teamId, p2pUsername, p2POptions.getAuthorizationService(), p2POptions.getReceiverTransferDao()
                        , p2POptions.getSenderTransferDao());
                options.setBatchSize(p2POptions.getBatchSize());
                options.setSyncFinishedCallback(p2POptions.getSyncFinishedCallback());
                options.setRecalledIdentifier(p2POptions.getRecalledIdentifier());

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
            instance = new CoreLibrary(context, null, null, -1, -1);
        }
    }

    public static void reset(Context context, SyncConfiguration syncConfiguration) {
        if (context != null) {
            instance = new CoreLibrary(context, syncConfiguration, null, -1, -1);
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

    public AccountManager getAccountManager() {
        if (accountManager == null) {
            accountManager = AccountManager.get(context.applicationContext());
            accountManager.addOnAccountsUpdatedListener(this, null, true);
        }

        return accountManager;
    }

    public AccountAuthenticatorXml getAccountAuthenticatorXml() {
        if (authenticatorXml == null)
            authenticatorXml = Utils.parseAuthenticatorXMLConfigData(context.applicationContext());

        return authenticatorXml;
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

    @Nullable
    public P2POptions getP2POptions() {
        return p2POptions;
    }

    public boolean isPeerToPeerProcessing() {
        return isPeerToPeerProcessing;
    }

    public void setPeerToPeerProcessing(boolean peerToPeerProcessing) {
        isPeerToPeerProcessing = peerToPeerProcessing;

        sendPeerToPeerProcessingStatus(isPeerToPeerProcessing);
    }

    private void sendPeerToPeerProcessingStatus(boolean status) {
        Intent intent = new Intent();
        intent.setAction(AllConstants.PeerToPeer.PROCESSING_ACTION);
        intent.putExtra(AllConstants.PeerToPeer.KEY_IS_PROCESSING, status);

        LocalBroadcastManager.getInstance(context.applicationContext())
                .sendBroadcast(intent);
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        if (context.allSharedPreferences().getDBEncryptionVersion() > 0) {
            try {
                String loggedInUser = context.allSharedPreferences().fetchRegisteredANM();

                if (!StringUtils.isBlank(loggedInUser)) {

                    boolean accountExists = false;

                    for (Account account : accounts) {
                        if (account.type.equals(getAccountAuthenticatorXml().getAccountType()) && account.name.equals(context.allSharedPreferences().fetchRegisteredANM())) {
                            accountExists = true;
                            break;
                        }
                    }

                    if (!accountExists) {

                        Utils.logoutUser(context, context.applicationContext().getString(R.string.account_removed));

                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public boolean addModuleConfiguration(@NonNull String moduleName, @NonNull ModuleConfiguration moduleConfiguration) {
        return addModuleConfiguration(false, moduleName, moduleConfiguration);
    }

    public boolean addModuleConfiguration(boolean isDefaultModule, @NonNull String moduleName, @NonNull ModuleConfiguration moduleConfiguration) {
        this.moduleConfigurations.put(moduleName, moduleConfiguration);
        if (isDefaultModule) {
            this.defaultModule = moduleName;
        }

        return true;
    }

    public boolean removeModuleConfiguration(@NonNull String moduleName) {
        return this.moduleConfigurations.remove(moduleName) != null;
    }

    @NonNull
    public ModuleConfiguration getModuleConfiguration(@NonNull String moduleName) {
        ModuleConfiguration moduleConfiguration = this.moduleConfigurations.get(moduleName);

        if (moduleConfiguration == null) {
            throw new IllegalStateException("The module configuration for " + moduleName + " could not be found! Kindly make sure that this is configured correctly through CoreLibrary.getInstance().addModuleConfiguration()");
        }
        return moduleConfiguration;

    }

    public void setCurrentModule(@NonNull String moduleName) {
        this.currentModule = moduleName;
    }

    @NonNull
    public String getCurrentModuleName() {
        return currentModule != null ? currentModule : defaultModule;
    }

    @NonNull
    public ModuleConfiguration getCurrentModuleConfiguration() {
        return getModuleConfiguration(getCurrentModuleName());
    }

    @NonNull
    public void startRegisterActivity(@NonNull android.content.Context context) {
        Intent intent = new Intent(context, BaseConfigurableRegisterActivity.class);
        intent.putExtra(AllConstants.IntentExtra.MODULE_NAME, getCurrentModuleName());
        context.startActivity(intent);
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }
}
