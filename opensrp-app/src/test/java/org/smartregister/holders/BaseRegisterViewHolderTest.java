package org.smartregister.holders;

import android.view.LayoutInflater;
import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-03-2021.
 */
public class BaseRegisterViewHolderTest extends BaseRobolectricUnitTest {

    private BaseRegisterViewHolder baseRegisterViewHolder;
    private View view;

    @Before
    public void setUp() throws Exception {
        view = LayoutInflater.from(RuntimeEnvironment.application)
                .inflate(R.layout.base_configurable_register_list_row, null);
    }

    @Test
    public void constructor() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        Assert.assertNotNull(baseRegisterViewHolder.textViewParentName);
        Assert.assertNotNull(baseRegisterViewHolder.textViewChildName);
        Assert.assertNotNull(baseRegisterViewHolder.textViewGender);
        Assert.assertNotNull(baseRegisterViewHolder.dueButton);
        Assert.assertNotNull(baseRegisterViewHolder.dueButtonLayout);
        Assert.assertNotNull(baseRegisterViewHolder.tvRegisterType);
        Assert.assertNotNull(baseRegisterViewHolder.tvLocation);
        Assert.assertNotNull(baseRegisterViewHolder.childColumn);
        Assert.assertNotNull(baseRegisterViewHolder.secondDotDivider);
        Assert.assertNotNull(baseRegisterViewHolder.firstDotDivider);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void showGuardianName() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        Assert.assertEquals(View.GONE, baseRegisterViewHolder.textViewParentName.getVisibility());

        baseRegisterViewHolder.showGuardianName();
        Assert.assertEquals(View.VISIBLE, baseRegisterViewHolder.textViewParentName.getVisibility());
    }

    @Test
    public void removeGuardianName() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        baseRegisterViewHolder.textViewParentName.setVisibility(View.VISIBLE);

        baseRegisterViewHolder.removeGuardianName();
        Assert.assertEquals(View.GONE, baseRegisterViewHolder.textViewParentName.getVisibility());
    }

    @Test
    public void showPersonLocation() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        baseRegisterViewHolder.tvLocation.setVisibility(View.GONE);
        baseRegisterViewHolder.secondDotDivider.setVisibility(View.GONE);

        baseRegisterViewHolder.showPersonLocation();
        Assert.assertEquals(View.VISIBLE, baseRegisterViewHolder.tvLocation.getVisibility());
        Assert.assertEquals(View.VISIBLE, baseRegisterViewHolder.secondDotDivider.getVisibility());
    }

    @Test
    public void removePersonLocation() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        baseRegisterViewHolder.tvLocation.setVisibility(View.VISIBLE);
        baseRegisterViewHolder.secondDotDivider.setVisibility(View.VISIBLE);

        baseRegisterViewHolder.removePersonLocation();
        Assert.assertEquals(View.GONE, baseRegisterViewHolder.tvLocation.getVisibility());
        Assert.assertEquals(View.GONE, baseRegisterViewHolder.secondDotDivider.getVisibility());
    }

    @Test
    public void showRegisterType() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        baseRegisterViewHolder.tvRegisterType.setVisibility(View.GONE);
        baseRegisterViewHolder.firstDotDivider.setVisibility(View.GONE);

        baseRegisterViewHolder.showRegisterType();
        Assert.assertEquals(View.VISIBLE, baseRegisterViewHolder.tvRegisterType.getVisibility());
        Assert.assertEquals(View.VISIBLE, baseRegisterViewHolder.firstDotDivider.getVisibility());
    }

    @Test
    public void hideRegisterType() {
        baseRegisterViewHolder = new BaseRegisterViewHolder(view);

        baseRegisterViewHolder.tvRegisterType.setVisibility(View.VISIBLE);
        baseRegisterViewHolder.firstDotDivider.setVisibility(View.VISIBLE);

        baseRegisterViewHolder.hideRegisterType();
        Assert.assertEquals(View.GONE, baseRegisterViewHolder.tvRegisterType.getVisibility());
        Assert.assertEquals(View.GONE, baseRegisterViewHolder.firstDotDivider.getVisibility());
    }
}