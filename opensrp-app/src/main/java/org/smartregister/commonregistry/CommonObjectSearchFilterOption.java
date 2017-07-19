package org.smartregister.commonregistry;

import org.smartregister.Context;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.FilterOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raihan Ahmed on 6/21/15.
 */
public class CommonObjectSearchFilterOption implements FilterOption {
    private final String criteria;
    ArrayList<FilterOptionsForSearch> filteroptions;
    public enum ByColumnAndByDetails{
        byColumn,byDetails,byChildren
    }

    public CommonObjectSearchFilterOption(String criteria, ArrayList<FilterOptionsForSearch> filtermaps) {
        this.criteria = criteria;
        this.filteroptions = filtermaps;

    }

    @Override
    public String name() {
        return "Search";
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        for(int i = 0;i<filteroptions.size();i++) {

        switch (filteroptions.get(i).byColumnAndByDetails){
            case byColumn:
                return ((CommonPersonObjectClient)client).getColumnmaps().get(filteroptions.get(i).fieldname).trim().toLowerCase().contains(criteria.trim().toLowerCase());
            case byDetails:
                return (((CommonPersonObjectClient)client).getDetails().get(filteroptions.get(i).fieldname)!=null?((CommonPersonObjectClient)client).getDetails().get(filteroptions.get(i).fieldname):"").trim().toLowerCase().contains(criteria.toLowerCase());
            case byChildren:
                CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
                AllCommonsRepository allchildRepository = Context.getInstance().allCommonsRepositoryobjects(filteroptions.get(i).childname);
                ArrayList<String> list = new ArrayList<String>();
                list.add((currentclient.entityId()));
                List<CommonPersonObject> allchild = allchildRepository.findByRelational_IDs(list);
                for (int j = 0; j < allchild.size(); j++) {
                    switch (filteroptions.get(i).byChildColumnAndByDetails) {
                        case byDetails:
                        if (allchild.get(j).getDetails().get(filteroptions.get(i).fieldname) != null) {
                            if (allchild.get(i).getDetails().get(filteroptions.get(i).fieldname).toLowerCase().trim().contains(criteria.toLowerCase().trim())) {
                                return true;
                            }
                        }
                        break;
                        case byColumn:
                            if (allchild.get(j).getColumnmaps().get(filteroptions.get(i).fieldname) != null) {
                                if (allchild.get(i).getColumnmaps().get(filteroptions.get(i).fieldname).toLowerCase().trim().contains(criteria.toLowerCase().trim())) {
                                    return true;
                                }
                            }
                            break;
                    }
                }
                break;
        }
        }
        return false;
    }

    static class FilterOptionsForSearch{
            String childname;
        ByColumnAndByDetails byColumnAndByDetails;
        ByColumnAndByDetails byChildColumnAndByDetails;
        String fieldname;

        FilterOptionsForSearch(ByColumnAndByDetails byColumnAndByDetails, String fieldname) {
            this.byColumnAndByDetails = byColumnAndByDetails;
            this.fieldname = fieldname;
        }

        FilterOptionsForSearch(String childname, ByColumnAndByDetails byColumnAndByDetails, String fieldname, ByColumnAndByDetails byChildColumnAndByDetails) {
            this.childname = childname;
            this.byColumnAndByDetails = byColumnAndByDetails;
            this.fieldname = fieldname;
            this.byChildColumnAndByDetails = byChildColumnAndByDetails;
        }
    }
}
