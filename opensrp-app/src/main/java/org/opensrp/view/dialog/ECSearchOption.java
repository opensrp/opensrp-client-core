package org.opensrp.view.dialog;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.view.contract.SmartRegisterClient;

public class ECSearchOption implements FilterOption {
    private final String criteria;

    public ECSearchOption(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.str_ec_search_hint);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return StringUtils.isBlank(criteria) || client.satisfiesFilter(criteria);
    }
}
