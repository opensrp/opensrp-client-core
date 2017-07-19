package org.smartregister.view.activity;

import android.test.ActivityInstrumentationTestCase2;
import org.smartregister.Context;
import org.smartregister.util.*;

import java.util.Date;

import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.util.FakeContext.setupService;
import static org.smartregister.util.Wait.waitForFilteringToFinish;
import static org.smartregister.util.Wait.waitForProgressBarToGoAway;

public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    private DrishtiSolo solo;
    private FakeUserService userService;

    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        FakeDrishtiService drishtiService = new FakeDrishtiService(String.valueOf(new Date().getTime() - 1));
        userService = new FakeUserService();

        setupService(drishtiService, userService, 100000).updateApplicationContext(getActivity());
        Context.getInstance().session().setPassword("password");

        solo = new DrishtiSolo(getInstrumentation(), getActivity());
    }

    /* Bug in Android. Cannot run webview with emulator: http://code.google.com/p/android/issues/detail?id=12987 */
    public void ignoringTestShouldGoBackToLoginScreenWhenLoggedOutWithAbilityToLogBackIn() throws Exception {
        userService.setupFor("user", "password", false, false, SUCCESS);

        solo.logout();
        solo.assertCurrentActivity("Should be in Login screen.", LoginActivity.class);

        userService.setupFor("user", "password", false, false, SUCCESS);
        solo.assertCanLogin("user", "password");
    }

    @Override
    public void tearDown() throws Exception {
        waitForFilteringToFinish();
        waitForProgressBarToGoAway(getActivity());
        solo.finishOpenedActivities();
    }
}
