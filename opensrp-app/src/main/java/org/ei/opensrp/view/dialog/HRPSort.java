package org.ei.opensrp.view.dialog;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.ei.opensrp.view.contract.SmartRegisterClient.HIGH_RISK_COMPARATOR;

public class HRPSort extends HighRiskSort {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_high_risk_pregnancy_label);
    }

}
