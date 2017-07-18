package org.ei.opensrp.view.dialog;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.*;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.ei.opensrp.AllConstants.FormNames.CHILD_ILLNESS;
import static org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class ChildOverviewServiceMode extends ServiceModeOption {
    private String illnessReport = Context.getInstance().getStringResource(R.string.illness_report);

    public ChildOverviewServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.child_service_mode_overview);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 6;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{26, 14, 12, 15, 23, 10};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_id_no, R.string.header_dob,
                        R.string.header_last_service, R.string.header_sick_status, R.string.header_edit};
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {
        viewHolder.serviceModeOverviewView().setVisibility(VISIBLE);

        setupDobView(client, viewHolder);
        setupLastServiceView(client, viewHolder);
        setupSickStatus(client, viewHolder, clientSectionClickListener);
        setupEditView(client, viewHolder, clientSectionClickListener);
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

    private void setupDobView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder) {
        viewHolder.dobView().setText(client.dateOfBirth());
    }

    private void setupLastServiceView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder) {
        ServiceProvidedDTO lastService = client.lastServiceProvided();
        viewHolder.lastServiceDateView().setText(lastService.date());
        viewHolder.lastServiceNameView().setText(lastService.type().displayName());
    }

    private void setupSickStatus(ChildSmartRegisterClient client,
                                 NativeChildSmartRegisterViewHolder viewHolder,
                                 View.OnClickListener onClickListener) {
        final ChildSmartRegisterClient.ChildSickStatus sickStatus = client.sickStatus();
        if (sickStatus == ChildSmartRegisterClient.ChildSickStatus.noDiseaseStatus) {
            viewHolder.sickVisitView().setVisibility(VISIBLE);
            viewHolder.sickVisitView().setTag(client);
            viewHolder.sickVisitView().setOnClickListener(
                    provider().newFormLauncher(CHILD_ILLNESS, client.entityId(), null));
            viewHolder.sicknessDetailLayout().setVisibility(GONE);
        } else {
            viewHolder.sickVisitView().setVisibility(GONE);
            viewHolder.sicknessDetailLayout().setVisibility(VISIBLE);

            viewHolder.illnessView().setText(String.format(illnessReport, sickStatus.diseases()));
            viewHolder.illnessDateView().setText(sickStatus.date());
        }
    }

    private void setupEditView(ChildSmartRegisterClient client,
                               NativeChildSmartRegisterViewHolder viewHolder,
                               View.OnClickListener onClickListener) {
        Drawable iconPencilDrawable = Context.getInstance().applicationContext().getResources().getDrawable(R.drawable.ic_pencil);
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }
}
