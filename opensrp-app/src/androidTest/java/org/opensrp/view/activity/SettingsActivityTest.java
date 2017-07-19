package org.opensrp.view.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import org.opensrp.Context;
import org.opensrp.repository.AllSettings;
import org.opensrp.repository.AllSharedPreferences;
import org.opensrp.util.DrishtiSolo;
import org.opensrp.util.FakeDrishtiService;
import org.opensrp.util.FakeUserService;

import static org.opensrp.util.FakeContext.setupService;
import static org.opensrp.util.Wait.waitForFilteringToFinish;
import static org.opensrp.util.Wait.waitForProgressBarToGoAway;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    private DrishtiSolo solo;
    private FakeDrishtiService drishtiService;
    private FakeUserService userService;

    public SettingsActivityTest() {
        super("org.ei.drishti.test", HomeActivity.class);
        drishtiService = new FakeDrishtiService("Default");
        userService = new FakeUserService();
    }

    @Override
    public void setUp() throws Exception {
        setupService(drishtiService, userService, 1000000
        ).updateApplicationContext(getActivity().getApplicationContext());

        solo = new DrishtiSolo(getInstrumentation(), getActivity());
    }

    @Suppress
    public void testShouldRegisterANM() throws Exception {
        AllSharedPreferences preferences = Context.getInstance().allSharedPreferences();
        AllSettings settings = Context.getInstance().allSettings();

        settings.registerANM("XYZ", "password Y");

        assertEquals("XYZ", preferences.fetchRegisteredANM());
    }

    @Override
    public void tearDown() throws Exception {
        waitForFilteringToFinish();
        waitForProgressBarToGoAway(getActivity());
        solo.finishOpenedActivities();
    }
}
