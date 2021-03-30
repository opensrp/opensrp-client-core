package org.smartregister.holders;

import android.view.LayoutInflater;
import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 16-03-2021.
 */
public class FooterViewHolderTest extends BaseRobolectricUnitTest {

    private FooterViewHolder footerViewHolder;

    @Test
    public void constructor() {
        View view = LayoutInflater.from(RuntimeEnvironment.application)
                        .inflate(R.layout.smart_register_pagination, null);
        footerViewHolder = new FooterViewHolder(view);

        Assert.assertNotNull(footerViewHolder.nextPageView);
        Assert.assertNotNull(footerViewHolder.pageInfoView);
        Assert.assertNotNull(footerViewHolder.previousPageView);
    }
}