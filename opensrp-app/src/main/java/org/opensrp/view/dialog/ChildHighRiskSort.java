package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.opensrp.view.contract.ChildSmartRegisterClient.HR_COMPARATOR;

public class ChildHighRiskSort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_child_hr);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, HR_COMPARATOR);
        return allClients;
    }
}
