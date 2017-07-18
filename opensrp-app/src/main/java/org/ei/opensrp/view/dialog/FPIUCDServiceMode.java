package org.ei.opensrp.view.dialog;

import org.ei.opensrp.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;

import static org.ei.opensrp.Context.getInstance;

public class FPIUCDServiceMode extends FPAllMethodsServiceMode {

    public FPIUCDServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_register_service_mode_iucd);
    }
}
