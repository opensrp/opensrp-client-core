package org.smartregister.multitenant;

import android.content.SharedPreferences;

import org.smartregister.CoreLibrary;
import org.smartregister.exception.AppResetException;
import org.smartregister.view.activity.DrishtiApplication;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class ResetAppUtil {

    public void resetSharedPreferences() {
        SharedPreferences sharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences().getPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().commit();
        }
    }

    public void clearSqCipherDb() {
        DrishtiApplication.getInstance().getRepository().deleteRepository();
    }

    public void clearAllPrivateKeyEntries() throws AppResetException {
        KeyStore keyStore = CoreLibrary.getInstance().context().userService().getKeyStore();
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
}
