package org.smartregister.view.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;

import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.DrishtiSolo;
import org.smartregister.util.FakeDrishtiService;
import org.smartregister.util.FakeUserService;

import static org.smartregister.util.FakeContext.setupService;
import static org.smartregister.util.Wait.waitForFilteringToFinish;
import static org.smartregister.util.Wait.waitForProgressBarToGoAway;

public class SettingsActivityTest extends ActivityInstrumentationTestCase2<NativeHomeActivity> {
    private DrishtiSolo solo;
    private FakeDrishtiService drishtiService;
    private FakeUserService userService;

    public SettingsActivityTest() {
        super("org.ei.drishti.test", NativeHomeActivity.class);
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
        AllSharedPreferences preferences = CoreLibrary.getInstance().context().allSharedPreferences();
        AllSettings settings = CoreLibrary.getInstance().context().allSettings();

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
