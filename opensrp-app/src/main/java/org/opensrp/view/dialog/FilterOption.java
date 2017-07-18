package org.opensrp.view.dialog;

import org.opensrp.view.contract.SmartRegisterClient;

public interface FilterOption extends DialogOption {
    public boolean filter(SmartRegisterClient client);
}
