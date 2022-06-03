package org.smartregister.view.controller;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.activity.EligibleCoupleDetailActivity;
import org.smartregister.view.activity.TestProfileActivity;

/**
 * Created by Vincent Karuri on 02/02/2021
 */
public class ProfileNavigationControllerTest extends BaseUnitTest {

    private ProfileNavigationController profileNavigationController;

    @Before
    public void setUp() throws Exception {
        profileNavigationController = new ProfileNavigationController();
    }

    @Test
    public void testNavigateToECProfileShouldNavigateToProfile() {
        final String CASE_ID = "case_id";

        Activity activity = Robolectric.buildActivity(TestProfileActivity.class).setup().get();

        Intent expectedIntent = new Intent(activity, EligibleCoupleDetailActivity.class);
        expectedIntent.putExtra(CASE_ID, CASE_ID);

        profileNavigationController.navigateToECProfile(activity, CASE_ID);

        Intent actualIntent = Shadows.shadowOf(RuntimeEnvironment.getApplication()).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}