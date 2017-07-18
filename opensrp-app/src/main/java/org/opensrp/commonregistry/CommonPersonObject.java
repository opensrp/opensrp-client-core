package org.opensrp.commonregistry;

import java.util.Map;

/**
 * Created by Raihan Ahmed on 4/15/15.
 */
public class CommonPersonObject {
    private String caseId;
    private Map<String, String> details;
    private String type;
    private String relationalid;
    private Map<String, String> columnmaps;
    private short closed;

    public Map<String, String> getColumnmaps() {
        return columnmaps;
    }

    public void setColumnmaps(Map<String, String> columnmaps) {
        this.columnmaps = columnmaps;
    }



    public CommonPersonObject(String caseId, String relationalid, Map<String, String> details, String type) {
        this.details = details;
        this.caseId = caseId;
        this.type = type;
        this.relationalid = relationalid;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getRelationalId() {
        return relationalid;
    }

    public void setClosed(short closed) {
        this.closed = closed;
    }

    public short getClosed() {
        return closed;
    }
}
