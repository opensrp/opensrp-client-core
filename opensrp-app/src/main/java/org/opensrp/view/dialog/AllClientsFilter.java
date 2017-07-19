package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClient;

public class AllClientsFilter implements FilterOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.filter_by_all_label);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return true;
    }
}
