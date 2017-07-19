package org.opensrp.router;

import org.opensrp.domain.AlertActionRoute;
import org.opensrp.domain.MotherActionRoute;
import org.ei.drishti.dto.Action;

import static org.opensrp.util.Log.logWarn;

public class ActionRouter {
    public void directAlertAction(Action action) {
        AlertActionRoute[] alertActionRoutes = AlertActionRoute.values();
        for (AlertActionRoute alertActionRoute : alertActionRoutes) {
            if (alertActionRoute.identifier().equals(action.type())) {
                alertActionRoute.direct(action);
                return;
            }
        }
        logWarn("Unknown type in Alert action: " + action);
    }

    public void directMotherAction(Action action) {
        MotherActionRoute[] motherActionRoutes = MotherActionRoute.values();
        for (MotherActionRoute motherActionRoute : motherActionRoutes) {
            if (motherActionRoute.identifier().equals(action.type())) {
                motherActionRoute.direct(action);
                return;
            }
        }
        logWarn("Unknown type in Mother action: " + action);
    }
}
