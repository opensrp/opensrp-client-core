package org.smartregister.sync.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 10-11-2020.
 */
public class BaseHelperTest extends BaseRobolectricUnitTest {

    @Test
    public void getFormattedBaseUrl() {

        CoreLibrary.getInstance().context().allSharedPreferences()
                .getPreferences().edit()
                .putString(AllConstants.DRISHTI_BASE_URL, "http://site.com/").commit();

        Assert.assertNotNull(CoreLibrary.getInstance().context().configuration().dristhiBaseURL());

        Assert.assertEquals("http://site.com", new BaseHelper().getFormattedBaseUrl());
    }
}