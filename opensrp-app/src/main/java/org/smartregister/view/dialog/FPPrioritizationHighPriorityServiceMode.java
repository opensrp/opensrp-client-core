package org.smartregister.view.dialog;

import android.view.View;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.*;
import org.smartregister.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewHolder.OnClickFormLauncher;

import static org.smartregister.AllConstants.FormNames.FP_CHANGE;
import static org.smartregister.AllConstants.FormNames.FP_COMPLICATIONS;
import static org.smartregister.Context.getInstance;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class FPPrioritizationHighPriorityServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationHighPriorityServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_high_priority_service_mode);
    }
}
