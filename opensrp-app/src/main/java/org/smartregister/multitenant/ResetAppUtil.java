package org.smartregister.multitenant;

import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.exception.AppResetException;
import org.smartregister.multitenant.check.EventClientSyncedCheck;
import org.smartregister.multitenant.exception.PreResetAppOperationException;
import org.smartregister.multitenant.executor.CoreLibraryExecutors;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.model.AppDatabase;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class ResetAppUtil {

    private DrishtiApplication application;
    private CoreLibraryExecutors coreLibraryExecutors;
    private ArrayList<PreResetAppCheck> preResetAppChecks = new ArrayList<>();

    public ResetAppUtil(@NonNull DrishtiApplication drishtiApplication) {
        this.application = drishtiApplication;
        coreLibraryExecutors = new CoreLibraryExecutors();
        preResetAppChecks.add(new EventClientSyncedCheck());
    }

    public void startResetProcess() {
        // Show some UI here to display the reset progress
        performPreResetChecks();

    }

    public void performPreResetChecks() {
        // Should be handled in the background
        coreLibraryExecutors.diskIO()
                .execute(() -> {
                    try {
                        for (PreResetAppCheck preResetAppCheck : preResetAppChecks) {
                            if (!preResetAppCheck.isCheckOk(application)) {
                                performPreResetOperations(preResetAppCheck);

                            }
                        }

                        performResetOperations();

                        coreLibraryExecutors.mainThread()
                                .execute(() -> {
                                    // Done here, what should we do
                                });

                    } catch (PreResetAppOperationException | AppResetException e) {
                        Timber.e(e);
                    }
                });
    }

    public void performResetOperations() throws AppResetException {
        clearSqCipherDb();
        clearP2PDb();
        clearAllPrivateKeyEntries();
        clearSharedPreferences();
    }

    protected void clearP2PDb() {
        P2POptions p2POptions = CoreLibrary.getInstance().getP2POptions();
        if (p2POptions != null && p2POptions.isEnableP2PLibrary()) {
            AppDatabase roomP2PDb = P2PLibrary.getInstance().getDb();
            roomP2PDb.clearAllTables();
            Utils.deleteRoomDb(application.getApplicationContext(), roomP2PDb.getOpenHelper().getDatabaseName());
        }
    }

    public void performPreResetOperations(@NonNull PreResetAppCheck preResetAppCheck) throws PreResetAppOperationException {
        preResetAppCheck.performPreResetAppOperations(application);
    }

    protected boolean clearSharedPreferences() {
        SharedPreferences sharedPreferences = application.getContext().allSharedPreferences().getPreferences();
        if (sharedPreferences != null) {
            return sharedPreferences.edit().clear().commit();
        }

        return false;
    }

    protected boolean clearSqCipherDb() {
        return application.getRepository().deleteRepository();
    }

    protected void clearAllPrivateKeyEntries() throws AppResetException {
        KeyStore keyStore = application.getContext().userService().getKeyStore();
        if (keyStore != null) {
            try {
                Enumeration<String> keystoreEnumeration = keyStore.aliases();
                while (keystoreEnumeration.hasMoreElements()) {
                    String keyStoreAlias = keystoreEnumeration.nextElement();
                    keyStore.deleteEntry(keyStoreAlias);
                }
            } catch (KeyStoreException ex) {
                Timber.e(ex);
                throw new AppResetException(ex.getMessage(), ex);
            }
        }
    }

    public boolean addPreResetAppCheck(@NonNull PreResetAppCheck preResetAppCheck) {
        if (!preResetAppChecks.contains(preResetAppCheck)) {
            return preResetAppChecks.add(preResetAppCheck);
        }

        return false;
    }

    public boolean removePreResetAppCheck(@NonNull PreResetAppCheck preResetAppCheck) {
        return preResetAppChecks.remove(preResetAppCheck);
    }

    @Nullable
    public PreResetAppCheck removePreResetAppCheck(@NonNull String checkName) {
        for (PreResetAppCheck preResetAppCheck: preResetAppChecks) {
            if (checkName.equals(preResetAppCheck.getUniqueName())) {
                if(removePreResetAppCheck(preResetAppCheck)) {
                    return preResetAppCheck;
                }

                break;
            }
        }

        return null;
    }


    @MainThread
    public void showProgressText(@NonNull String progressText) {

    }
}
