package org.opensrp.view.dialog;

import android.view.View;
import org.opensrp.R;
import org.opensrp.provider.SmartRegisterClientsProvider;
import org.opensrp.view.contract.*;
import org.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.opensrp.view.viewHolder.OnClickFormLauncher;

import static org.opensrp.AllConstants.FormNames.FP_CHANGE;
import static org.opensrp.AllConstants.FormNames.FP_COMPLICATIONS;
import static org.opensrp.Context.getInstance;
import static org.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class FPPrioritizationHighPriorityServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationHighPriorityServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_high_priority_service_mode);
    }
}
