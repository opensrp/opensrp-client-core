package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.opensrp.view.contract.ANCSmartRegisterClient.EDD_COMPARATOR;

public class EstimatedDateOfDeliverySort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_edd_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, EDD_COMPARATOR);
        return allClients;
    }
}
