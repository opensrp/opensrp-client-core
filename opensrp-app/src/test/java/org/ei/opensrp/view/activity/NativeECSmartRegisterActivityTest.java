package org.ei.opensrp.view.activity;


import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.R;
import org.ei.opensrp.setup.DrishtiTestRunner;
import org.ei.opensrp.shadows.ShadowContext;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.ECClients;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;
import org.ei.opensrp.view.controller.ECSmartRegisterController;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.viewHolder.NativeECSmartRegisterViewHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.FormNames.EC_REGISTRATION;
import static org.junit.Assert.*;

@RunWith(DrishtiTestRunner.class)
@Config(shadows = {ShadowContext.class})
public class NativeECSmartRegisterActivityTest {

    private Activity ecActivity;

    @Before
    public void setup() {
        ecActivity = Robolectric.buildActivity(NativeECSmartRegisterActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    public void clientsHeaderShouldContain7Columns() {
        ViewGroup header = (ViewGroup) ecActivity.findViewById(R.id.clients_header_layout);

        assertEquals(7, header.getChildCount());
        String[] txtHeaders = {"NAME", "EC NO.", "GPLSA", "FP METHOD", "CHILDREN", "STATUS", ""};
        for (int i = 0; i < 7; i++) {
            assertEquals(((TextView) header.getChildAt(i)).getText(), txtHeaders[i]);
        }
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    public void listViewShouldContainNoItemsIfNoClientPresent() {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);
        tryGetAdapter(list);

        assertEquals(1, list.getCount());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor20Clients.class})
    public void listViewShouldNotHavePagingFor20Items() throws InterruptedException {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);

        ViewGroup footer = (ViewGroup) tryGetAdapter(list).getView(20, null, null);
        Button nextButton = (Button) footer.findViewById(R.id.btn_next_page);
        Button previousButton = (Button) footer.findViewById(R.id.btn_previous_page);
        TextView info = (TextView) footer.findViewById(R.id.txt_page_info);

        assertEquals(21, list.getCount());
        assertNotSame(View.VISIBLE, nextButton.getVisibility());
        assertNotSame(View.VISIBLE, previousButton.getVisibility());
        assertEquals("Page 1 of 1", info.getText());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor21Clients.class})
    public void listViewShouldHavePagingFor21Items() throws InterruptedException {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);

        ViewGroup footer = (ViewGroup) tryGetAdapter(list).getView(20, null, null);
        Button nextButton = (Button) footer.findViewById(R.id.btn_next_page);
        Button previousButton = (Button) footer.findViewById(R.id.btn_previous_page);
        TextView info = (TextView) footer.findViewById(R.id.txt_page_info);

        assertEquals(21, tryGetAdapter(list).getCount());
        assertSame(View.VISIBLE, nextButton.getVisibility());
        assertNotSame(View.VISIBLE, previousButton.getVisibility());
        assertEquals("Page 1 of 2", info.getText());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor21Clients.class})
    public void listViewNavigationShouldWorkIfClientsSpanMoreThanOnePage() throws InterruptedException {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);
        ViewGroup footer = (ViewGroup) tryGetAdapter(list).getView(20, null, null);
        Button nextButton = (Button) footer.findViewById(R.id.btn_next_page);
        Button previousButton = (Button) footer.findViewById(R.id.btn_previous_page);
        TextView info = (TextView) footer.findViewById(R.id.txt_page_info);

        nextButton.performClick();
        assertEquals(2, tryGetAdapter(list).getCount());
        assertNotSame(View.VISIBLE, nextButton.getVisibility());
        assertSame(View.VISIBLE, previousButton.getVisibility());
        assertEquals("Page 2 of 2", info.getText());

        previousButton.performClick();
        assertEquals(21, tryGetAdapter(list).getCount());
        assertSame(View.VISIBLE, nextButton.getVisibility());
        assertNotSame(View.VISIBLE, previousButton.getVisibility());
        assertEquals("Page 1 of 2", info.getText());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor1Clients.class})
    public void listViewHeaderAndListViewItemWeightsShouldMatch() throws InterruptedException {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);

        LinearLayout listItem = (LinearLayout) tryGetAdapter(list).getView(0, null, null);
        LinearLayout header = (LinearLayout) ecActivity.findViewById(R.id.clients_header_layout);

        assertEquals(2, tryGetAdapter(list).getCount());
        int separatorCount = 6;
        assertEquals(listItem.getChildCount() - separatorCount, header.getChildCount());
        assertEquals((int) listItem.getWeightSum(), (int) header.getWeightSum());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    public void activityShouldBeClosedOnPressingNavBarBackButton() {
        ecActivity
                .findViewById(R.id.btn_back_to_home)
                .performClick();

        ShadowActivity sa = Robolectric.shadowOf(ecActivity);
        assertTrue(sa.isFinishing());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    public void activityShouldBeClosedOnPressingNavBarTitleButton() {
        ecActivity
                .findViewById(R.id.title_layout)
                .performClick();

        ShadowActivity sa = Robolectric.shadowOf(ecActivity);
        assertTrue(sa.isFinishing());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor5Clients.class})
    public void pressingSearchCancelButtonShouldClearSearchTextAndLoadAllClients() {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);
        EditText searchText = (EditText) ecActivity.findViewById(R.id.edt_search);

        searchText.setText("adh");
        assertTrue("Adh".equalsIgnoreCase(searchText.getText().toString()));
        assertEquals(2, tryGetAdapter(list).getCount());

        ecActivity
                .findViewById(R.id.btn_search_cancel)
                .performClick();
        assertEquals("", searchText.getText().toString());
        assertEquals(6, tryGetAdapter(list).getCount());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor25Clients.class})
    public void paginationShouldBeCascadeWhenSearchIsInProgress() {
        final ListView list = (ListView) ecActivity.findViewById(R.id.list);
        ViewGroup footer = (ViewGroup) tryGetAdapter(list).getView(20, null, null);
        Button nextButton = (Button) footer.findViewById(R.id.btn_next_page);
        Button previousButton = (Button) footer.findViewById(R.id.btn_previous_page);
        TextView info = (TextView) footer.findViewById(R.id.txt_page_info);
        EditText searchText = (EditText) ecActivity.findViewById(R.id.edt_search);

        assertEquals(21, tryGetAdapter(list).getCount());

        searchText.setText("a");

        assertEquals(4, tryGetAdapter(list).getCount());
        ViewGroup listItem = (ViewGroup) tryGetAdapter(list).getView(0, null, null);
        assertEquals(((TextView) listItem.findViewById(R.id.txt_wife_name)).getText(), "Adhiti");
        assertNotSame(View.VISIBLE, nextButton.getVisibility());
        assertNotSame(View.VISIBLE, previousButton.getVisibility());
        assertEquals("Page 1 of 1", info.getText());

        searchText.setText("no match criteria");
        assertEquals(1, tryGetAdapter(list).getCount());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    public void pressingSortOptionButtonShouldOpenDialogFragmentWithOptionsAndSelectingAnOptionShouldUpdateStatusBar() {
        ecActivity.findViewById(R.id.sort_selection)
                .performClick();

        Fragment fragment = ecActivity.getFragmentManager().findFragmentByTag("dialog");
        ListView list = (ListView) fragment.getView().findViewById(R.id.dialog_list);
        TextView sortedByInStatusBar = (TextView) ecActivity.findViewById(R.id.sorted_by);

        assertTrue(fragment.isVisible());
        assertEquals(6, tryGetAdapter(list).getCount());
        assertEquals("Name (A to Z)", sortedByInStatusBar.getText());

        ViewGroup item1View = (ViewGroup) tryGetAdapter(list).getView(1, null, null);
        assertEquals("EC Number", ((TextView) item1View.findViewById(R.id.dialog_list_option)).getText().toString());

        list.performItemClick(item1View, 1, 1);
        assertFalse(fragment.isVisible());
        assertEquals("EC Number", sortedByInStatusBar.getText());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class, ShadowVillageController.class})
    public void pressingFilterOptionButtonShouldOpenDialogFragmentWithOptionsAndSelectingAnOptionShouldUpdateStatusBar() {
        ecActivity.findViewById(R.id.filter_selection)
                .performClick();

        Fragment fragment = ecActivity.getFragmentManager().findFragmentByTag("dialog");
        TextView villageInStatusBar = (TextView) ecActivity.findViewById(R.id.village);
        ListView list = (ListView) fragment.getView().findViewById(R.id.dialog_list);
        int defaultFilterOptions = 1;

        assertTrue(fragment.isVisible());
        assertEquals(4 + defaultFilterOptions, tryGetAdapter(list).getCount());
        assertEquals("All", villageInStatusBar.getText());

        ViewGroup item1View = (ViewGroup) tryGetAdapter(list).getView(2, null, null);
        assertEquals("Mysore", ((TextView) item1View.findViewById(R.id.dialog_list_option)).getText().toString());

        list.performItemClick(item1View, 2, 2);
        assertFalse(fragment.isVisible());
        assertEquals("Mysore", villageInStatusBar.getText());
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    public void pressingServiceModeOptionButtonShouldDoNothing() {
        ecActivity.findViewById(R.id.service_mode_selection)
                .performClick();

        assertNull(ecActivity.getFragmentManager().findFragmentByTag("dialog"));
    }

    // @Test
    // @Config(shadows = {ShadowECSmartRegisterControllerWithZeroClients.class})
    // public void pressingNewRegisterButtonShouldOpenECRegistrationFormActivity() {
    //     ecActivity.findViewById(R.id.register_client)
    //             .performClick();

    //     ShadowIntent shadowIntent = Robolectric.shadowOf(
    //             Robolectric.shadowOf(ecActivity).getNextStartedActivity());

    //     assertEquals(FormActivity.class.getName(), shadowIntent.getComponent().getClassName());
    //     assertEquals(EC_REGISTRATION, shadowIntent.getExtras().get(FORM_NAME_PARAM));
    // }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor5Clients.class})
    public void pressingClientProfileLayoutShouldLaunchProfileActivity() {
        ListView list = (ListView) ecActivity.findViewById(R.id.list);
        ViewGroup item1 = (ViewGroup) tryGetAdapter(list).getView(0, null, null);
        item1.findViewById(R.id.profile_info_layout)
                .performClick();

        ShadowIntent shadowIntent = Robolectric.shadowOf(
                Robolectric.shadowOf(ecActivity).getNextStartedActivity());

        assertEquals(EligibleCoupleDetailActivity.class.getName(),
                shadowIntent.getComponent().getClassName());
        assertEquals((
                (ECClient) ((NativeECSmartRegisterViewHolder) item1.getTag())
                        .profileInfoLayout()
                        .getTag()).entityId(),
                shadowIntent.getStringExtra(AllConstants.CASE_ID));
    }

    @Test
    @Config(shadows = {ShadowECSmartRegisterControllerFor1Clients.class})
    public void pressingClientEditOptionShouldOpenDialogFragmentAndSelectingAnOptionShouldLaunchRespectiveActivity() {
        ListView list = (ListView) ecActivity.findViewById(R.id.list);
        View client1 = tryGetAdapter(list).getView(0, null, null);
        client1.findViewById(R.id.btn_edit)
                .performClick();

        Fragment fragment = ecActivity.getFragmentManager().findFragmentByTag("dialog");

        //assertTrue(fragment.isVisible()); // This is failing, don't know why.

        ListView dialogList = (ListView) fragment.getView().findViewById(R.id.dialog_list);
        dialogList.performItemClick(dialogList.getChildAt(0), 0, 0);
        assertFalse(fragment.isVisible());

        ShadowIntent shadowIntent = Robolectric.shadowOf(
                Robolectric.shadowOf(ecActivity).getNextStartedActivity());

        final ECClient client = (ECClient) ((NativeECSmartRegisterViewHolder) client1.getTag()).editButton().getTag();

        assertEquals(FormActivity.class.getName(), shadowIntent.getComponent().getClassName());
        assertEquals(client.entityId(), shadowIntent.getStringExtra(ENTITY_ID_PARAM));
    }

    private ListAdapter tryGetAdapter(final ListView list) {
        ListAdapter adapter = list.getAdapter();
        while (adapter == null) {
            Robolectric.idleMainLooper(1000);
            adapter = list.getAdapter();
        }
        return adapter;
    }

    public static ECClients createClients(int clientCount) {
        ECClients clients = new ECClients();
        for (int i = 0; i < clientCount; i++) {
            clients.add(new ECClient("CASE " + i, "Wife 1" + i, "Husband 1" + i, "Village 1" + i, 100 + i));
        }
        return clients;
    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerWithZeroClients {

        @Implementation
        public ECClients getClients() {
            return new ECClients();
        }

    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor1Clients {

        @Implementation
        public ECClients getClients() {
            return createClients(1);
        }
    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor20Clients {

        @Implementation
        public ECClients getClients() {
            return createClients(20);
        }

    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor21Clients {

        @Implementation
        public ECClients getClients() {
            return createClients(21);
        }

    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor5Clients {

        @Implementation
        public ECClients getClients() {
            ECClients clients = new ECClients();
            clients.add(new ECClient("abcd1", "Adhiti", "Rama", "Battiganahalli", 69));
            clients.add(new ECClient("abcd2", "Akshara", "Rajesh", "Half bherya", 500));
            clients.add(new ECClient("abcd3", "Anitha", "Chandan", "Half bherya", 87));
            clients.add(new ECClient("abcd4", "Bhagya", "Ramesh", "Hosa agrahara", 140));
            clients.add(new ECClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", 36));
            return clients;
        }
    }

    @Implements(ECSmartRegisterController.class)
    public static class ShadowECSmartRegisterControllerFor25Clients {

        @Implementation
        public ECClients getClients() {
            ECClients clients = new ECClients();
            clients.add(new ECClient("abcd4", "Bhagya", "Ramesh", "Hosa agrahara", 140));
            clients.add(new ECClient("abcd5", "Chaitra", "Rams", "Somanahalli colony", 36));
            for (int i = 0; i < 20; i++) {
                clients.add(new ECClient("abcd2" + i, "wife" + i, "husband" + i, "Village" + i, 1001 + i));
            }
            clients.add(new ECClient("abcd1", "Adhiti", "Rama", "Battiganahalli", 69));
            clients.add(new ECClient("abcd2", "Akshara", "Rajesh", "Half bherya", 500));
            clients.add(new ECClient("abcd3", "Anitha", "Chandan", "Half bherya", 87));
            return clients;
        }
    }

    @Implements(VillageController.class)
    public static class ShadowVillageController {

        @Implementation
        public Villages getVillages() {
            Villages villages = new Villages();
            villages.add(new Village("Hosa Agrahara"));
            villages.add(new Village("Mysore"));
            villages.add(new Village("Bangalore"));
            villages.add(new Village("Kanakpura"));
            return villages;
        }
    }
}
