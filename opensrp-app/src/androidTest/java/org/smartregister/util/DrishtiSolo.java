package org.smartregister.util;

import android.app.Activity;
import android.app.Instrumentation;

import com.jayway.android.robotium.solo.Solo;

import org.smartregister.view.activity.LoginActivity;
import org.smartregister.view.activity.NativeHomeActivity;

import static org.smartregister.util.Wait.waitForFilteringToFinish;
import static org.smartregister.util.Wait.waitForProgressBarToGoAway;

public class DrishtiSolo extends Solo {
    public DrishtiSolo(Instrumentation instrumentation, Activity activity) {
        super(instrumentation, activity);
        waitForProgressBarToGoAway(activity);
    }

    public DrishtiSolo assertCanLogin(String userName, String password) {
        enterText(0, userName);
        enterText(1, password);
        clickOnButton(0);
        waitForActivity(NativeHomeActivity.class.getSimpleName());
        waitForFilteringToFinish();
        return this;
    }

    public DrishtiSolo assertCannotLogin(String userName, String password) {
        enterText(0, userName);
        enterText(1, password);
        clickOnButton(0);
        waitForActivity(LoginActivity.class.getSimpleName());
        waitForFilteringToFinish();
        return this;
    }

    public void logout() {
        sendKey(MENU);
        clickOnText("Logout");
        waitForActivity(LoginActivity.class.getSimpleName());
    }
}
