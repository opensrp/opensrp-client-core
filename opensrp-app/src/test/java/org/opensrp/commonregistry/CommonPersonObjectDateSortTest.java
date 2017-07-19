package org.opensrp.commonregistry;

import org.opensrp.repository.AllBeneficiaries;
import org.opensrp.util.Cache;
import org.opensrp.view.contract.SmartRegisterClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.opensrp.util.EasyMap.create;
import static org.mockito.MockitoAnnotations.initMocks;

/*
 by Raihan Ahmed
 */
@RunWith(RobolectricTestRunner.class)
public class CommonPersonObjectDateSortTest {
    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private CommonPersonObjectController controller;
    private Map<String, String> emptyDetails;

    private CommonObjectDateSort commonObjectdateSort;
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        emptyDetails = Collections.emptyMap();
  }

    @Test
    public void shouldSortCommonObjectsByColumnName() throws Exception {
        controller = new CommonPersonObjectController(allCommonsRepository, allBeneficiaries, new Cache<String>(), new Cache<CommonPersonObjectClients>(),"name","bindtype","nullkey", CommonPersonObjectController.ByColumnAndByDetails.byDetails);

        commonObjectdateSort = new CommonObjectDateSort(CommonObjectDateSort.ByColumnAndByDetails.byColumn,"date");

         Map<String, String> column1 = create("date", "2015-06-06").map();
        Map<String, String> column2 = create("date","2015-07-07").map();
        Map<String, String> column3= create("date","2015-01-01").map();

        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",emptyDetails,"Woman A");
        expectedClient1.setColumnmaps(column1);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",emptyDetails,"Woman B");
        expectedClient2.setColumnmaps(column2);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",emptyDetails,"Woman C");
        expectedClient3.setColumnmaps(column3);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient1);
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        assertEquals(asList(expectedClient3,expectedClient1, expectedClient2), commonObjectdateSort.sort(clients));

    }

    @Test
    public void shouldSortCommonObjectsByDetailName() throws Exception {
        controller = new CommonPersonObjectController(allCommonsRepository, allBeneficiaries, new Cache<String>(), new Cache<CommonPersonObjectClients>(),"name","bindtype","nullkey", CommonPersonObjectController.ByColumnAndByDetails.byColumn);

        commonObjectdateSort = new CommonObjectDateSort(CommonObjectDateSort.ByColumnAndByDetails.byDetails,"date");

        Map<String, String> detail1 = create("date", "2015-06-06").map();
        Map<String, String> detail2 = create("date","2015-07-07").map();
        Map<String, String> detail3= create("date","2015-01-01").map();

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
        assertEquals(asList(expectedClient3,expectedClient1, expectedClient2), commonObjectdateSort.sort(clients));

    }
}
