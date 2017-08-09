package org.smartregister.service;

import org.apache.commons.io.IOUtils;
import org.ei.drishti.dto.Action;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.dto.AlertStatus.normal;
import static org.ei.drishti.dto.BeneficiaryType.mother;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.util.ActionBuilder.actionForCloseAlert;
import static org.smartregister.util.ActionBuilder.actionForCreateAlert;

@RunWith(RobolectricTestRunner.class)
public class DrishtiServiceTest {
    @Mock
    private HTTPAgent httpAgent;

    private DrishtiService drishtiService;
    public static final String EXPECTED_URL = "http://base.drishti.url/actions?anmIdentifier=anm1&timeStamp=0";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        drishtiService = new DrishtiService(httpAgent, "http://base.drishti.url");
    }

    @Test
    public void shouldFetchAlertActions() throws Exception {
        when(httpAgent.fetch(EXPECTED_URL)).thenReturn(new Response<String>(ResponseStatus.success, IOUtils.toString(getClass().getResource("/alerts.json"))));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        verify(httpAgent).fetch(EXPECTED_URL);
        assertEquals(asList(actionForCreateAlert("Case X", normal.value(), mother.value(), "Ante Natal Care - Normal", "ANC 1", "2012-01-01", "2012-01-11", "1333695798583"),
                actionForCloseAlert("Case Y", "ANC 1", "2012-01-01", "1333695798644")), actions.payload());
        assertEquals(ResponseStatus.success, actions.status());
    }

    @Test
    public void shouldFetchNoAlertActionsWhenJsonIsForEmptyList() throws Exception {
        when(httpAgent.fetch(EXPECTED_URL)).thenReturn(new Response<String>(ResponseStatus.success, "[]"));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        assertTrue(actions.payload().isEmpty());
    }

    @Test
    public void shouldFetchNoAlertActionsWhenHTTPCallFails() throws Exception {
        when(httpAgent.fetch(EXPECTED_URL)).thenReturn(new Response<String>(ResponseStatus.failure, null));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        assertTrue(actions.payload().isEmpty());
        assertEquals(ResponseStatus.failure, actions.status());
    }

    @Test
    public void shouldURLEncodeTheANMIdentifierPartWhenItHasASpace() {
        String expectedURLWithSpaces = "http://base.drishti.url/actions?anmIdentifier=ANM+WITH+SPACE&timeStamp=0";
        when(httpAgent.fetch(expectedURLWithSpaces)).thenReturn(new Response<String>(ResponseStatus.success, "[]"));

        drishtiService.fetchNewActions("ANM WITH SPACE", "0");

        verify(httpAgent).fetch(expectedURLWithSpaces);
    }

    @Test
    public void shouldReturnFailureResponseWhenJsonIsMalformed() {
        String expectedURLWithSpaces = "http://base.drishti.url/actions?anmIdentifier=ANMX&timeStamp=0";
        when(httpAgent.fetch(expectedURLWithSpaces)).thenReturn(new Response<String>(ResponseStatus.success, "[{\"anmIdentifier\": \"ANMX\", "));

        Response<List<Action>> actions = drishtiService.fetchNewActions("ANMX", "0");

        assertTrue(actions.payload().isEmpty());
        assertEquals(ResponseStatus.failure, actions.status());
    }
}
