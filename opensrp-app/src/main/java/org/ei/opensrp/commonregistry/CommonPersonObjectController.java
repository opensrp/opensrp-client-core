package org.ei.opensrp.commonregistry;

import android.util.Log;

import com.google.gson.Gson;

import org.ei.opensrp.Context;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
/**
 * Created by Raihan Ahmed on 4/15/15.
 */
public class CommonPersonObjectController {

    private  final String person_CLIENTS_LIST;

    private final AllCommonsRepository allpersonobjects;
    private final AllBeneficiaries allBeneficiaries;
    private final Cache<String> cache;
    private final Cache<CommonPersonObjectClients> personObjectClientsCache;
    public final String nameString;
    public  String filterkey = null;
    public String filtervalue = null;
    public String null_check_key = "";
    public boolean filtercase = true;
    ByColumnAndByDetails byColumnAndByDetails;
    ByColumnAndByDetails byColumnAndByDetailsNullcheck;
    SortOption sortOption;

    ArrayList <ControllerFilterMap> filtermap;

    public enum ByColumnAndByDetails{
        byColumn,byDetails,byrelationalid,byrelational_id;
    }


    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"ClientsList";
        this.nameString = nameString;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;

    }
    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,String filterkey,String filtervalue,ByColumnAndByDetails byColumnAndByDetails,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck ) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"_"+filterkey+"_"+filtervalue+"ClientsList";
        this.nameString = nameString;
        this.filterkey = filterkey;
        this.filtervalue = filtervalue;
        this.byColumnAndByDetails = byColumnAndByDetails;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;
    }
    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck,SortOption sortOption) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"ClientsList";
        this.nameString = nameString;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;
        this.sortOption = sortOption;

    }
    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,String filterkey,String filtervalue,ByColumnAndByDetails byColumnAndByDetails,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck,SortOption sortOption ) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"_"+filterkey+"_"+filtervalue+"ClientsList";
        this.nameString = nameString;
        this.filterkey = filterkey;
        this.filtervalue = filtervalue;
        this.byColumnAndByDetails = byColumnAndByDetails;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;
        this.sortOption = sortOption;
    }

    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,String filterkey,String filtervalue,boolean filterCase,ByColumnAndByDetails byColumnAndByDetails,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck,SortOption sortOption ) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"_"+filterkey+"_"+filtervalue+"ClientsList";
        this.nameString = nameString;
        this.filterkey = filterkey;
        this.filtervalue = filtervalue;
        this.filtercase = filterCase;
        this.byColumnAndByDetails = byColumnAndByDetails;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;
        this.sortOption = sortOption;
    }
    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,ArrayList <ControllerFilterMap> filtermap,ByColumnAndByDetails byColumnAndByDetails,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck,SortOption sortOption ) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"_"+filterkey+"_"+filtervalue+"ClientsList";
        this.nameString = nameString;
        this.filtermap = filtermap;
        this.byColumnAndByDetails = byColumnAndByDetails;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;
        this.sortOption = sortOption;
    }
    public CommonPersonObjectController(AllCommonsRepository allpersons,
                                        AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                        Cache<CommonPersonObjectClients> personClientsCache, String nameString, String bindtype,ArrayList <ControllerFilterMap> filtermap,ByColumnAndByDetails byColumnAndByDetails,String null_check_key,ByColumnAndByDetails byColumnAndByDetailsNullcheck ) {
        this.allpersonobjects = allpersons;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.personObjectClientsCache = personClientsCache;
        this.person_CLIENTS_LIST = bindtype+"_"+filterkey+"_"+filtervalue+"ClientsList";
        this.nameString = nameString;
        this.filtermap = filtermap;
        this.byColumnAndByDetails = byColumnAndByDetails;
        this.null_check_key = null_check_key;
        this.byColumnAndByDetailsNullcheck = byColumnAndByDetailsNullcheck;
    }

    public String get() {
        return cache.get(person_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<CommonPersonObject> p = allpersonobjects.all();
                updateDetails(p);
                CommonPersonObjectClients pClients = new CommonPersonObjectClients();
                if(filtermap != null){
                    for (CommonPersonObject personinlist : p) {
                        boolean filter = false;
                        Log.v("is filtermap ", "" + filtermap.size());
                        for(int k = 0;k<filtermap.size();k++) {
                            filter = filtermap.get(k).filtermapLogic(personinlist);
                        }

                        if (!isnull(personinlist) && filter) {
                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
//                    pClient.entityID = personinlist.getCaseId();
                            pClient.setColumnmaps(personinlist.getColumnmaps());
                            pClients.add(pClient);

                        }
                    }
                }
                else if(filterkey == null) {

                    for (CommonPersonObject personinlist : p) {
                        Log.v("is filtermap ", "wrong place");
                        if (!isnull(personinlist)) {
                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
//                    pClient.entityID = personinlist.getCaseId();
                            pClient.setColumnmaps(personinlist.getColumnmaps());
                            pClients.add(pClient);
                        }

                    }

                }else{
                    switch (byColumnAndByDetails){
                        case byColumn:
                            for (CommonPersonObject personinlist : p) {
                                if(!isnull(personinlist)) {
                                    if (personinlist.getColumnmaps().get(filterkey) != null) {
                                        if (personinlist.getColumnmaps().get(filterkey).equalsIgnoreCase(filtervalue)==filtercase) {
                                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                            pClient.setColumnmaps(personinlist.getColumnmaps());

                                            pClients.add(pClient);
                                        }
                                    }
                                }
                            }
                            break;
                        case byDetails:
                            for (CommonPersonObject personinlist : p) {
                                if(!isnull(personinlist)) {
                                    if (personinlist.getDetails().get(filterkey) != null) {
                                        if (personinlist.getDetails().get(filterkey).equalsIgnoreCase(filtervalue)==filtercase) {
                                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                            pClient.setColumnmaps(personinlist.getColumnmaps());

                                            pClients.add(pClient);
                                        }
                                    }
                                }
                            }
                            break;
                        case byrelationalid:
                            for (CommonPersonObject personinlist : p) {
                                if(!isnull(personinlist)) {
                                    if (personinlist.getRelationalId().equalsIgnoreCase(filtervalue)==filtercase) {
                                        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                        pClient.setColumnmaps(personinlist.getColumnmaps());
                                        pClients.add(pClient);

                                    }
                                }
                            }
                            break;
                        case byrelational_id:
                            for (CommonPersonObject personinlist : p) {
                                if(!isnull(personinlist)) {
                                    if (personinlist.getColumnmaps().get("relational_id").equalsIgnoreCase(filtervalue)==filtercase) {
                                        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                        pClient.setColumnmaps(personinlist.getColumnmaps());
                                        pClients.add(pClient);

                                    }
                                }
                            }
                            break;
                    }
                }
                if(sortOption == null) {
                    sortByName(pClients);
                }else{
                    sortOption.sort(pClients);
                }
                return new Gson().toJson(pClients);
            }
        });
    }

    //#TODO: Remove duplication
    public CommonPersonObjectClients getClients() {
        return personObjectClientsCache.get(person_CLIENTS_LIST, new CacheableData<CommonPersonObjectClients>() {


            @Override
            public CommonPersonObjectClients fetch() {
                List<CommonPersonObject> p = allpersonobjects.all();
                updateDetails(p);
                CommonPersonObjectClients pClients = new CommonPersonObjectClients();
                if(filtermap != null){
                    for (CommonPersonObject personinlist : p) {
                        boolean filter = false;
                            Log.v("is filtermap ", "" + filtermap.size());
                            for(int k = 0;k<filtermap.size();k++) {
                                filter = filtermap.get(k).filtermapLogic(personinlist);
                            }

                            if (!isnull(personinlist) && filter) {
                                CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
//                    pClient.entityID = personinlist.getCaseId();
                                pClient.setColumnmaps(personinlist.getColumnmaps());
                                pClients.add(pClient);

                            }
                        }
                }
                else  if(filterkey == null){
                    for (CommonPersonObject personinlist : p) {

                            if (!isnull(personinlist)) {
                                CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
//                    pClient.entityID = personinlist.getCaseId();
                                pClient.setColumnmaps(personinlist.getColumnmaps());
                                pClients.add(pClient);
                            }
                        }

                }else{
                    switch (byColumnAndByDetails){
                        case byColumn:
                            for (CommonPersonObject personinlist : p) {
                                if(!isnull(personinlist)) {
                                    if (personinlist.getColumnmaps().get(filterkey) != null) {
                                        if (personinlist.getColumnmaps().get(filterkey).equalsIgnoreCase(filtervalue) == filtercase) {
                                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                            pClient.setColumnmaps(personinlist.getColumnmaps());

                                            pClients.add(pClient);
                                        }
                                    }
                                }
                            }
                            break;
                        case byDetails:
                            for (CommonPersonObject personinlist : p) {
                                if(!isnull(personinlist)) {
                                    if (personinlist.getDetails().get(filterkey) != null) {
                                        if (personinlist.getDetails().get(filterkey).equalsIgnoreCase(filtervalue) == filtercase) {
                                            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                            pClient.setColumnmaps(personinlist.getColumnmaps());

                                            pClients.add(pClient);
                                        }
                                    }
                                }
                            }
                            break;
                        case byrelationalid:
                            for (CommonPersonObject personinlist : p) {
                                if (!isnull(personinlist)) {
                                    if (personinlist.getRelationalId().equalsIgnoreCase(filtervalue) ==filtercase) {
                                        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString));
                                        pClient.setColumnmaps(personinlist.getColumnmaps());
                                        pClients.add(pClient);

                                    }
                                }
                            }
                            break;
                        case byrelational_id:
                            for (CommonPersonObject personinlist : p) {
                                if (!isnull(personinlist)) {
                                    if (personinlist.getColumnmaps().get("relational_id").equalsIgnoreCase(filtervalue) ==filtercase) {
                                        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get(nameString) );
                                        pClient.setColumnmaps(personinlist.getColumnmaps());
                                        pClients.add(pClient);

                                    }
                                }
                            }
                            break;
                    }
                }
                if(sortOption == null) {
                    sortByName(pClients);
                }else{
                    sortOption.sort(pClients);
                }
                return pClients;
            }
        });
    }


    private void sortByName(List<? extends SmartRegisterClient> personClients) {
        sort(personClients, new Comparator<SmartRegisterClient>() {


            @Override
            public int compare(SmartRegisterClient personClient, SmartRegisterClient personClient2) {

                return ((CommonPersonObjectClient)personClient).getName().trim().compareToIgnoreCase(((CommonPersonObjectClient)personClient2).getName().trim());
            }
        });
    }

    //#TODO: Needs refactoring
    public boolean isnull(CommonPersonObject personinlist){
        boolean isnull = false;
        switch (byColumnAndByDetailsNullcheck){
            case byColumn:
                if(personinlist.getColumnmaps().get(null_check_key) == null || personinlist.getColumnmaps().get(null_check_key).equalsIgnoreCase("")){
                    isnull = true;
                }
                break;
            case byDetails:
                if(personinlist.getDetails().get(null_check_key) == null || personinlist.getDetails().get(null_check_key).equalsIgnoreCase("")){
                    isnull = true;
                }
                break;
        }
        return isnull;
    }


    private void updateDetails(List<CommonPersonObject> p){
        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        for(CommonPersonObject pc: p) {
            detailsRepository.updateDetails(pc);
        }

    }

}


