package org.smartregister.router;

import org.ei.drishti.dto.Action;
import org.smartregister.domain.AlertActionRoute;
import org.smartregister.domain.MotherActionRoute;

import timber.log.Timber;

public class ActionRouter {
    public void directAlertAction(Action action) {
        AlertActionRoute[] alertActionRoutes = AlertActionRoute.values();
        for (AlertActionRoute alertActionRoute : alertActionRoutes) {
            if (alertActionRoute.identifier().equals(action.type())) {
                alertActionRoute.direct(action);
                return;
            }
        }
        Timber.w("Unknown type in Alert action: %s", action);
    }

    public void directMotherAction(Action action) {
        MotherActionRoute[] motherActionRoutes = MotherActionRoute.values();
        for (MotherActionRoute motherActionRoute : motherActionRoutes) {
            if (motherActionRoute.identifier().equals(action.type())) {
                motherActionRoute.direct(action);
                return;
            }
        }
        Timber.w("Unknown type in Mother action: %s", action);
    }
}
