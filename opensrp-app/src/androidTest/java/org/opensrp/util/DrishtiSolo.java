package org.opensrp.util;

import android.app.Activity;
import android.app.Instrumentation;
import com.jayway.android.robotium.solo.Solo;
import org.opensrp.view.activity.HomeActivity;
import org.opensrp.view.activity.LoginActivity;

import static org.opensrp.util.Wait.*;

public class DrishtiSolo extends Solo {
    public DrishtiSolo(Instrumentation instrumentation, Activity activity) {
        super(instrumentation, activity);
        waitForProgressBarToGoAway(activity);
    }

    public DrishtiSolo assertCanLogin(String userName, String password) {
        enterText(0, userName);
        enterText(1, password);
        clickOnButton(0);
        waitForActivity(HomeActivity.class.getSimpleName());
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
