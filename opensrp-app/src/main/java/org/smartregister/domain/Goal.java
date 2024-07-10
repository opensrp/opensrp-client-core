package org.smartregister.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class Goal {

    private String id;

    private String description;

    private String priority;

    @SerializedName("target")
    private List<Target> targets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }
}
