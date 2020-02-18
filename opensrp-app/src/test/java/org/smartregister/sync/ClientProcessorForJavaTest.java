package org.smartregister.sync;

import android.content.ContentValues;
import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.ColumnType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.smartregister.sync.ClientProcessorForJava.JSON_ARRAY;


public class ClientProcessorForJavaTest extends BaseUnitTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private Context context;

    @Test
    public void testGetFormattedValueDate() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);

        Column column = new Column();
        column.dataType = ColumnType.Date;
        column.saveFormat = "yyyy-MM-dd";
        column.sourceFormat = "dd-MM-yyyy";
        String columnValue = "16-04-2019";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "2019-04-16");
    }

    @Test
    public void testGetFormattedValueString() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);

        Column column = new Column();
        column.dataType = ColumnType.String;
        column.saveFormat = "Sheila is %s";
        String columnValue = "smart";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "Sheila is smart");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessEventMarksItSaved() throws Exception {
        ClientProcessorForJava clientProcessorForJava = Mockito.spy(new ClientProcessorForJava(context));
        Event mockEvent = Mockito.mock(Event.class);
        clientProcessorForJava.processEvent(mockEvent, null, null);

        Mockito.verify(clientProcessorForJava).completeProcessing(mockEvent);
    }

    @Test
    public void testGetValuesStrShouldGetCorrectlyFormattedString() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        List<String> values = new ArrayList<>();
        Obs obs = new Obs();
        obs.withsaveObsAsArray(true);
        String valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, JSON_ARRAY);
        assertNull(valStr);

        values.add("val1");
        values.add("val2");
        values.add("val3");
        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, null);
        assertEquals("[\"val1\",\"val2\",\"val3\"]", valStr);

        obs.setSaveObsAsArray(false);
        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, JSON_ARRAY);
        assertEquals("[\"val1\",\"val2\",\"val3\"]", valStr);

        valStr = Whitebox.invokeMethod(clientProcessor, "getValuesStr", obs, values, null);
        assertEquals("val1", valStr);
    }

    @Test
    public void testGetClientAttributesShouldReturnRequiredValues() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("national_id", "3434-34");
        attributes.put("drivers-license", "DL-324");
        Client client = new Client("123-23");
        client.setAttributes(attributes);
        Map<String, Object> result = Whitebox.invokeMethod(clientProcessor, "getClientAttributes", client);
        assertEquals(attributes, result);
    }

    @Test
    public void testGetGenderShouldReturnCorrectValue() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        Client client = new Client("123-23");
        client.setGender("Female");
        Map<String, String> resultMap = Whitebox.invokeMethod(clientProcessor, "getGender", client);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("gender", "Female");
        assertEquals(expectedMap, resultMap);
    }

    @Test
    public void testUpdateIdenitifierShouldRemoveHyphenFromOpenmrsId() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);
        ContentValues contentValues = new ContentValues();
        contentValues.put("zeir_id", "23-23");
        Whitebox.setInternalState(clientProcessor, "openmrsGenIds", new String[]{"zeir_id"});
        Whitebox.invokeMethod(clientProcessor, "updateIdenitifier", contentValues);
        assertEquals("2323", contentValues.get("zeir_id"));
    }
}
