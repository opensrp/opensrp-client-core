package org.ei.opensrp.view.dialog;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;

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
