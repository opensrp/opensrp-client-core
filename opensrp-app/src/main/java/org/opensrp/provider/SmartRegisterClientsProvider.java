package org.opensrp.provider;

import android.view.View;
import android.view.ViewGroup;
import org.opensrp.view.contract.SmartRegisterClient;
import org.opensrp.view.contract.SmartRegisterClients;
import org.opensrp.view.dialog.FilterOption;
import org.opensrp.view.dialog.ServiceModeOption;
import org.opensrp.view.dialog.SortOption;
import org.opensrp.view.viewHolder.OnClickFormLauncher;

public interface SmartRegisterClientsProvider {

    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup);

    public SmartRegisterClients getClients();

    SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                       FilterOption searchFilter, SortOption sortOption);

    void onServiceModeSelected(ServiceModeOption serviceModeOption);

    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData);
}
