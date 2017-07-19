package org.opensrp.view.dialog;

import org.opensrp.R;
import org.opensrp.provider.SmartRegisterClientsProvider;

import static org.opensrp.Context.getInstance;

public class FPPrioritizationTwoPlusChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationTwoPlusChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_two_plus_children_service_mode);
    }
}
