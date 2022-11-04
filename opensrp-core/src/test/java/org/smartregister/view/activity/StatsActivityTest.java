package org.smartregister.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import androidx.test.core.app.ApplicationProvider;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.view.fragment.StatsFragment;

public class StatsActivityTest extends BaseUnitTest {

    private StatsActivity statsActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SettingsActivity.class);
        ActivityController<StatsActivity> controller = Robolectric.buildActivity(StatsActivity.class, intent);
        statsActivity = Mockito.spy(controller.get());
    }

    @Test
    public void onCreateInitializesActivity() {
        Bundle savedInstanceState = new Bundle();
        statsActivity.onCreate(savedInstanceState);
        Mockito.verify(statsActivity).setupViews();
        Mockito.verify(statsActivity).setupViewPager(ArgumentMatchers.any(ViewPager.class));
        Assert.assertNotNull(statsActivity.getSupportActionBar());
    }

    @Test
    public void canShowProgressDialog() {
        Assert.assertNull(ShadowDialog.getLatestDialog());
        statsActivity.showProgressDialog();
        Dialog progressDialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(progressDialog);
        Assert.assertTrue(progressDialog.isShowing());
    }

    @Test
    public void canHideProgressDialog() {
        statsActivity.showProgressDialog();
        Dialog progressDialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(progressDialog);
        statsActivity.hideProgressDialog();
        Assert.assertFalse(progressDialog.isShowing());
    }

    @Test
    public void getOtherFragmentsReturnsCorrectFragmentArray() {
        Fragment[] fragments = statsActivity.getOtherFragments();
        Assert.assertEquals(1, fragments.length);
        Assert.assertTrue(fragments[0] instanceof StatsFragment);
    }

}
