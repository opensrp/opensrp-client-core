package org.ei.opensrp.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PregnancyDetails {
    private String monthsPregnant;
    private String edd;
    private boolean isEDDPassed;
    private boolean isLastMonthOfPregnancy;
    private int daysPastEdd;

    public PregnancyDetails(String monthsPregnant, String edd, int daysPastEdd) {
        this.monthsPregnant = monthsPregnant;
        this.edd = edd;
        isEDDPassed = (Integer.valueOf(monthsPregnant) >= 9) ? true : false;
        isLastMonthOfPregnancy = Integer.valueOf(monthsPregnant) >= 8;
        this.daysPastEdd = daysPastEdd;
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

    public boolean isLastMonthOfPregnancy() {
        return isLastMonthOfPregnancy;
    }
}
