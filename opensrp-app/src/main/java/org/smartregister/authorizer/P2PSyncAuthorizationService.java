package org.smartregister.authorizer;

import android.support.annotation.NonNull;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 22/03/2019
 */

public class P2PSyncAuthorizationService implements P2PAuthorizationService {

    private Map<String, Object> authorizationDetails;
    private static final String KEY_TEAM_ID = "team-id";

    @Override
    public void authorizeConnection(@NonNull Map<String, Object> peerDeviceMap, @NonNull final AuthorizationCallback authorizationCallback) {
        getAuthorizationDetails(new OnAuthorizationDetailsProvidedCallback() {
            @Override
            public void onAuthorizationDetailsProvided(@NonNull Map<String, Object> map) {
                Object peerDeviceTeamId = map.get(KEY_TEAM_ID);
                if (peerDeviceTeamId != null && peerDeviceTeamId instanceof String && ((String) peerDeviceTeamId).equals(map.get(KEY_TEAM_ID))) {
                    authorizationCallback.onConnectionAuthorized();
                } else {
                    authorizationCallback.onConnectionAuthorizationRejected("Incorrect authorization details provided");
                }
            }
        });
    }

    @Override
    public void getAuthorizationDetails(@NonNull OnAuthorizationDetailsProvidedCallback onAuthorizationDetailsProvidedCallback) {
        if (authorizationDetails == null) {
            // Todo: Add the authorization details here
        } else {
            onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails);
        }
    }
}
