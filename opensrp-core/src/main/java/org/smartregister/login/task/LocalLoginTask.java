package org.smartregister.login.task;

import org.smartregister.CoreLibrary;
import org.smartregister.event.Listener;
import org.smartregister.service.UserService;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ndegwamartin on 13/06/2020.
 */
public class LocalLoginTask {

    private final String mUsername;
    private final char[] mPassword;
    private final Listener<Boolean> mAfterLoginCheck;
    private BaseLoginContract.View mLoginView;

    public LocalLoginTask(BaseLoginContract.View loginView, String username, char[] password, Listener<Boolean> afterLoginCheck) {
        mLoginView = loginView;
        mUsername = username;
        mPassword = password;
        mAfterLoginCheck = afterLoginCheck;
    }

    public void execute() {

        mLoginView.showProgress(true);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {

            boolean loginResponse = getUserService().isUserInValidGroup(mUsername, mPassword);

            mLoginView.getAppCompatActivity().runOnUiThread(() -> {

                mLoginView.showProgress(false);
                mAfterLoginCheck.onEvent(loginResponse);

            });

        });

    }

    private UserService getUserService() {
        return CoreLibrary.getInstance().context().userService();
    }
}
