package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

public class FPPrioritizationOneChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationOneChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.fp_prioritization_one_child_service_mode);
    }
}
