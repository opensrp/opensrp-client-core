package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

public class FPFemaleSterilizationServiceMode extends FPAllMethodsServiceMode {

    public FPFemaleSterilizationServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context()
                .getStringResource(R.string.fp_register_service_mode_female_sterilization);
    }
}
