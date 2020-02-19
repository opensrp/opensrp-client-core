package org.smartregister.sync;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.ColumnType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.smartregister.sync.ClientProcessorForJava.JSON_ARRAY;


public class ClientProcessorForJavaTest extends BaseUnitTest {

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
    public void testProcessEventUsingMiniProcessorShouldCallMiniProcessorProcessEventClient() throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = Mockito.mock(MiniClientProcessorForJava.class);
        String eventType = "Custom Test Event";
        HashSet<String> supportedEventTypes = new HashSet<>();
        supportedEventTypes.add(eventType);
        Mockito.doReturn(supportedEventTypes).when(miniClientProcessorForJava).getEventTypes();

        String baseEntityId = "bei";
        EventClient eventClient = new EventClient(new Event(), new Client(baseEntityId));

        ClientProcessorForJava clientProcessorForJava = Mockito.spy(new ClientProcessorForJava(context));
        clientProcessorForJava.addMiniProcessors(miniClientProcessorForJava);

        ClientClassification clientClassification = new ClientClassification();
        clientProcessorForJava.processEventUsingMiniProcessor(clientClassification, eventClient, eventType);

        Mockito.verify(miniClientProcessorForJava, Mockito.times(1)).processEventClient(Mockito.eq(eventClient), ArgumentMatchers.<List<Event>>any(), Mockito.eq(clientClassification));
    }
}
