package org.smartregister.view.dialog;

import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.ANCServiceType;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.AlertStatus;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.viewholder.NativeANCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativePNCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static android.view.View.VISIBLE;
import static org.smartregister.AllConstants.ANCVisitFields.WEIGHT;
import static org.smartregister.AllConstants.FormNames.ANC_VISIT;
import static org.smartregister.domain.ANCServiceType.ANC_1;
import static org.smartregister.domain.ANCServiceType.ANC_2;
import static org.smartregister.domain.ANCServiceType.ANC_3;
import static org.smartregister.domain.ANCServiceType.ANC_4;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;

public class ANCVisitsServiceMode extends ServiceModeOption {

    public ANCVisitsServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.anc_service_mode_anc_visits);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 8;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{21, 9, 12, 12, 12, 12, 12, 10};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{R.string.header_name, R.string.header_id, R.string
                        .header_anc_status, R.string.header_anc_1, R.string.header_anc_2, R
                        .string.header_anc_3, R.string.header_anc_4, R.string.header_other};
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {
        viewHolder.serviceModeANCVisitsView().setVisibility(VISIBLE);
        setupANCVisit1Layout(client, viewHolder);
        setupANCVisit2Layout(client, viewHolder);
        setupANCVisit3Layout(client, viewHolder);
        setupANCVisit4Layout(client, viewHolder);
        setupANCVisitView(client, viewHolder);
    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    public void setupANCVisit1Layout(ANCSmartRegisterClient client,
                                     NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO ancVisitAlert = client.getAlert(ANC_1);
        ServiceProvidedDTO ancServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.ANC_1.displayName());
        viewHolder.hideViewsInANCVisit1Layout();
        if (ancServiceProvided != null) {
            setServiceProvidedLayout(client, ancServiceProvided,
                    viewHolder.layoutANCVisit1ServiceProvided(), viewHolder.txtANCVisit1DoneDate(),
                    viewHolder.lblANCVisit1Bp(), viewHolder.txtANCVisit1BpValue(),
                    viewHolder.lblANCVisit1Weight(), viewHolder.txtANCVisit1WeightValue());
        } else if (ancVisitAlert != emptyAlert && ancVisitAlert.name()
                .equalsIgnoreCase(ANC_1.shortName())) {
            setAlertLayout(viewHolder.layoutANCVisit1Alert(), viewHolder.txtANCVisit1DueType(),
                    viewHolder.txtANCVisit1DueOn(), client, ancVisitAlert);
        }
    }

    public void setupANCVisit2Layout(ANCSmartRegisterClient client,
                                     NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO ancVisitAlert = client.getAlert(ANC_2);
        ServiceProvidedDTO ancServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.ANC_2.displayName());
        viewHolder.hideViewsInANCVisit2Layout();
        if (ancServiceProvided != null) {
            setServiceProvidedLayout(client, ancServiceProvided,
                    viewHolder.layoutANCVisit2ServiceProvided(), viewHolder.txtANCVisit2DoneDate(),
                    viewHolder.lblANCVisit2Bp(), viewHolder.txtANCVisit2BpValue(),
                    viewHolder.lblANCVisit2Weight(), viewHolder.txtANCVisit2WeightValue());
        } else if (ancVisitAlert != emptyAlert && ancVisitAlert.name()
                .equalsIgnoreCase(ANC_2.shortName())) {
            setAlertLayout(viewHolder.layoutANCVisit2Alert(), viewHolder.txtANCVisit2DueType(),
                    viewHolder.txtANCVisit2DueOn(), client, ancVisitAlert);
        }
    }

    public void setupANCVisit3Layout(ANCSmartRegisterClient client,
                                     NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO ancVisitAlert = client.getAlert(ANC_3);
        ServiceProvidedDTO ancServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.ANC_3.displayName());
        viewHolder.hideViewsInANCVisit3Layout();
        if (ancServiceProvided != null) {
            setServiceProvidedLayout(client, ancServiceProvided,
                    viewHolder.layoutANCVisit3ServiceProvided(), viewHolder.txtANCVisit3DoneDate(),
                    viewHolder.lblANCVisit3Bp(), viewHolder.txtANCVisit3BpValue(),
                    viewHolder.lblANCVisit3Weight(), viewHolder.txtANCVisit3WeightValue());
        } else if (ancVisitAlert != emptyAlert && ancVisitAlert.name()
                .equalsIgnoreCase(ANC_3.shortName())) {
            setAlertLayout(viewHolder.layoutANCVisit3Alert(), viewHolder.txtANCVisit3DueType(),
                    viewHolder.txtANCVisit3DueOn(), client, ancVisitAlert);
        }
    }

    public void setupANCVisit4Layout(ANCSmartRegisterClient client,
                                     NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO ancVisitAlert = client.getAlert(ANC_4);
        ServiceProvidedDTO ancServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.ANC_4.displayName());
        viewHolder.hideViewsInANCVisit4Layout();
        if (ancServiceProvided != null) {
            setServiceProvidedLayout(client, ancServiceProvided,
                    viewHolder.layoutANCVisit4ServiceProvided(), viewHolder.txtANCVisit4DoneDate(),
                    viewHolder.lblANCVisit4Bp(), viewHolder.txtANCVisit4BpValue(),
                    viewHolder.lblANCVisit4Weight(), viewHolder.txtANCVisit4WeightValue());
        } else if (ancVisitAlert != emptyAlert && ancVisitAlert.name()
                .equalsIgnoreCase(ANC_4.shortName())) {
            setAlertLayout(viewHolder.layoutANCVisit4Alert(), viewHolder.txtANCVisit4DueType(),
                    viewHolder.txtANCVisit4DueOn(), client, ancVisitAlert);
        }
    }

    private void setupANCVisitView(ANCSmartRegisterClient client,
                                   NativeANCSmartRegisterViewHolder viewHolder) {
        viewHolder.btnOtherANCVisit()
                .setOnClickListener(provider().newFormLauncher(ANC_VISIT, client.entityId(), null));
        viewHolder.btnOtherANCVisit().setTag(client);
    }

    private OnClickFormLauncher launchForm(String formName, ANCSmartRegisterClient client,
                                           AlertDTO alert) {
        return provider().newFormLauncher(formName, client.entityId(),
                "{\"entityId\":\"" + client.entityId() + "\",\"alertName\":\"" + alert.name()
                        + "\"}");
    }

    private void setAlertLayout(View layout, TextView typeView, TextView dateView,
                                ANCSmartRegisterClient client, AlertDTO alert) {
        layout.setVisibility(View.VISIBLE);
        layout.setOnClickListener(launchForm(ANC_VISIT, client, alert));
        typeView.setVisibility(View.VISIBLE);
        dateView.setVisibility(View.VISIBLE);
        typeView.setText(alert.ancServiceType().shortName());
        dateView.setText("due " + alert.shortDate());

        final AlertStatus alertStatus = alert.alertStatus();
        layout.setBackgroundResource(alertStatus.backgroundColorResourceId());
        typeView.setTextColor(alertStatus.fontColor());
        dateView.setTextColor(alertStatus.fontColor());
    }

    private void setServiceProvidedLayout(ANCSmartRegisterClient client, ServiceProvidedDTO
            ancServiceProvided, View serviceProvidedLayout, TextView txtVisitDoneDate, TextView
                                                  lblBp, TextView txtBp, TextView lblWeight, TextView txtWeight) {
        serviceProvidedLayout.setVisibility(View.VISIBLE);
        txtVisitDoneDate.setVisibility(View.VISIBLE);
        txtVisitDoneDate.setText(ancServiceProvided.shortDate());
        if (StringUtils.isNotEmpty(client.getHyperTension(ancServiceProvided))) {
            setHyperTensionValues(client, ancServiceProvided, lblBp, txtBp);
        }
        if (StringUtils.isNotEmpty(ancServiceProvided.data().get(WEIGHT))) {
            setWeightValues(ancServiceProvided, lblWeight, txtWeight);
        }
    }

    private void setWeightValues(ServiceProvidedDTO ancServiceProvided, TextView lblWeight,
                                 TextView txtWeight) {
        txtWeight.setVisibility(View.VISIBLE);
        lblWeight.setVisibility(View.VISIBLE);
        txtWeight.setText(ancServiceProvided.data().get(WEIGHT));
    }

    private void setHyperTensionValues(ANCSmartRegisterClient client, ServiceProvidedDTO
            ancServiceProvided, TextView lblBp, TextView txtBp) {
        txtBp.setVisibility(View.VISIBLE);
        lblBp.setVisibility(View.VISIBLE);
        txtBp.setText(client.getHyperTension(ancServiceProvided));
    }

}
