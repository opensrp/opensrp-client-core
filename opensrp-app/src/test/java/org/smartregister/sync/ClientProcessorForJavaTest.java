package org.smartregister.sync;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.ColumnType;

import static org.junit.Assert.assertEquals;

public class ClientProcessorForJavaTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Test
    public void testGetFormattedValueDate() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);

        Column column = new Column();
        column.data_type = ColumnType.Date;
        column.save_format = "yyyy-MM-dd";
        column.source_format = "dd-MM-yyyy";
        String columnValue = "16-04-2019";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "2019-04-16");
    }

    @Test
    public void testGetFormattedValueString() throws Exception {
        ClientProcessorForJava clientProcessor = new ClientProcessorForJava(context);

        Column column = new Column();
        column.data_type = ColumnType.String;
        column.save_format = "Sheila is %s";
        String columnValue = "smart";

        String res = Whitebox.invokeMethod(clientProcessor, "getFormattedValue", column, columnValue);

        assertEquals(res, "Sheila is smart");
    }
}
