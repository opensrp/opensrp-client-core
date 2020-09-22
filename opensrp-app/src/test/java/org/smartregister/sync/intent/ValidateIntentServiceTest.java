package org.smartregister.sync.intent;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vincent Karuri on 22/09/2020
 */
public class ValidateIntentServiceTest extends BaseUnitTest {

    private ValidateIntentService validateIntentService;

    @Mock
    private HTTPAgent httpAgent;
    @Mock
    private Context openSRPContext;
    @Mock
    private EventClientRepository eventClientRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMethods();
        validateIntentService = Robolectric.buildIntentService(ValidateIntentService.class).get();
    }

    @Test
    public void testOnHandleIntent() {
        ReflectionHelpers.setField(validateIntentService, "context", RuntimeEnvironment.application);
        ReflectionHelpers.setField(validateIntentService, "httpAgent", httpAgent);
        ReflectionHelpers.setField(validateIntentService, "openSRPContext", openSRPContext);

        validateIntentService.onHandleIntent(new Intent());

        Mockito.verify(eventClientRepository).markClientValidationStatus("client_id1", false);
        Mockito.verify(eventClientRepository).markClientValidationStatus("client_id2", true);

        Mockito.verify(eventClientRepository).markEventValidationStatus("event_id1", false);
        Mockito.verify(eventClientRepository).markEventValidationStatus("event_id2", true);
    }

    private void mockMethods() throws JSONException {
        Mockito.doReturn(eventClientRepository).when(openSRPContext).getEventClientRepository();
        Mockito.doReturn(new ArrayList(Arrays.asList("client_id1", "client_id2"))).when(eventClientRepository)
                .getUnValidatedClientBaseEntityIds(ArgumentMatchers.anyInt());
        Mockito.doReturn(new ArrayList(Arrays.asList("event_id1", "event_id2"))).when(eventClientRepository)
                .getUnValidatedEventFormSubmissionIds(ArgumentMatchers.anyInt());

        DristhiConfiguration dristhiConfiguration = Mockito.mock(DristhiConfiguration.class);
        Mockito.doReturn("/").when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(dristhiConfiguration).when(openSRPContext).configuration();

        JSONObject results = new JSONObject();
        JSONArray invalidClients = new JSONArray(Arrays.asList("client_id1"));
        results.put(AllConstants.KEY.CLIENTS, invalidClients);

        JSONArray invalidEvents = new JSONArray(Arrays.asList("event_id1"));
        results.put(AllConstants.KEY.EVENTS, invalidEvents);

        Response<String> response = new Response(ResponseStatus.success, results.toString());
        Mockito.doReturn(response).when(httpAgent).postWithJsonResponse(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}