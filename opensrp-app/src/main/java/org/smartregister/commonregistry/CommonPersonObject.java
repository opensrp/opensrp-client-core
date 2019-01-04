package org.smartregister.commonregistry;

import java.util.Map;

/**
 * <p>Provides basic information about a person</p>
 * <ul>
 *     <li><b>CaseID</b> Primary Key of the Client - BaseEntityID </li>
 *     <li><b>Details</b> A map object that returns a list of parameters and the results</li>
 *     <li><b>Type</b> ? </li>
 *     <li><b>RelationalID</b> Primary Key of local patient - BaseEntityID </li>
 *     <li><b>Closed</b> if a case is closed </li>
 *     <li><b>Columnmaps</b> Local Column Names </li>
 * </ul>
 *
 * @author Raihan Ahmed
 * @since 2015-15-04
 * @version 0.1
 */
public class CommonPersonObject {
    private String caseId;
    private Map<String, String> details;
    private String type;
    private String relationalid;
    private Map<String, String> columnmaps;
    private short closed;

    public CommonPersonObject(String caseId, String relationalid, Map<String, String> details,
                              String type) {
        this.details = details;
        this.caseId = caseId;
        this.type = type;
        this.relationalid = relationalid;
    }

    public Map<String, String> getColumnmaps() {
        return columnmaps;
    }

    public void setColumnmaps(Map<String, String> columnmaps) {
        this.columnmaps = columnmaps;
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

    public short getClosed() {
        return closed;
    }

    public void setClosed(short closed) {
        this.closed = closed;
    }
}
