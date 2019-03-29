package org.smartregister.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 26/03/2019
 */

@RunWith(MockitoJUnitRunner.class)
public class P2PSyncAuthorizationServiceTest {

    private String teamId = "some-team-id";
    private P2PSyncAuthorizationService p2PSyncAuthorizationService;

    @Before
    public void setUp() throws Exception {
        p2PSyncAuthorizationService = new P2PSyncAuthorizationService(teamId);
    }

    @Test
    public void initShouldAddTeamId() {
        Map<String, Object> authorizationDetails = ReflectionHelpers.getField(p2PSyncAuthorizationService, "authorizationDetails");

        assertEquals(teamId, authorizationDetails.get(AllConstants.PeerToPeer.KEY_TEAM_ID));
    }

    @Test
    public void authorizeConnection() {

    }

    @Test
    public void getAuthorizationDetails() {
    }
}