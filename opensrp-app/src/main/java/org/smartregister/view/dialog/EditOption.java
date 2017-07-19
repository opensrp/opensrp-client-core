package org.smartregister.view.dialog;

import org.smartregister.view.contract.SmartRegisterClient;

public interface EditOption extends DialogOption {
    public void doEdit(SmartRegisterClient client);
    public void doEditWithMetadata(SmartRegisterClient client, String metadata);
}
