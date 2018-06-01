package org.smartregister.view.activity;

import static org.smartregister.util.Wait.waitForFilteringToFinish;

public class LoginActivityTest {
//    private DrishtiSolo solo;
//    private FakeUserService userService;

    public LoginActivityTest() {
//        super(LoginActivity.class);
    }

    //    @Override
    protected void setUp() throws Exception {
//        FakeDrishtiService drishtiService = new FakeDrishtiService(String.valueOf(new Date().getTime() - 1));
//        userService = new FakeUserService();
//
//        setupService(drishtiService, userService, -1000).updateApplicationContext(getActivity());
//        CoreLibrary.getInstance().context().session().setPassword(null);
//
//        solo = new DrishtiSolo(getInstrumentation(), getActivity());
    }

    public void ignoreTestShouldAllowLoginWithoutCheckingRemoteLoginWhenLocalLoginSucceeds() throws Exception {
//        userService.setupFor("user", "password", true, true, UNKNOWN_RESPONSE);
//
//        solo.assertCanLogin("user", "password");
//
//        userService.assertOrderOfCalls("local", "login");
    }

    public void ignoreTestShouldTryRemoteLoginWhenThereIsNoRegisteredUser() throws Exception {
//        userService.setupFor("user", "password", false, false, SUCCESS);
//
//        solo.assertCanLogin("user", "password");
//
//        userService.assertOrderOfCalls("remote", "login");
    }

    public void ignoreTestShouldFailToLoginWhenBothLoginMethodsFail() throws Exception {
//        userService.setupFor("user", "password", false, false, UNKNOWN_RESPONSE);
//
//        solo.assertCannotLogin("user", "password");
//
//        userService.assertOrderOfCalls("remote");
    }

    public void ignoreTestShouldNotTryRemoteLoginWhenRegisteredUserExistsEvenIfLocalLoginFails() throws Exception {
//        userService.setupFor("user", "password", true, false, SUCCESS);
//
//        solo.assertCannotLogin("user", "password");
//        userService.assertOrderOfCalls("local");
    }

    public void ignoreTestShouldNotTryLocalLoginWhenRegisteredUserDoesNotExist() throws Exception {
//        userService.setupFor("user", "password", false, true, SUCCESS);
//
//        solo.assertCanLogin("user", "password");
//        userService.assertOrderOfCalls("remote", "login");
    }

    //    @Override
    public void tearDown() throws Exception {
        waitForFilteringToFinish();
//        waitForProgressBarToGoAway(getActivity());
//        solo.finishOpenedActivities();
    }
}
