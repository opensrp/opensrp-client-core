package org.smartregister.view.contract;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.dialog.AllClientsFilter;
import org.smartregister.view.dialog.AllEligibleCoupleServiceMode;
import org.smartregister.view.dialog.BPLSort;
import org.smartregister.view.dialog.ECNumberSort;
import org.smartregister.view.dialog.ECSearchOption;
import org.smartregister.view.dialog.FPAllMethodsServiceMode;
import org.smartregister.view.dialog.FPMethodFilter;
import org.smartregister.view.dialog.HighPrioritySort;
import org.smartregister.view.dialog.NameSort;
import org.smartregister.view.dialog.OutOfAreaFilter;
import org.smartregister.view.dialog.SCSort;
import org.smartregister.view.dialog.STSort;
import org.smartregister.view.dialog.VillageFilter;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class SmartRegisterClientsTest {

    @Mock
    AllEligibleCoupleServiceMode allEligibleCoupleServiceMode;

    @Mock
    FPAllMethodsServiceMode fpAllMethodsServiceMode;

    @Mock
    Context context;

    @Mock
    private android.content.Context applicationContext;

    private Context currentContext;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(context.applicationContext()).thenReturn(applicationContext);
        currentContext = CoreLibrary.getInstance().context();
        Context.setInstance(context);
    }

    @After
    public void tearDown() throws Exception {
        Context.setInstance(currentContext);
    }

    @Test
    public void emptyStringSearchShouldReturnAllTheResults() {

        SmartRegisterClients originalClients = getUniformSmartRegisterClients(10);
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());

        assertEquals(originalClients, filteredClients);
    }

    @Test
    public void ShouldReturnFilteredResultsForSearchString() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();

        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption("a"),
                new NameSort());

        assertEquals(3, filteredClients.size());
        for (int i = 0; i < 3; i++) {
            assertTrue(filteredClients.get(i).name().startsWith("A"));
        }
    }

    @Test
    public void ShouldReturnSortedResultsForNameSortOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();

        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());

        ECClients expectedClients = new ECClients();
        expectedClients.addAll(
                asList(
                        new ECClient("abcd1", "Adhiti", "Rama", "Battiganahalli", 69).withIsHighPriority(true).withIsOutOfArea(true),
                        new ECClient("abcd2", "Akshara", "Rajesh", "Half bherya", 500).withCaste("SC").withEconomicStatus("BPL").withIsHighPriority(true).withFPMethod("condom"),
                        new ECClient("abcd3", "Anitha", "Chandan", "Half bherya", 87).withCaste("SC").withIsOutOfArea(true).withFPMethod("ocp").withFPMethod("ocp"),
                        new ECClient("abcd6", "Bhagya", "Ramesh", "Hosa agrahara", 93).withIsHighPriority(true).withCaste("ST").withFPMethod("condom"),
                        new ECClient("abcd4", "Bhavani", "Ravi", "Gowrikoppalu", 140).withEconomicStatus("BPL").withFPMethod("female_sterilization"),
                        new ECClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", 36).withCaste("ST").withFPMethod("ocp")
                ));
        assertEquals(expectedClients, filteredClients);
    }

    @Test
    public void shouldReturnSortedResultsForECNumberSortOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();

        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new ECNumberSort());

        assertEquals(6, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(1).name());
        assertEquals("Akshara", filteredClients.get(5).name());
        assertEquals("Anitha", filteredClients.get(2).name());
        assertEquals("Bhagya", filteredClients.get(3).name());
        assertEquals("Bhavani", filteredClients.get(4).name());
        assertEquals("Chaitra", filteredClients.get(0).name());
    }

    @Test
    public void shouldReturnSortedResultsForBPLSortOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new BPLSort());
        assertEquals(6, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(2).name());
        assertEquals("Akshara", filteredClients.get(0).name());
        assertEquals("Anitha", filteredClients.get(3).name());
        assertEquals("Bhagya", filteredClients.get(4).name());
        assertEquals("Bhavani", filteredClients.get(1).name());
        assertEquals("Chaitra", filteredClients.get(5).name());
    }

    @Test
    public void shouldReturnSortedResultsForHPSortOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new HighPrioritySort());
        assertEquals(6, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(0).name());
        assertEquals("Akshara", filteredClients.get(1).name());
        assertEquals("Bhagya", filteredClients.get(2).name());
        assertEquals("Chaitra", filteredClients.get(5).name());
        assertEquals("Bhavani", filteredClients.get(4).name());
        assertEquals("Anitha", filteredClients.get(3).name());
    }

    @Test
    public void shouldReturnSortedResultsForSCSortOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new SCSort());
        assertEquals(6, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(2).name());
        assertEquals("Akshara", filteredClients.get(0).name());
        assertEquals("Anitha", filteredClients.get(1).name());
        assertEquals("Bhagya", filteredClients.get(3).name());
        assertEquals("Bhavani", filteredClients.get(4).name());
        assertEquals("Chaitra", filteredClients.get(5).name());
    }

    @Test
    public void shouldReturnSortedResultsForSTSortOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new AllClientsFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new STSort());
        assertEquals(6, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(2).name());
        assertEquals("Akshara", filteredClients.get(3).name());
        assertEquals("Anitha", filteredClients.get(4).name());
        assertEquals("Bhagya", filteredClients.get(0).name());
        assertEquals("Bhavani", filteredClients.get(5).name());
        assertEquals("Chaitra", filteredClients.get(1).name());
    }

    @Test
    public void shouldReturn2ResultsFor_Half_Bherya_VillageFilterOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new VillageFilter("Half bherya"),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());
        assertEquals(2, filteredClients.size());
        assertEquals("Akshara", filteredClients.get(0).name());
        assertEquals("Anitha", filteredClients.get(1).name());
    }

    @Test
    public void shouldReturn2ResultsFor_Hosa_agrahara_VillageFilterOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new VillageFilter("Hosa agrahara"),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());
        assertEquals(1, filteredClients.size());
        assertEquals("Bhagya", filteredClients.get(0).name());
    }

    @Test
    public void shouldReturn1ResultsFor_Battiganahalli_VillageFilterOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new VillageFilter("Battiganahalli"),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());
        assertEquals(1, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(0).name());
    }

    @Test
    public void shouldReturn1ResultsFor_Somanahalli_colony_VillageFilterOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new VillageFilter("Somanahalli colony"),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());
        assertEquals(1, filteredClients.size());
        assertEquals("Chaitra", filteredClients.get(0).name());
    }

    @Test
    public void shouldReturn1ResultsForOutOfAreaFilterOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new OutOfAreaFilter(),
                allEligibleCoupleServiceMode,
                new ECSearchOption(""),
                new NameSort());
        assertEquals(2, filteredClients.size());
        assertEquals("Adhiti", filteredClients.get(0).name());
        assertEquals("Anitha", filteredClients.get(1).name());
    }

    @Test
    public void shouldReturnCascadedResultsApplyingMultipleFilterOption() {
        SmartRegisterClients originalClients = getSmartRegisterClientsWithProperDetails();

        SmartRegisterClients filteredClients = originalClients.applyFilter(
                new VillageFilter("Hosa agrahara"),
                allEligibleCoupleServiceMode,
                new ECSearchOption("bh"),
                new NameSort());

        assertEquals(1, filteredClients.size());
        assertEquals("Bhagya", filteredClients.get(0).name());
    }

    @Test
    public void shouldReturnFilteredListForFP() {
        when(applicationContext.getString(R.string.fp_register_service_mode_condom)).thenReturn("Condom");

        SmartRegisterClients originalClients = getFPSmartRegisterClientsWithProperDetails();
        SmartRegisterClients filteredClients = originalClients.applyFilterWithFP(fpAllMethodsServiceMode, new NameSort(), new FPMethodFilter("condom"));

        assertEquals(2, filteredClients.size());
        assertEquals("Akshara", filteredClients.get(0).name());
        assertEquals("Bhagya", filteredClients.get(1).name());
    }


    public SmartRegisterClients getUniformSmartRegisterClients(int clientCount) {
        SmartRegisterClients clients = new SmartRegisterClients();
        for (int i = 0; i < clientCount; i++) {
            clients.add(new ECClient("CASE " + i, "Wife" + i, "Husband" + i, "Village" + i, 100 + i));
        }
        return clients;
    }

    public ECClients getSmartRegisterClientsWithProperDetails() {
        ECClients clients = new ECClients();
        clients.add(new ECClient("abcd2", "Akshara", "Rajesh", "Half bherya", 500).withCaste("SC").withEconomicStatus("BPL").withIsHighPriority(true).withFPMethod("condom"));
        clients.add(new ECClient("abcd1", "Adhiti", "Rama", "Battiganahalli", 69).withIsHighPriority(true).withIsOutOfArea(true));
        clients.add(new ECClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", 36).withCaste("ST").withFPMethod("ocp"));
        clients.add(new ECClient("abcd4", "Bhavani", "Ravi", "Gowrikoppalu", 140).withEconomicStatus("BPL").withFPMethod("female_sterilization"));
        clients.add(new ECClient("abcd6", "Bhagya", "Ramesh", "Hosa agrahara", 93).withIsHighPriority(true).withCaste("ST").withFPMethod("condom"));
        clients.add(new ECClient("abcd3", "Anitha", "Chandan", "Half bherya", 87).withCaste("SC").withIsOutOfArea(true).withFPMethod("ocp"));
        return clients;
    }

    public FPClients getFPSmartRegisterClientsWithProperDetails() {
        FPClients clients = new FPClients();
        clients.add(new FPClient("abcd2", "Akshara", "Rajesh", "Half bherya", "500").withCaste("SC").withEconomicStatus("BPL").withIsHighPriority(true).withFPMethod("condom"));
        clients.add(new FPClient("abcd1", "Adhiti", "Rama", "Battiganahalli", "69").withIsHighPriority(true));
        clients.add(new FPClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", "36").withCaste("ST").withFPMethod("ocp"));
        clients.add(new FPClient("abcd4", "Bhavani", "Ravi", "Gowrikoppalu", "140").withEconomicStatus("BPL").withFPMethod("female_sterilization"));
        clients.add(new FPClient("abcd6", "Bhagya", "Ramesh", "Hosa agrahara", "93").withIsHighPriority(true).withCaste("ST").withFPMethod("condom"));
        clients.add(new FPClient("abcd3", "Anitha", "Chandan", "Half bherya", "87").withCaste("SC").withFPMethod("ocp"));
        return clients;
    }
}
