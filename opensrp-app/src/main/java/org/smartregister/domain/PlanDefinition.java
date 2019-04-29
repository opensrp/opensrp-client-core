package org.smartregister.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class PlanDefinition {

    private String identifier;

    private String version;

    private String name;

    private String title;

    private String status;

    private String date;

    private ExecutionPeriod executionPeriod;

    private List<UseContext> useContext;

    private Map<String, String> jurisdiction;

    @SerializedName("goal")
    private List<Goal> goals;

    @SerializedName("action")
    private List<Action> actions;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ExecutionPeriod getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(ExecutionPeriod executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public List<UseContext> getUseContext() {
        return useContext;
    }

    public void setUseContext(List<UseContext> useContext) {
        this.useContext = useContext;
    }

    public Map<String, String> getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Map<String, String> jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    class UseContext {
        private String code;

        private String valueCodableConcept;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValueCodableConcept() {
            return valueCodableConcept;
        }

        public void setValueCodableConcept(String valueCodableConcept) {
            this.valueCodableConcept = valueCodableConcept;
        }
    }

}



