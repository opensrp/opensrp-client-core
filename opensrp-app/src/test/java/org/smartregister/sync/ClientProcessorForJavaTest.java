package org.smartregister.sync;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.db.Event;

public class ClientProcessorForJavaTest extends BaseUnitTest {

    @Mock
    private Context context;


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
}
