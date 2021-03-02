package org.smartregister.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 01-03-2021.
 */
public class PractitionerRole implements Serializable {

    private static final long serialVersionUID = -2472589757270251270L;
    @JsonProperty
    private String identifier;

    @JsonProperty
    private Boolean active;

    @JsonProperty
    @SerializedName("organization")
    private String organizationIdentifier;

    @JsonProperty
    @SerializedName("practitioner")
    private String practitionerIdentifier;

    @JsonProperty
    private PractitionerRoleCode code;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getOrganizationIdentifier() {
        return organizationIdentifier;
    }

    public void setOrganizationIdentifier(String organizationIdentifier) {
        this.organizationIdentifier = organizationIdentifier;
    }

    public String getPractitionerIdentifier() {
        return practitionerIdentifier;
    }

    public void setPractitionerIdentifier(String practitionerIdentifier) {
        this.practitionerIdentifier = practitionerIdentifier;
    }

    public PractitionerRoleCode getCode() {
        return code;
    }

    public void setCode(PractitionerRoleCode code) {
        this.code = code;
    }
}