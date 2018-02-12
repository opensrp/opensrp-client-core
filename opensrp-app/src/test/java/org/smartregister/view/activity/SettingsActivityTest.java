package org.smartregister.view.activity;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.Preference;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
public class SettingsActivityTest extends BaseUnitTest {

    private ActivityController<SettingsActivity> controller;

    @InjectMocks
    private SettingsActivity activity;

    @Mock
    private org.smartregister.Context context_;
    @Mock
    CoreLibrary coreLibrary;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        Intent intent = new Intent(RuntimeEnvironment.application, SettingsActivity.class);
        controller = Robolectric.buildActivity(SettingsActivity.class, intent);
        activity = controller.get();
        controller.setup();
        Preference baseUrlPreference = activity.findPreference("DRISHTI_BASE_URL");
        if (baseUrlPreference != null) {
            EditTextPreference baseUrlEditTextPreference = (EditTextPreference)
                    baseUrlPreference;
            Preference.OnPreferenceChangeListener preferenceChangeListener = baseUrlEditTextPreference.getOnPreferenceChangeListener();
            Assert.assertEquals(preferenceChangeListener.onPreferenceChange(baseUrlEditTextPreference, "http://127.0.0.1"), true);
        }

    }

    @Test
    public void assertTestingTestToSeeTestWorks() {
        Assert.assertNotNull(activity);
    }
}
