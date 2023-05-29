package org.smartregister.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.io.IOUtils;
import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.AlertStatus;
import org.ei.drishti.dto.BeneficiaryType;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.util.ActionBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DrishtiServiceTest extends BaseUnitTest {
    @Mock
    private HTTPAgent httpAgent;

    private DrishtiService drishtiService;
    public static final String EXPECTED_URL = "http://base.drishti.url/actions";
    private static final String ACTION_JSON = "{\"anmIdentifier\":\"anm1\",\"timeStamp\":\"0\"}";

    @Before
    public void setUp() throws Exception {
        
        drishtiService = new DrishtiService(httpAgent, "http://base.drishti.url");
    }

    @Test
    public void shouldFetchAlertActions() throws Exception {
        String anm = "anm1";
        String timestamp = "0";

        JSONObject json = new JSONObject();
        json.put("anmIdentifier", anm);
        json.put("timeStamp", timestamp);

        Mockito.when(httpAgent.post(EXPECTED_URL, json.toString()))
                .thenReturn(new Response<String>(ResponseStatus.success, IOUtils.toString(getClass().getResource("/alerts.json"))));

        Response<List<Action>> actions = drishtiService.fetchNewActions(anm, timestamp);

        Mockito.verify(httpAgent).post(EXPECTED_URL, json.toString());
        Assert.assertEquals(
                Arrays.asList(
                        ActionBuilder.actionForCreateAlert("Case X", AlertStatus.normal.value(), BeneficiaryType.mother.value(), "Ante Natal Care - Normal", "ANC 1", "2012-01-01", "2012-01-11", "1333695798583"),
                        ActionBuilder.actionForCloseAlert("Case Y", "ANC 1", "2012-01-01", "1333695798644")
                ),
                actions.payload()
        );
        Assert.assertEquals(ResponseStatus.success, actions.status());
    }

    @Test
    public void shouldFetchNoAlertActionsWhenJsonIsForEmptyList() throws Exception {
        Mockito.when(httpAgent.post(EXPECTED_URL, ACTION_JSON)).thenReturn(new Response<String>(ResponseStatus.success, "[]"));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        assertTrue(actions.payload().isEmpty());
    }

    @Test
    public void shouldFetchNoAlertActionsWhenHTTPCallFails() throws Exception {
        Mockito.when(httpAgent.post(EXPECTED_URL, ACTION_JSON)).thenReturn(new Response<String>(ResponseStatus.failure, null));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        assertTrue(actions.payload().isEmpty());
        Assert.assertEquals(ResponseStatus.failure, actions.status());
    }

    @Test
    public void shouldURLEncodeTheANMIdentifierPartWhenItHasASpace() {
        String expectedURLWithSpaces = "http://base.drishti.url/actions";
        String json = "{\"anmIdentifier\":\"ANM WITH SPACE\",\"timeStamp\":\"0\"}";

        Mockito.when(httpAgent.post(expectedURLWithSpaces, json)).thenReturn(new Response<String>(ResponseStatus.success, "[]"));

        drishtiService.fetchNewActions("ANM WITH SPACE", "0");

        Mockito.verify(httpAgent).post(expectedURLWithSpaces, json);
    }

    @Test
    public void shouldReturnFailureResponseWhenJsonIsMalformed() {
        String expectedURLWithSpaces = "http://base.drishti.url/actions";
        Mockito.when(httpAgent.post(expectedURLWithSpaces, ACTION_JSON))
                .thenReturn(new Response<String>(ResponseStatus.success, "[{\"anmIdentifier\": \"anm1\", "));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        assertTrue(actions.payload().isEmpty());
        Assert.assertEquals(ResponseStatus.failure, actions.status());
    }

    @Test
    public void testStartOnlyIfConnectedToNetwork() {
        Context context = Mockito.mock(Context.class);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(networkInfo.isConnected()).thenReturn(true);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(connectivityManager);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(context);
        assertTrue(networkInfo.isConnected());
    }
}
