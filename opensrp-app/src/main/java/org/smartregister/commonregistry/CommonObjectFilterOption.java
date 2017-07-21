package org.smartregister.commonregistry;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.FilterOption;

public class CommonObjectFilterOption implements FilterOption {
    private final String criteria;
    public final String fieldname;
    private final String filterOptionName;
    ByColumnAndByDetails byColumnAndByDetails;

    public enum ByColumnAndByDetails{
        byColumn, byDetails
    }

    public CommonObjectFilterOption(String criteriaArg,
                                    String fieldnameArg,
                                    ByColumnAndByDetails byColumnAndByDetailsArg,
                                    String filteroptionnameArg) {
        criteria = criteriaArg;
        fieldname = fieldnameArg;
        byColumnAndByDetails = byColumnAndByDetailsArg;
        filterOptionName = filteroptionnameArg;
    }

    @Override
    public String name() {
        return filterOptionName;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        switch (byColumnAndByDetails) {
            case byColumn:
                return ((CommonPersonObjectClient) client).getColumnmaps().get(fieldname).
                        contains(criteria);
            case byDetails:
                return (((CommonPersonObjectClient) client).getDetails().get(fieldname) != null
                        ? ((CommonPersonObjectClient) client).getDetails().get(fieldname) : "").
                        toLowerCase().contains(criteria.toLowerCase());
        }
        return false;
    }
}
