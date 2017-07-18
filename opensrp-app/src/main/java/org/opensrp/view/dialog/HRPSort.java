package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.opensrp.view.contract.SmartRegisterClient.HIGH_RISK_COMPARATOR;

public class HRPSort extends HighRiskSort {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_high_risk_pregnancy_label);
    }

}
