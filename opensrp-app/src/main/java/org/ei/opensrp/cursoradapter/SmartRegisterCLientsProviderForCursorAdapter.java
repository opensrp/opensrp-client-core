package org.ei.opensrp.cursoradapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

/**
 * Created by raihan on 3/9/16.
 */
public interface SmartRegisterCLientsProviderForCursorAdapter  {
    public void getView(SmartRegisterClient client,View view);
    SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                       FilterOption searchFilter, SortOption sortOption);
    void onServiceModeSelected(ServiceModeOption serviceModeOption);
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData);
    public LayoutInflater inflater();
    public View inflatelayoutForCursorAdapter();
}
