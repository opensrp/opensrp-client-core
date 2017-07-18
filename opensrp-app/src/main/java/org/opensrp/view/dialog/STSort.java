package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.opensrp.view.contract.SmartRegisterClient.ST_COMPARATOR;

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
