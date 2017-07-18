package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.ECClient;
import org.opensrp.view.contract.ECSmartRegisterBaseClient;
import org.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

public class ECNumberSort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_ec_number_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, ECSmartRegisterBaseClient.EC_NUMBER_COMPARATOR);
        return allClients;
    }
}
