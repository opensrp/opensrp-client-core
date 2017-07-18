package org.ei.opensrp.view.dialog;

import org.ei.opensrp.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;

import static org.ei.opensrp.Context.getInstance;

public class FPPrioritizationOneChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationOneChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_one_child_service_mode);
    }
}
