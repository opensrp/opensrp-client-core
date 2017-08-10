package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

public class FPPrioritizationTwoPlusChildrenServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationTwoPlusChildrenServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context()
                .getStringResource(R.string.fp_prioritization_two_plus_children_service_mode);
    }
}
