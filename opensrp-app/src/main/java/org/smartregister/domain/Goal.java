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
}
