package org.smartregister.view.dialog;

import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

import static org.smartregister.Context.getInstance;

public class FPPrioritizationOneChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationOneChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_one_child_service_mode);
    }
}
