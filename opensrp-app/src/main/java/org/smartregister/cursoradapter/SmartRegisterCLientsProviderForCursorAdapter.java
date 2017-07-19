package org.smartregister.cursoradapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewHolder.OnClickFormLauncher;

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
