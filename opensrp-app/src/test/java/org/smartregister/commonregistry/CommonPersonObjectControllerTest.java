package org.smartregister.commonregistry;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.util.Cache;

import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.util.EasyMap.create;

/*
 by Raihan Ahmed
 */
@RunWith(RobolectricTestRunner.class)
public class CommonPersonObjectControllerTest {
    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    @Mock
    private org.smartregister.Context context;

    private CommonPersonObjectController controller;
    private Map<String, String> emptyDetails;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        CoreLibrary.init(context);
        emptyDetails = Collections.emptyMap();
        controller = new CommonPersonObjectController(allCommonsRepository, allBeneficiaries, new Cache<String>(), new Cache<CommonPersonObjectClients>(),"name","bindtype","name", CommonPersonObjectController.ByColumnAndByDetails.byDetails);
    }

    @Test
    public void shouldSortECsByName() throws Exception {
         Map<String, String> personDetails1 = create("name", "Woman A").map();
        Map<String, String> personDetails2 = create("name","Woman B").map();
        Map<String, String> personDetails3 = create("name","Woman C").map();

        CommonPersonObject cpo2 = new CommonPersonObject("entity id 2","relational id 2",personDetails2,"bindtype");
        cpo2.setColumnmaps(emptyDetails);
        CommonPersonObject cpo3 = new CommonPersonObject("entity id 3","relational id 3",personDetails3,"bindtype");
        cpo3.setColumnmaps(emptyDetails);
        CommonPersonObject cpo1 = new CommonPersonObject("entity id 1","relational id 1",personDetails1,"bindtype");
        cpo1.setColumnmaps(emptyDetails);

        when(allCommonsRepository.all()).thenReturn(asList(cpo2, cpo3, cpo1));
        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",personDetails1,"Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",personDetails2,"Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",personDetails3,"Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        String clients = controller.get();

        Gson gson = new Gson();
        String objectlist = gson.toJson(asList(expectedClient1, expectedClient2, expectedClient3));
        assertEquals(objectlist,clients);
    }


    @Test
    public void shouldMapCommonObjectToCommonObjectClient() throws Exception {
        Map<String, String> details = create("name", "Woman A").map();

        CommonPersonObject commonpersonobject = new CommonPersonObject("entity id 1","relational id 1",details,"bindtype");
        commonpersonobject.setColumnmaps(emptyDetails);
        when(allCommonsRepository.all()).thenReturn(asList(commonpersonobject));
        CommonPersonObjectClient expectedCommonObjectClient = new CommonPersonObjectClient("entity id 1",details,"Woman A" );
        expectedCommonObjectClient.setColumnmaps(emptyDetails);
        String clients = controller.get();
        Gson gson = new Gson();
        String objectlist = gson.toJson(asList(expectedCommonObjectClient));
        assertEquals(objectlist, clients);
    }

    @Test
    public void shouldfilterNullsAccordingToFilterKey() throws Exception {
        Map<String, String> personDetails1 = create("name", "Woman A").map();
        Map<String, String> personDetails2 = create("name","Woman B").map();
        Map<String, String> personDetails3 = emptyDetails;




        CommonPersonObject cpo2 = new CommonPersonObject("entity id 2","relational id 2",personDetails2,"bindtype");
        cpo2.setColumnmaps(emptyDetails);
        CommonPersonObject cpo3 = new CommonPersonObject("entity id 3","relational id 3",personDetails3,"bindtype");
        cpo3.setColumnmaps(emptyDetails);
        CommonPersonObject cpo1 = new CommonPersonObject("entity id 1","relational id 1",personDetails1,"bindtype");
        cpo1.setColumnmaps(emptyDetails);

        when(allCommonsRepository.all()).thenReturn(asList(cpo2, cpo3, cpo1));
        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",personDetails1,"Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",personDetails2,"Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",personDetails3,"Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        String clients = controller.get();

        Gson gson = new Gson();
        String objectlist = gson.toJson(asList(expectedClient1, expectedClient2));
        assertEquals(objectlist,clients);
    }






}
