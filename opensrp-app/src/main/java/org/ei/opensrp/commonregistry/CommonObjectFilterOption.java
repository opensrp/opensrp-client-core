package org.ei.opensrp.commonregistry;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.FilterOption;

public class CommonObjectFilterOption implements FilterOption {
    private final String criteria;
    public final String fieldname;
    private final String filterOptionName;
    ByColumnAndByDetails byColumnAndByDetails;

    public enum ByColumnAndByDetails{
        byColumn,byDetails;
    }

    public CommonObjectFilterOption(String criteria,String fieldname,ByColumnAndByDetails byColumnAndByDetails,String filteroptionname) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.byColumnAndByDetails= byColumnAndByDetails;
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String name() {
        return filterOptionName;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        switch (byColumnAndByDetails){
            case byColumn:
                return ((CommonPersonObjectClient)client).getColumnmaps().get(fieldname).contains(criteria);
            case byDetails:
                return (((CommonPersonObjectClient)client).getDetails().get(fieldname)!=null?((CommonPersonObjectClient)client).getDetails().get(fieldname):"").toLowerCase().contains(criteria.toLowerCase());
        }
        return false;
    }
}
