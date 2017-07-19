package org.smartregister.view.dialog;

import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.Collections;

public class DateOfDeliverySort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_date_of_delivery_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, PNCSmartRegisterClient.DATE_OF_DELIVERY_COMPARATOR);
        return allClients;
    }
}
