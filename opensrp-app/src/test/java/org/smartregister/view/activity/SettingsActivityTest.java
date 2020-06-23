package org.smartregister.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

@PrepareForTest({Utils.class})
public class SettingsActivityTest extends BaseUnitTest {

    private static final String HOSTURL = "https://www.opensrp.smartregister.com";
    private static final String PORT = "8080";

    private ActivityController<SettingsActivity> controller;

    @Mock
    private AlertDialog alertDialog;

    @InjectMocks
    private SettingsActivity activity;

    @Mock
    private AllSharedPreferences sharedPreferences;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private View view;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        Intent intent = new Intent(RuntimeEnvironment.application, SettingsActivity.class);
        controller = Robolectric.buildActivity(SettingsActivity.class, intent);
        activity = controller.get();
        controller.setup();

    }

    @Test
    public void assertActivityCreatedSuccessfully() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void testOnPreferenceChangeInvokesPreferenceSettingsWithCorrectValues() {


        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(sharedPreferences);

        activity.onPreferenceChange(null, HOSTURL + ":" + PORT);

        Mockito.verify(sharedPreferences).savePort(8080);

        Mockito.verify(sharedPreferences).saveHost(HOSTURL);
    }


    @Test
    public void testOnClickInvokesBaseUrlEditTextPreferenceOnClickMethod() {

        ReflectionHelpers.setField(activity, "dialog", alertDialog);

        EditTextPreference baseUrlEditTextPreference = ReflectionHelpers.getField(activity, "baseUrlEditTextPreference");
        Assert.assertNotNull(baseUrlEditTextPreference);
        baseUrlEditTextPreference.getEditText().setText(HOSTURL + ":" + PORT);

        activity.onClick(view);

        Mockito.verify(alertDialog).dismiss();

    }

    @Test
    public void testOnClickInvokesShowErrorToastIfInvalidUrlEntered() {

        ReflectionHelpers.setField(activity, "dialog", alertDialog);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(sharedPreferences);

        EditTextPreference baseUrlEditTextPreference = ReflectionHelpers.getField(activity, "baseUrlEditTextPreference");
        Assert.assertNotNull(baseUrlEditTextPreference);
        baseUrlEditTextPreference.getEditText().setText("baddly-formed-url:" + PORT);

        activity.onClick(view);

        PowerMockito.verifyStatic(Utils.class);
        Utils.showShortToast(activity, RuntimeEnvironment.application.getString(R.string.invalid_url_massage));

    }
}
