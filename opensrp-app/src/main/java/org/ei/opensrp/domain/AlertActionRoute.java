package org.ei.opensrp.domain;

import org.ei.opensrp.Context;
import org.ei.drishti.dto.Action;

public enum AlertActionRoute {
    CREATE_ALERT("createAlert") {
        @Override
        public void direct(Action action) {
            Context.getInstance().alertService().create(action);
        }
    },
    CLOSE_ALERT("closeAlert") {
        @Override
        public void direct(Action action) {
            Context.getInstance().alertService().close(action);
        }
    },
    DELETE_ALL_ALERTS("deleteAllAlerts") {
        @Override
        public void direct(Action action) {
            Context.getInstance().alertService().deleteAll(action);
        }
    };

    private String identifier;

    AlertActionRoute(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public abstract void direct(Action action);
}
