package org.smartregister.view.activity.mock;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.smartregister.R;
import org.smartregister.view.activity.BaseLoginActivity;

/**
 * Created by Raihan Ahmed on 11/11/17.
 */

public class LoginActivityMock extends BaseLoginActivity {

    public static InputMethodManager inputManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return 0;
    }

    @Override
    protected void initializePresenter() {
        //Override
    }

    @Override
    public Object getSystemService(String name) {
        if (name.equalsIgnoreCase(INPUT_METHOD_SERVICE)) {
            return inputManager;
        } else {
            return super.getSystemService(name);
        }
    }

    @Nullable
    @Override
    public View getCurrentFocus() {
        return findViewById(R.id.login_userNameText);
    }

    @Override
    public void goToHome(boolean isRemote) {
        //Override
    }
}