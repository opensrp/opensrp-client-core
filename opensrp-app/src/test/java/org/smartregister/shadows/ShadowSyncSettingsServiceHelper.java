package org.smartregister.shadows;

import org.json.JSONException;
import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.helper.SyncSettingsServiceHelper;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 11-08-2020.
 */
@Implements(SyncSettingsServiceHelper.class)
public class ShadowSyncSettingsServiceHelper {

    private static SyncSettingsServiceHelper instance;

    @RealObject
    private SyncSettingsServiceHelper realObject;

    @Implementation
    public SyncSettingsServiceHelper __constructor__(String baseUrl, HTTPAgent httpAgent) {

        ReflectionHelpers.setField(realObject, "httpAgent", httpAgent);
        ReflectionHelpers.setField(realObject, "baseUrl", baseUrl);
        AllSharedPreferences sharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        ReflectionHelpers.setField(realObject, "sharedPreferences", sharedPreferences);

        /*realObject.setBaseUrl(baseUrl);
        realObject.setHttpAgent(httpAgent);
        realObject.setSharedPreferences(sharedPreferences);*/

        realObject = Mockito.spy(realObject);
        instance = realObject;

        return realObject;
    }

    public static SyncSettingsServiceHelper getLastInstance() {
        return instance;
    }

}
