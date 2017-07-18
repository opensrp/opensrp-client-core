package org.ei.opensrp.view.dialog;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.opensrp.view.contract.SmartRegisterClient;

public class PNCServiceModeFilter implements FilterOption {
    private final String fpMethodName;
    private String allMethodsIdentifier;

    public PNCServiceModeFilter(String fpMethodName) {

        this.fpMethodName = fpMethodName;
        this.allMethodsIdentifier = "Overview";
    }

    @Override
    public String name() {
        return fpMethodName;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return name().equalsIgnoreCase(allMethodsIdentifier);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
