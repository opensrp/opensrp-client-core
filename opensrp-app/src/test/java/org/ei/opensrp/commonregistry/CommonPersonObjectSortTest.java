package org.ei.opensrp.commonregistry;

import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.opensrp.util.EasyMap.create;
import static org.mockito.MockitoAnnotations.initMocks;

/*
 by Raihan Ahmed
 */
@RunWith(RobolectricTestRunner.class)
public class CommonPersonObjectSortTest {
    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private CommonPersonObjectController controller;
    private Map<String, String> emptyDetails;

    private CommonObjectSort commonObjectSort;
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        emptyDetails = Collections.emptyMap();
        controller = new CommonPersonObjectController(allCommonsRepository, allBeneficiaries, new Cache<String>(), new Cache<CommonPersonObjectClients>(),"name","bindtype","name", CommonPersonObjectController.ByColumnAndByDetails.byDetails);
    }

    @Test
    public void shouldSortCommonObjectsByColumnName() throws Exception {

        commonObjectSort = new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byColumn,false,"name","name");

        Map<String, String> column1 = create("name", "Woman A").map();
        Map<String, String> column2 = create("name","Woman B").map();
        Map<String, String> column3= create("name","Woman C").map();




        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",emptyDetails,"Woman A");
        expectedClient1.setColumnmaps(column1);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",emptyDetails,"Woman B");
        expectedClient2.setColumnmaps(column2);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",emptyDetails,"Woman C");
        expectedClient3.setColumnmaps(column3);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        clients.add(expectedClient1);


//        Gson gson = new Gson();
//        String objectlist = gson.toJson(asList(expectedClient1, expectedClient2, expectedClient3));

//        List<CommonPersonObjectClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<CommonPersonObjectClient>>() {
//        }.getType());
        assertEquals(asList(expectedClient1, expectedClient2, expectedClient3), commonObjectSort.sort(clients));
//        assertEquals(objectlist,clients);
    }

    @Test
    public void shouldSortCommonObjectsByDetailName() throws Exception {

        commonObjectSort = new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,false,"name","name");

        Map<String, String> detail1 = create("name", "Woman A").map();
        Map<String, String> detail2 = create("name","Woman B").map();
        Map<String, String> detail3= create("name","Woman C").map();




        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",detail1,"Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",detail2,"Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",detail3,"Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        clients.add(expectedClient1);

        assertEquals(asList(expectedClient1, expectedClient2, expectedClient3), commonObjectSort.sort(clients));

    }
    @Test
    public void shouldSortCommonObjectsByInteger() throws Exception {

        commonObjectSort = new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"HID","HID");

        Map<String, String> detail1 = create("HID", "1").map();
        Map<String, String> detail2 = create("HID","2").map();
        Map<String, String> detail3= create("HID","3").map();




        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",detail1,"Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",detail2,"Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",detail3,"Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        clients.add(expectedClient1);


        assertEquals(asList(expectedClient1, expectedClient2, expectedClient3), commonObjectSort.sort(clients));

    }









}
