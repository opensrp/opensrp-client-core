package org.smartregister.account;

import android.content.Context;

/**
 * Created by ndegwamartin on 11/05/2020.
 */
public abstract class AbstractAccountAuthenticator extends android.accounts.AbstractAccountAuthenticator {
    public AbstractAccountAuthenticator(Context context) {
        super(context);
    }

    public abstract String getRefreshToken();
}
