package org.smartregister.anc.model;

import android.text.TextUtils;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.LoginContract;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginModel implements LoginContract.Model {

    @Override
    public org.smartregister.Context getOpenSRPContext() {
        return AncApplication.getInstance().getContext();
    }

    @Override
    public boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 1;
    }

    @Override
    public boolean isEmptyUsername(String username) {
        return username == null || TextUtils.isEmpty(username);
    }

    @Override
    public boolean isUserLoggedOut() {
        return getOpenSRPContext().IsUserLoggedOut();
    }

}
