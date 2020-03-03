package org.smartregister.util.mock;

import android.view.View;
import android.view.ViewGroup;

import org.mockito.Mockito;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

/**
 * Created by ndegwamartin on 2020-03-03.
 */
public class SmartRegisterClientProviderMock implements SmartRegisterClientsProvider {

    private SmartRegisterClient client = new SmartRegisterClientMock();

    @Override
    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
        return Mockito.mock(View.class);
    }

    @Override
    public SmartRegisterClients getClients() {

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(client);
        return clients;
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients();
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return new OnClickFormLauncher(Mockito.mock(SecuredActivity.class), formName, entityId, metaData);
    }
}
