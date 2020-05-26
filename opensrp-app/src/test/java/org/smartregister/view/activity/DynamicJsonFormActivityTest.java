package org.smartregister.view.activity;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.util.FormUtils;

import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 19-05-2020.
 */
public class DynamicJsonFormActivityTest extends BaseRobolectricUnitTest {

    private DynamicJsonFormActivity dynamicJsonFormActivity;

    @Before
    public void setUp() throws Exception {
        dynamicJsonFormActivity = new DynamicJsonFormActivity();
    }

    @Test
    public void getRulesShouldReturnCallFormUtils() throws Exception {
        FormUtils formUtils = Mockito.mock(FormUtils.class);

        ReflectionHelpers.setStaticField(FormUtils.class, "instance", formUtils);
        ReflectionHelpers.setField(formUtils, "mContext", RuntimeEnvironment.application);
        String rulesFileIdentifier = "registration_calculation.yml";

        Mockito.doReturn(new BufferedReader(new StringReader(""))).when(formUtils).getRulesFromRepository(Mockito.eq(rulesFileIdentifier));


        dynamicJsonFormActivity.getRules(RuntimeEnvironment.application, rulesFileIdentifier);
        Mockito.verify(formUtils).getRulesFromRepository(Mockito.eq(rulesFileIdentifier));
    }

    @Test
    public void getSubFormShouldCallFormUtils() throws Exception {
        FormUtils formUtils = Mockito.mock(FormUtils.class);

        ReflectionHelpers.setStaticField(FormUtils.class, "instance", formUtils);
        ReflectionHelpers.setField(formUtils, "mContext", RuntimeEnvironment.application);
        String subFormIdentifier = "tuberculosis_test";

        JSONObject jsonObject = new JSONObject();

        Mockito.doReturn(jsonObject).when(formUtils).getSubFormJsonFromRepository(subFormIdentifier, null, RuntimeEnvironment.application, false);


        dynamicJsonFormActivity.getSubForm(subFormIdentifier, null, RuntimeEnvironment.application, false);
        Mockito.verify(formUtils).getSubFormJsonFromRepository(subFormIdentifier, null, RuntimeEnvironment.application, false);
    }
}