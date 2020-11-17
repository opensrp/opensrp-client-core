package org.smartregister.sync.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.TestSyncConfiguration;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 10-11-2020.
 */
public class BaseHelperTest extends BaseRobolectricUnitTest {
/*
    @Before
    public void setUp() throws Exception {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        CoreLibrary.init(context, new TestSyncConfiguration(), 1588062490000l, new P2POptions(true));
    }*/


    @BeforeClass
    public static void resetCoreLibrary() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void getFormattedBaseUrl() {

        CoreLibrary.getInstance().context().allSharedPreferences()
                .getPreferences().edit()
                .putString(AllConstants.DRISHTI_BASE_URL, "http://site.com/").commit();

        Assert.assertNotNull(CoreLibrary.getInstance().context().configuration().dristhiBaseURL());

        Assert.assertEquals("http://site.com", new BaseHelper().getFormattedBaseUrl());
    }
}