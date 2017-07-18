package org.ei.opensrp.view.dialog;

import org.ei.opensrp.view.contract.SmartRegisterClient;

public interface FilterOption extends DialogOption {
    public boolean filter(SmartRegisterClient client);
}
