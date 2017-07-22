package org.smartregister.view.dialog;

import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

import static org.smartregister.Context.getInstance;

public class FPPrioritizationTwoPlusChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationTwoPlusChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance()
                .getStringResource(R.string.fp_prioritization_two_plus_children_service_mode);
    }
}
