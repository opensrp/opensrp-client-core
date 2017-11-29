package org.smartregister.adapter;

import android.view.View;
import android.view.ViewGroup;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

@RunWith(RobolectricTestRunner.class)
public class SmartRegisterPaginatedAdapterTest {

    @Test
    public void assertshouldReturn0PageCountFor0Clients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(0);

        Assert.assertEquals(adapter.getCount(), 0);
        Assert.assertEquals(adapter.pageCount(), 0);
        Assert.assertEquals(adapter.currentPage(), 0);
        Assert.assertFalse(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void assertshouldReturn1PageCountFor20Clients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(20);
        Assert.assertEquals(adapter.getCount(), 20);
        Assert.assertEquals(adapter.pageCount(), 1);
        Assert.assertEquals(adapter.currentPage(), 0);
        Assert.assertFalse(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void assertshouldReturn2PageCountFor21Clients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(21);
        Assert.assertEquals(adapter.getCount(), 20);
        Assert.assertEquals(adapter.pageCount(), 2);
        Assert.assertEquals(adapter.currentPage(), 0);
        Assert.assertTrue(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());

        adapter.nextPage();

        Assert.assertEquals(adapter.getCount(), 1);
        Assert.assertEquals(adapter.currentPage(), 1);
        Assert.assertFalse(adapter.hasNextPage());
        Assert.assertTrue(adapter.hasPreviousPage());

        adapter.previousPage();

        Assert.assertEquals(adapter.currentPage(), 0);
        Assert.assertTrue(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void assertshouldReturn3PageCountFor50Clients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(50);
        Assert.assertEquals(adapter.pageCount(), 3);
    }

    @Test
    public void assertgetItemShouldReturnRespectiveItem() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(50);
        Assert.assertEquals(((ECClient) adapter.getItem(0)).name(), "Name0");
        Assert.assertEquals(((ECClient) adapter.getItem(49)).name(), "Name49");
    }

    @Test
    public void assertgetViewShouldDelegateCallToProviderGetViewWithProperClient() {
        FakeClientsProvider fakeClientsProvider = new FakeClientsProvider(getSmartRegisterClients(50));
        SmartRegisterPaginatedAdapter adapter = getAdapter(fakeClientsProvider);

        adapter.getView(0, null, null);
        Assert.assertEquals("Name0", fakeClientsProvider.getViewCurrentClient.name());

        adapter.getView(49, null, null);
        Assert.assertEquals("Name49", fakeClientsProvider.getViewCurrentClient.name());
    }

    @Test
    public void assertgetItemIdShouldReturnTheActualPositionWithoutPagination() {
        FakeClientsProvider fakeClientsProvider = new FakeClientsProvider(getSmartRegisterClients(50));
        SmartRegisterPaginatedAdapter adapter = getAdapter(fakeClientsProvider);

        Assert.assertEquals(0, adapter.getItemId(0));
        Assert.assertEquals(19, adapter.getItemId(19));
        adapter.nextPage();
        Assert.assertEquals(20, adapter.getItemId(0));
        Assert.assertEquals(39, adapter.getItemId(19));
        adapter.nextPage();
        Assert.assertEquals(40, adapter.getItemId(0));
        Assert.assertEquals(49, adapter.getItemId(9));
    }

    @Test
    public void assertupdateClientsShouldApplyFilterToShowOnlyFiveClients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(50);
        Assert.assertEquals(3, adapter.pageCount());
        Assert.assertEquals(20, adapter.getCount());

        adapter.refreshList(null, null, null, null);

        Assert.assertEquals(1, adapter.pageCount());
        Assert.assertEquals(5, adapter.getCount());
    }

    @Test
    public void assertpaginationShouldWorkFor25ClientsPerPage() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(50, 25);
        Assert.assertEquals(2, adapter.pageCount());
        Assert.assertEquals(25, adapter.getCount());
    }

    private SmartRegisterPaginatedAdapter getAdapterWithFakeClients(int clientsCount) {
        return getAdapter(getFakeProvider(getSmartRegisterClients(clientsCount)));
    }

    private SmartRegisterPaginatedAdapter getAdapterWithFakeClients(int clientsCount, int clientsPerPage) {
        return getAdapter(clientsPerPage, getFakeProvider(getSmartRegisterClients(clientsCount)));
    }

    private SmartRegisterClients getSmartRegisterClients(int count) {
        SmartRegisterClients clients = new SmartRegisterClients();
        for (int i = 0; i < count; i++) {
            clients.add(getClient(i));
        }
        return clients;
    }

    private SmartRegisterClient getClient(int i) {
        return new ECClient("abcd" + i, "name" + i, "husband" + i, "village" + i, 1000 + i);
    }

    private FakeClientsProvider getFakeProvider(SmartRegisterClients clients) {
        return new FakeClientsProvider(clients);
    }

    private SmartRegisterPaginatedAdapter getAdapter(FakeClientsProvider provider) {
        return new SmartRegisterPaginatedAdapter(provider);
    }

    private SmartRegisterPaginatedAdapter getAdapter(int clientsPerPage, FakeClientsProvider provider) {
        return new SmartRegisterPaginatedAdapter(clientsPerPage, provider);
    }

    private class FakeClientsProvider implements SmartRegisterClientsProvider {
        private SmartRegisterClients clients;

        public SmartRegisterClient getViewCurrentClient;

        public FakeClientsProvider(SmartRegisterClients clients) {
            this.clients = clients;
        }

        @Override
        public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
            this.getViewCurrentClient = client;
            return null;
        }

        @Override
        public SmartRegisterClients getClients() {
            return clients;
        }

        @Override
        public SmartRegisterClients updateClients(
                FilterOption villageFilter, ServiceModeOption serviceModeOption,
                FilterOption searchFilter, SortOption sortOption) {
            return getSmartRegisterClients(5);
        }

        @Override
        public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

        }

        @Override
        public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
            return null;
        }
    }
}
