package org.smartregister.login.model;

import android.text.TextUtils;

import org.smartregister.CoreLibrary;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 27/06/2018. Model class to business logic handle
 */
public class BaseLoginModel implements BaseLoginContract.Model {

    @Override
    public org.smartregister.Context getOpenSRPContext() {
        return  CoreLibrary.getInstance().context();

    }

    /**
     * If password is not null and length is greater than 1 it'll return true otherwise false
     * @param password
     * @return
     */

    @Override
    public boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 1;
    }

    /**
     * Username empty check if null or empty
     * @param username
     * @return
     */

    @Override
    public boolean isEmptyUsername(String username) {
        return username == null || TextUtils.isEmpty(username);
    }

    /**
     * If user logout it'll remove the session from app
     * @return
     */

    @Override
    public boolean isUserLoggedOut() {
        return getOpenSRPContext().IsUserLoggedOut();
    }

}
