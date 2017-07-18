package org.ei.opensrp.view.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.opensrp.domain.*;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.AllEligibleCouples;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.ServiceProvidedService;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.EasyMap;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.ChildClient;
import org.ei.opensrp.view.contract.ServiceProvidedDTO;
import org.ei.opensrp.view.contract.Visits;
import org.ei.opensrp.view.contract.pnc.PNCClient;
import org.ei.opensrp.view.contract.pnc.PNCClients;
import org.ei.opensrp.view.preProcessor.PNCClientPreProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static junit.framework.Assert.assertEquals;
import static org.ei.opensrp.domain.AlertStatus.normal;
import static org.ei.opensrp.util.EasyMap.create;
import static org.ei.opensrp.util.EasyMap.mapOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class PNCSmartRegisterControllerTest {
    public static final String[] PNC_ALERTS = new String[]{
            "PNC 1"
    };
    public static final String[] PNC_SERVICES = new String[]{
            "PNC"
    };
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AlertService alertService;
    @Mock
    private ServiceProvidedService serviceProvidedService;
    @Mock
    private PNCClientPreProcessor preProcessor;

    private PNCSmartRegisterController controller;
    private Map<String, String> emptyMap;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        emptyMap = Collections.emptyMap();
        controller = new PNCSmartRegisterController(serviceProvidedService, alertService, allEligibleCouples, allBeneficiaries, new Cache<String>(), new Cache<PNCClients>(), preProcessor);
    }

    @Test
    public void shouldSortPNCsByWifeName() throws Exception {
        Map<String, String> details = mapOf("edd", "Tue, 25 Feb 2014 00:00:00 GMT");
        EligibleCouple ec2 = new EligibleCouple("EC Case 2", "Woman B", "Husband B", "EC Number 2", "kavalu_hosur", "Bherya SC", emptyMap);
        EligibleCouple ec3 = new EligibleCouple("EC Case 3", "Woman C", "Husband C", "EC Number 3", "Bherya", "Bherya SC", emptyMap);
        EligibleCouple ec1 = new EligibleCouple("EC Case 1", "Woman A", "Husband A", "EC Number 1", "Bherya", null, emptyMap);
        Mother m1 = new Mother("Entity X", "EC Case 2", "thayi 1", "2013-05-25").withDetails(details);
        Mother m2 = new Mother("Entity Y", "EC Case 3", "thayi 2", "2013-05-25").withDetails(details);
        Mother m3 = new Mother("Entity Z", "EC Case 1", "thayi 3", "2013-05-25").withDetails(details);
        PNCClient expectedClient1 = createPNCClient("Entity Z", "Woman A", "Bherya", "thayi 3", "2013-05-25").withECNumber("EC Number 1").withHusbandName("Husband A").withChildren(EMPTY_LIST).withEntityIdToSavePhoto("EC Case 1");
        PNCClient expectedClient2 = createPNCClient("Entity X", "Woman B", "kavalu_hosur", "thayi 1", "2013-05-25").withECNumber("EC Number 2").withHusbandName("Husband B").withChildren(EMPTY_LIST).withEntityIdToSavePhoto("EC Case 2");
        PNCClient expectedClient3 = createPNCClient("Entity Y", "Woman C", "Bherya", "thayi 2", "2013-05-25").withECNumber("EC Number 3").withHusbandName("Husband C").withChildren(EMPTY_LIST).withEntityIdToSavePhoto("EC Case 3");
        when(allBeneficiaries.allPNCsWithEC()).thenReturn(asList(Pair.of(m1, ec2), Pair.of(m2, ec3), Pair.of(m3, ec1)));
        when(preProcessor.preProcess(any(PNCClient.class))).thenReturn(expectedClient1, expectedClient2, expectedClient3);

        PNCClients clients = controller.getClients();

        assertEquals(expectedClient1.wifeName(), clients.get(0).wifeName());
        assertEquals(expectedClient2.wifeName(), clients.get(1).wifeName());
        assertEquals(expectedClient3.wifeName(), clients.get(2).wifeName());
    }

    @Test
    public void shouldMapPNCToPNCClient() throws Exception {
        Map<String, String> ecDetails = create("wifeAge", "22")
                .put("currentMethod", "condom")
                .put("familyPlanningMethodChangeDate", "2013-01-02")
                .put("isHighPriority", Boolean.toString(false))
                .put("deliveryDate", "2011-05-05")
                .put("womanDOB", "2011-05-05")
                .put("caste", "sc")
                .put("economicStatus", "bpl")
                .put("iudPlace", "iudPlace")
                .put("iudPerson", "iudPerson")
                .put("numberOfCondomsSupplied", "20")
                .put("numberOfCentchromanPillsDelivered", "10")
                .put("numberOfOCPDelivered", "5")
                .map();
        Map<String, String> motherDetails = create("deliveryPlace", "PHC")
                .put("deliveryType", "live_birth")
                .put("deliveryComplications", "Headache")
                .put("otherDeliveryComplications", "Vomiting")
                .map();
        EligibleCouple eligibleCouple = new EligibleCouple("entity id 1", "Woman A", "Husband A", "EC Number 1", "Bherya", null, ecDetails).asOutOfArea();
        Mother mother = new Mother("Entity X", "entity id 1", "thayi 1", "2013-05-25").withDetails(motherDetails);
        when(allBeneficiaries.allPNCsWithEC()).thenReturn(asList(Pair.of(mother, eligibleCouple)));
        when(allBeneficiaries.findAllChildrenByMotherId("Entity X")).thenReturn(asList(new Child("child id 1", "Entity X", "male", mapOf("weight", "2.4")),
                new Child("child id 2", "Entity X", "female", mapOf("weight", "2.5"))));
        PNCClient expectedPNCClient = new PNCClient("Entity X", "Bherya", "Woman A", "thayi 1", "2013-05-25")
                .withECNumber("EC Number 1")
                .withIsHighPriority(false)
                .withAge("22")
                .withWomanDOB("2011-05-05")
                .withEconomicStatus("bpl")
                .withIUDPerson("iudPerson")
                .withIUDPlace("iudPlace")
                .withHusbandName("Husband A")
                .withIsOutOfArea(true)
                .withIsHighRisk(false)
                .withCaste("sc")
                .withFPMethod("condom")
                .withFamilyPlanningMethodChangeDate("2013-01-02")
                .withNumberOfCondomsSupplied("20")
                .withNumberOfCentchromanPillsDelivered("10")
                .withNumberOfOCPDelivered("5")
                .withDeliveryPlace("PHC")
                .withDeliveryType("live_birth")
                .withDeliveryComplications("Headache")
                .withOtherDeliveryComplications("Vomiting")
                .withPhotoPath("../../img/woman-placeholder.png")
                .withEntityIdToSavePhoto("entity id 1")
                .withAlerts(Collections.<AlertDTO>emptyList())
                .withChildren(asList(new ChildClient("child id 1", "male", "2.4", "thayi 1"), new ChildClient("child id 2", "female", "2.5", "thayi 1")))
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList());
        when(preProcessor.preProcess(any(PNCClient.class))).thenReturn(expectedPNCClient);

        PNCClients clients = controller.getClients();

        assertEquals(expectedPNCClient, clients.get(0));
    }

    @Test
    public void shouldCreatePNCClientsWithPNC1Alert() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 1", "Bherya", "Bherya SC", emptyMap);
        Mother mother = new Mother("Entity X", "entity id 1", "thayi 1", "2013-05-25");
        Alert pnc1Alert = new Alert("entity id 1", "PNC", "PNC 1", normal, "2013-01-01", "2013-02-01");
        when(allBeneficiaries.allPNCsWithEC()).thenReturn(asList(Pair.of(mother, ec)));
        when(alertService.findByEntityIdAndAlertNames("Entity X", PNC_ALERTS)).thenReturn(asList(pnc1Alert));
        AlertDTO expectedAlertDto = new AlertDTO("PNC 1", "normal", "2013-01-01");
        PNCClient expectedEC = createPNCClient("Entity X", "Woman C", "Bherya", "thayi 1", "2013-05-25")
                .withECNumber("EC Number 1")
                .withHusbandName("Husband C")
                .withEntityIdToSavePhoto("entity id 1")
                .withAlerts(asList(expectedAlertDto))
                .withChildren(EMPTY_LIST)
                .withServiceToVisitMap(new HashMap<String, Visits>());
        when(preProcessor.preProcess(any(PNCClient.class))).thenReturn(expectedEC);
        PNCClients clients = controller.getClients();

        verify(alertService).findByEntityIdAndAlertNames("Entity X", PNC_ALERTS);


        assertEquals(asList(expectedEC), clients);
    }

    @Test
    public void shouldCreatePNCClientsWithServicesProvided() throws Exception {
        EligibleCouple ec = new EligibleCouple("entity id 1", "Woman C", "Husband C", "EC Number 1", "Bherya", "Bherya SC", emptyMap);
        Mother mother = new Mother("Entity X", "entity id 1", "thayi 1", "2013-05-25");
        when(allBeneficiaries.allPNCsWithEC()).thenReturn(asList(Pair.of(mother, ec)));
        when(alertService.findByEntityIdAndAlertNames("Entity X", PNC_ALERTS)).thenReturn(Collections.<Alert>emptyList());
        when(serviceProvidedService.findByEntityIdAndServiceNames("Entity X", PNC_SERVICES))
                .thenReturn(asList(new ServiceProvided("entity id 1", "PNC 1", "2013-01-01", mapOf("dose", "100")), new ServiceProvided("entity id 1", "PNC 1", "2013-02-01", emptyMap)));
        List<ServiceProvidedDTO> expectedServicesProvided = asList(new ServiceProvidedDTO("PNC 1", "2013-01-01", mapOf("dose", "100")),
                new ServiceProvidedDTO("PNC 1", "2013-02-01", emptyMap));
        PNCClient expectedEC = createPNCClient("Entity X", "Woman C", "Bherya", "thayi 1", "2013-05-25")
                .withECNumber("EC Number 1")
                .withHusbandName("Husband C")
                .withEntityIdToSavePhoto("entity id 1")
                .withServicesProvided(expectedServicesProvided)
                .withChildren(EMPTY_LIST);
        when(preProcessor.preProcess(any(PNCClient.class))).thenReturn(expectedEC);

        PNCClients clients = controller.getClients();

        verify(alertService).findByEntityIdAndAlertNames("Entity X", PNC_ALERTS);
        verify(serviceProvidedService).findByEntityIdAndServiceNames("Entity X", PNC_SERVICES);
        assertEquals(asList(expectedEC), clients);
    }

    private PNCClient createPNCClient(String entityId, String name, String village, String thayi, String deliveryDate) {
        Visits visits = new Visits();
        Map<String, Visits> serviceToVisitsMap = EasyMap.create("pnc", visits).map();

        return new PNCClient(entityId, village, name, thayi, deliveryDate)
                .withPhotoPath("../../img/woman-placeholder.png")
                .withIsOutOfArea(false)
                .withAlerts(Collections.<AlertDTO>emptyList())
                .withServicesProvided(Collections.<ServiceProvidedDTO>emptyList());

    }

}
