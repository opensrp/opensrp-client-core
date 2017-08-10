package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

public class FPIUCDServiceMode extends FPAllMethodsServiceMode {

    public FPIUCDServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.fp_register_service_mode_iucd);
    }
}
