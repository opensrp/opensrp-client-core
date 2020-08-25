package org.smartregister.sync.intent;

import android.content.Intent;

import org.json.JSONException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-08-2020.
 */
public class SettingsSyncIntentServiceTest extends BaseRobolectricUnitTest {

    private SettingsSyncIntentService settingsSyncIntentService;

    @Before
    public void setUp() throws Exception {
        settingsSyncIntentService = Robolectric.buildIntentService(SettingsSyncIntentService.class)
                .create()
                .get();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    @Test
    public void onHandleIntent() {
    }

    @Test
    public void processSettingsShouldReturnTrueWhenIntentIsNull() throws JSONException {
        settingsSyncIntentService.syncSettingsServiceHelper = Mockito.spy(settingsSyncIntentService.syncSettingsServiceHelper);
        Assert.assertTrue(settingsSyncIntentService.processSettings(null));

        Mockito.verify(settingsSyncIntentService.syncSettingsServiceHelper, Mockito.never()).processIntent();
    }

    @Test
    public void processSettingsShouldReturnTrueAndCallProcessIntentWhenIntentIsNotNull() throws JSONException {
        settingsSyncIntentService.syncSettingsServiceHelper = Mockito.spy(settingsSyncIntentService.syncSettingsServiceHelper);
        Mockito.doReturn(30).when(settingsSyncIntentService.syncSettingsServiceHelper).processIntent();

        Intent intent = new Intent();

        Assert.assertTrue(settingsSyncIntentService.processSettings(intent));
        Mockito.verify(settingsSyncIntentService.syncSettingsServiceHelper, Mockito.times(1)).processIntent();

        Assert.assertEquals(30, intent.getIntExtra(AllConstants.INTENT_KEY.SYNC_TOTAL_RECORDS, 0));
    }

    @Ignore
    @Test
    public void onCreate() {
    }

    @Ignore
    @Test
    public void onDestroy() {
    }
}