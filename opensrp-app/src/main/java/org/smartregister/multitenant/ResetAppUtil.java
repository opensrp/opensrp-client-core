package org.smartregister.multitenant;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.smartregister.exception.AppResetException;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class ResetAppUtil {

    private DrishtiApplication application;

    public ResetAppUtil(@NonNull DrishtiApplication drishtiApplication) {
        this.application = drishtiApplication;
    }

    protected boolean resetSharedPreferences() {
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

    public boolean isEventsClientSynced() {
        EventClientRepository eventClientRepository = application.getContext().getEventClientRepository();
        if (eventClientRepository != null) {
            return eventClientRepository.getUnSyncedEventsCount() == 0;
        }

        return false;
    }
}
