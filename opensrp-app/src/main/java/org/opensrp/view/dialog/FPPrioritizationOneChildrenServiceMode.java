package org.opensrp.view.dialog;

import org.opensrp.R;
import org.opensrp.provider.SmartRegisterClientsProvider;

import static org.opensrp.Context.getInstance;

public class FPPrioritizationOneChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationOneChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_one_child_service_mode);
    }
}
