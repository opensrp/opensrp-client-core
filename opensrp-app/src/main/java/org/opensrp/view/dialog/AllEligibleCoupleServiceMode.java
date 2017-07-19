package org.opensrp.view.dialog;

import android.view.View;
import org.opensrp.Context;
import org.opensrp.R;
import org.opensrp.provider.SmartRegisterClientsProvider;
import org.opensrp.view.contract.ANCSmartRegisterClient;
import org.opensrp.view.contract.ChildSmartRegisterClient;
import org.opensrp.view.contract.FPSmartRegisterClient;
import org.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

import static org.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class AllEligibleCoupleServiceMode extends ServiceModeOption {

    public AllEligibleCoupleServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.couple_selection);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 7;
            }

            @Override
            public int weightSum() {
                return 1000;
            }

            @Override
            public int[] weights() {
                return new int[]{239, 73, 103, 107, 158, 221, 87};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_ec_no, R.string.header_gplsa,
                        R.string.header_fp, R.string.header_children, R.string.header_status,
                        R.string.header_edit};
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }
}
