package org.opensrp.view.dialog;

import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClient;

import static org.opensrp.AllConstants.OUT_OF_AREA;


public class OutOfAreaFilter implements FilterOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.filter_by_out_of_area_label);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return OUT_OF_AREA.equalsIgnoreCase(client.locationStatus());
    }
}
