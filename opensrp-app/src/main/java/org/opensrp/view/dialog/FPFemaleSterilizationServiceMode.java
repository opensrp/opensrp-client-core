package org.opensrp.view.dialog;

import org.opensrp.R;
import org.opensrp.provider.SmartRegisterClientsProvider;

import static org.opensrp.Context.getInstance;

public class FPFemaleSterilizationServiceMode extends FPAllMethodsServiceMode {

    public FPFemaleSterilizationServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_register_service_mode_female_sterilization);
    }
}
