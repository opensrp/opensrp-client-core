package org.smartregister.domain.db;

import androidx.annotation.NonNull;

import java.util.List;

public class EventClientQueryResult {

    private List<EventClient> eventClientList;
    private int maxRowId;

    public EventClientQueryResult(int maxRowId, @NonNull List<EventClient> eventClients) {
        this.maxRowId = maxRowId;
        this.eventClientList = eventClients;
    }

    public List<EventClient> getEventClientList() {
        return eventClientList;
    }

    public void setEventClientList(List<EventClient> eventClientList) {
        this.eventClientList = eventClientList;
    }

    public int getMaxRowId() {
        return maxRowId;
    }

    public void setMaxRowId(int maxRowId) {
        this.maxRowId = maxRowId;
    }
}
