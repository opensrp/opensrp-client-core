package org.smartregister.util;


import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.jsonmapping.LoginResponseData;

import java.util.Arrays;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-04-2021.
 */
public class HttpResponseUtilTest extends BaseRobolectricUnitTest {

    @Test
    public void getResponseBodyWhenGivenResponseString() {
        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.jurisdictions = Arrays.asList(new String[]{"Kenya", "Mozambique", "Tanzania"});
        String responseString = JsonFormUtils.gson.toJson(loginResponseData);

        // Call the method under test
        LoginResponseData actualLoginResponseData = HttpResponseUtil.getResponseBody(responseString);

        // Perform assertions
        Assert.assertEquals(actualLoginResponseData.jurisdictions, loginResponseData.jurisdictions);
    }
}