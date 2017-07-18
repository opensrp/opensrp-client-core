package org.ei.opensrp.view.contract;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FPPrioritizationServiceModes {
    ALL_EC(Context.getInstance().getStringResource(R.string.fp_prioritization_all_ec_service_mode)),
    HIGH_PRIORITY(Context.getInstance().getStringResource(R.string.fp_prioritization_high_priority_service_mode)),
    TWO_PLUS_CHILDREN(Context.getInstance().getStringResource(R.string.fp_prioritization_two_plus_children_service_mode)),
    ONE_CHILDREN(Context.getInstance().getStringResource(R.string.fp_prioritization_one_child_service_mode));


    private String name;
    /** The map to hold all the objects of this enum. */
    private static final Map<String, FPPrioritizationServiceModes> serviceModeMap = new HashMap<String, FPPrioritizationServiceModes>();


    private FPPrioritizationServiceModes(String name) {
        this.name = name;
    }

    /**
     * Create a map with all enums
     */
    static {
        for (FPPrioritizationServiceModes serviceMode : EnumSet.allOf(FPPrioritizationServiceModes.class))
            serviceModeMap.put(serviceMode.toString(), serviceMode);
    }

    public static FPPrioritizationServiceModes valueOfIdentifier(String value) {
        return serviceModeMap.get(value);
    }

    @Override
    public String toString() {
        return name;
    }
}
