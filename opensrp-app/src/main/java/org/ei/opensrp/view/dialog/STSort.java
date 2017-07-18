package org.ei.opensrp.view.dialog;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.ei.opensrp.view.contract.SmartRegisterClient.ST_COMPARATOR;

public class STSort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_st_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, ST_COMPARATOR);
        return allClients;
    }
}
