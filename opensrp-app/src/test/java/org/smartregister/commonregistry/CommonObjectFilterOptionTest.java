package org.smartregister.commonregistry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.smartregister.commonregistry.CommonObjectFilterOption.ByColumnAndByDetails.byColumn;

/**
 * Created by Richard Kareko on 5/5/20.
 */

public class CommonObjectFilterOptionTest extends BaseUnitTest {

    @Mock
    SmartRegisterClient client;

    private String fieldname;
    private String criteria;
    private String filterOptionName;
    private Map<String, String> emptyDetails;

    private CommonObjectFilterOption commonObjectFilterOption;

    @Before
    public void setUp() {
        emptyDetails = Collections.emptyMap();
        fieldname = "name";
        criteria = "Woman A";
        filterOptionName = "gender";

        commonObjectFilterOption = new CommonObjectFilterOption(criteria, fieldname, byColumn, filterOptionName);
    }

    @Test
    public void testInitialization() {
        assertNotNull(commonObjectFilterOption);
        assertEquals(fieldname, commonObjectFilterOption.fieldname);
        assertEquals(criteria, Whitebox.getInternalState(commonObjectFilterOption, "criteria"));
        assertEquals(filterOptionName, Whitebox.getInternalState(commonObjectFilterOption, "filterOptionName"));
        assertEquals(byColumn, Whitebox.getInternalState(commonObjectFilterOption, "byColumnAndByDetails"));
    }

    @Test
    public void testFilterByColumn() {
        Map<String, String> column1 = EasyMap.create("name", "Woman A").map();
        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",
                emptyDetails,
                "Woman A");
        expectedClient1.setColumnmaps(column1);
        boolean filter = commonObjectFilterOption.filter(expectedClient1);
        assertTrue(filter);
    }

}
