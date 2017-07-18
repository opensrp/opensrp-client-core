package org.ei.opensrp.service;

import com.google.gson.Gson;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.drishti.dto.Action;
import org.ei.opensrp.repository.AllReports;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.router.ActionRouter;
import org.ei.opensrp.util.Log;

import java.util.List;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.domain.FetchStatus.fetchedFailed;
import static org.ei.opensrp.domain.FetchStatus.nothingFetched;

public class ActionService {
    private final ActionRouter actionRouter;
    private DrishtiService drishtiService;
    private AllSettings allSettings;
    private AllSharedPreferences allSharedPreference;
    private AllReports allReports;

    public ActionService(DrishtiService drishtiService, AllSettings allSettings, AllSharedPreferences allSharedPreferences, AllReports allReports) {
       this(drishtiService, allSettings, allSharedPreferences, allReports, null);
    }

    public ActionService(DrishtiService drishtiService, AllSettings allSettings, AllSharedPreferences allSharedPreferences, AllReports allReports, ActionRouter actionRouter) {
        this.drishtiService = drishtiService;
        this.allSettings = allSettings;
        this.allSharedPreference = allSharedPreferences;
        this.allReports = allReports;
        this.actionRouter = actionRouter == null ? new ActionRouter() : actionRouter;
    }

    public FetchStatus fetchNewActions() {
        String previousFetchIndex = allSettings.fetchPreviousFetchIndex();
        Response<List<Action>> response = drishtiService.fetchNewActions(allSharedPreference.fetchRegisteredANM(), previousFetchIndex);

        if (response.isFailure()) {
            return fetchedFailed;
        }

        if (response.payload().isEmpty()) {
            return nothingFetched;
        }

        handleActions(response);
        return FetchStatus.fetched;
    }

    private void handleActions(Response<List<Action>> response) {
        for (Action actionToUse : response.payload()) {
            try {
                handleAction(actionToUse);
            } catch (Exception e) {
                Log.logError(format("Failed while handling action with target: {0} and exception: {1}", actionToUse.target(), e));
            }
        }
    }

    private void handleAction(Action actionToUse) {
        if ("report".equals(actionToUse.target())) {
            runAction(actionToUse, new ActionHandler() {
                @Override
                public void run(Action action) {
                    allReports.handleAction(action);
                }
            });

        } else if ("alert".equals(actionToUse.target())) {
            runAction(actionToUse, new ActionHandler() {
                @Override
                public void run(Action action) {
                    actionRouter.directAlertAction(action);
                }
            });

        } else if ("mother".equals(actionToUse.target())) {
            runAction(actionToUse, new ActionHandler() {
                @Override
                public void run(Action action) {
                    actionRouter.directMotherAction(action);
                }
            });

        } else {
            Log.logWarn("Unknown action " + actionToUse.target());
        }

        allSettings.savePreviousFetchIndex(actionToUse.index());
    }

    private void runAction(Action action, ActionHandler actionHandler) {
        try {
            actionHandler.run(action);
        } catch (Exception e) {
            throw new RuntimeException("Failed to run action: " + new Gson().toJson(action), e);
        }
    }

    private abstract class ActionHandler {
        public void run(Action action) {
        }
    }
}
