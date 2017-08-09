package org.smartregister.view.dialog;

import android.view.View;

import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.viewholder.NativeANCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativePNCSmartRegisterViewHolder;

import static android.view.View.VISIBLE;
import static org.smartregister.Context.getInstance;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class FPPrioritizationAllECServiceMode extends ServiceModeOption {

    public FPPrioritizationAllECServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_all_ec_service_mode);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 4;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{24, 6, 11, 59};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{R.string.header_name, R.string.header_ec_no, R.string
                        .header_gplsa, R.string.header_fp_prioritization_risks};
            }
        };
    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {
        viewHolder.serviceModeFPPrioritization().setVisibility(VISIBLE);
        setupPrioritizationRisksView(client, viewHolder);
        setupAddFPView(client, clientSectionClickListener, viewHolder);
        viewHolder.lytFPVideosView().setOnClickListener(clientSectionClickListener);
    }

    private void setupPrioritizationRisksView(FPSmartRegisterClient client,
                                              NativeFPSmartRegisterViewHolder viewHolder) {
        viewHolder.txtPrioritizationRiskView().setText(client.highPriorityReason());
    }

    private void setupAddFPView(FPSmartRegisterClient client, View.OnClickListener
            clientSectionClickListener, NativeFPSmartRegisterViewHolder viewHolder) {
        viewHolder.lytAddFPView().setOnClickListener(clientSectionClickListener);
        viewHolder.lytAddFPView().setTag(client);
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }
}
