package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.TestSyncConfiguration;
import org.smartregister.repository.AllSharedPreferences;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUtilitiesRobolectricTest extends BaseRobolectricUnitTest {

    @Test
    public void assertGetImageUrlDoesNotHaveDoubleSlash(){
        CoreLibrary.init(mock(Context.class), new TestSyncConfiguration(), 1588062490000l);
        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().allSharedPreferences());
        when(allSharedPreferences.fetchBaseURL(anyString()))
                .thenReturn("opensrp");
        final String entityId = "4d9c2150-b6bc-4df8-b68d-b90864792fd2";
        final String imageUrl = FileUtilities.getImageUrl(entityId);
        Assert.assertFalse(imageUrl.contains("//"));
    }
}
