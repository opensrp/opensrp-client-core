package org.opensrp.view.dialog;

import org.opensrp.view.contract.SmartRegisterClient;

public interface EditOption extends DialogOption {
    public void doEdit(SmartRegisterClient client);
    public void doEditWithMetadata(SmartRegisterClient client, String metadata);
}
