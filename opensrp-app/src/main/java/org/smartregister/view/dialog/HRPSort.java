package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;

public class HRPSort extends HighRiskSort {
    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.sort_by_high_risk_pregnancy_label);
    }

}
