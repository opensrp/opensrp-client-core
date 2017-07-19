package org.smartregister.view.dialog;

import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

import static org.smartregister.Context.getInstance;

public class FPMaleSterilizationServiceMode extends FPAllMethodsServiceMode {

    public FPMaleSterilizationServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_register_service_mode_male_sterilization);
    }
}
