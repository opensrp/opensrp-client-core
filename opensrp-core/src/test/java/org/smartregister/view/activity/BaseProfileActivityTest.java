package org.smartregister.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowToast;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;

public class BaseProfileActivityTest extends BaseRobolectricUnitTest {

    private final String INTENT_KEY = "intent_key";
    @Mock
    protected TabLayout tabLayout;
    @Mock
    protected ViewPager viewPager;
    @Mock
    private View view;
    @Mock
    private Button button;
    @Mock
    private AppBarLayout appBarLayout;
    @Mock
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @Mock
    private Toolbar toolbar;
    @Mock
    private ActionBar actionBar;
    private TestProfileActivity profileActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra(INTENT_KEY, INTENT_KEY);
        profileActivity = Mockito.spy(Robolectric.buildActivity(TestProfileActivity.class, intent).get());
    }

    @Test
    public void testOnCreateShouldBootstrapActivity() {

        Mockito.doNothing().when(profileActivity).setContentView(R.layout.activity_base_profile);
        Mockito.doReturn(view).when(profileActivity).findViewById(R.id.btn_profile_registration_info);
        Mockito.doReturn(button).when(profileActivity).findViewById(R.id.btn_profile_registration_info);
        Mockito.doReturn(appBarLayout).when(profileActivity).findViewById(R.id.collapsing_toolbar_appbarlayout);
        Mockito.doReturn(toolbar).when(profileActivity).findViewById(R.id.collapsing_toolbar);
        Mockito.doReturn(collapsingToolbarLayout).when(profileActivity).findViewById(R.id.collapsing_toolbar_layout);
        Mockito.doReturn(tabLayout).when(profileActivity).findViewById(R.id.tabs);
        Mockito.doReturn(viewPager).when(profileActivity).findViewById(R.id.viewpager);
        Mockito.doReturn(actionBar).when(profileActivity).getSupportActionBar();
        Mockito.doNothing().when(profileActivity).setSupportActionBar(toolbar);

        profileActivity.onCreation();

        Mockito.verify(actionBar).setDisplayHomeAsUpEnabled(true);
        Mockito.verify(profileActivity).setContentView(ArgumentMatchers.anyInt());
        Mockito.verify(profileActivity).findViewById(R.id.btn_profile_registration_info);
        Mockito.verify(profileActivity).findViewById(R.id.collapsing_toolbar_appbarlayout);
        Mockito.verify(profileActivity).findViewById(R.id.collapsing_toolbar);
        Mockito.verify(profileActivity).findViewById(R.id.tabs);
        Mockito.verify(profileActivity).findViewById(R.id.viewpager);
        Mockito.verify(profileActivity).getSupportActionBar();
        Mockito.verify(profileActivity).setSupportActionBar(toolbar);
        Mockito.verify(profileActivity).initializePresenter();
        Mockito.verify(profileActivity).setupViewPager(Mockito.any(ViewPager.class));

        profileActivity.onClick(Mockito.mock(View.class));
        Mockito.verify(profileActivity).fetchProfileData();
    }

    @Test
    public void testProgressDialogControlsShouldPerformCorrectAction() {
        Assert.assertNull(ShadowDialog.getLatestDialog());
        profileActivity.showProgressDialog(R.string.empty_string);
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(dialog);
        Assert.assertTrue(dialog.isShowing());
        profileActivity.hideProgressDialog();
        Assert.assertFalse(dialog.isShowing());
    }

    @Test
    public void testDisplayToastShouldDisplayToast() {
        Assert.assertNull(ShadowToast.getLatestToast());
        profileActivity.displayToast(R.string.empty_string);
        Assert.assertNotNull(ShadowToast.getLatestToast());
    }

    @Test
    public void testGetIntentStringShouldGetCorrectIntentString() {
        Assert.assertEquals(INTENT_KEY, profileActivity.getIntentString(INTENT_KEY));
    }
}
