package org.smartregister.view.dialog;

import android.view.View;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.viewholder.NativeANCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativePNCSmartRegisterViewHolder;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity
        .ClientsHeaderProvider;

public abstract class ServiceModeOption implements DialogOption {

    private SmartRegisterClientsProvider clientsProvider;

    public ServiceModeOption(SmartRegisterClientsProvider clientsProvider) {
        this.clientsProvider = clientsProvider;
    }

    public void apply() {
        clientsProvider.onServiceModeSelected(this);
    }

    public SmartRegisterClientsProvider provider() {
        return clientsProvider;
    }

    public abstract ClientsHeaderProvider getHeaderProvider();

    public abstract void setupListView(ChildSmartRegisterClient client,
                                       NativeChildSmartRegisterViewHolder viewHolder, View
                                               .OnClickListener clientSectionClickListener);

    public abstract void setupListView(ANCSmartRegisterClient client,
                                       NativeANCSmartRegisterViewHolder viewHolder, View
                                               .OnClickListener clientSectionClickListener);

    public abstract void setupListView(FPSmartRegisterClient client,
                                       NativeFPSmartRegisterViewHolder viewHolder, View
                                               .OnClickListener clientSectionClickListener);

    public abstract void setupListView(PNCSmartRegisterClient client,
                                       NativePNCSmartRegisterViewHolder viewHolder, View
                                               .OnClickListener clientSectionClickListener);

}
