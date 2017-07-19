package org.smartregister.view.dialog;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.view.contract.SmartRegisterClient;

public class ANCSearchOption implements FilterOption {
    private final String criteria;

    public ANCSearchOption(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.str_anc_search_hint);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return StringUtils.isBlank(criteria) || client.satisfiesFilter(criteria);
    }
}
