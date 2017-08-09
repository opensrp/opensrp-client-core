package org.smartregister.view.dialog;

import android.view.View;
import android.widget.TextView;

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
import static org.smartregister.AllConstants.FormNames.TT;
import static org.smartregister.Context.getInstance;
import static org.smartregister.domain.ANCServiceType.TT_1;
import static org.smartregister.domain.ANCServiceType.TT_2;
import static org.smartregister.domain.ANCServiceType.TT_BOOSTER;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;

public class TTServiceMode extends ServiceModeOption {

    public TTServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.anc_service_mode_tt);
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
                return new int[]{21, 9, 12, 19, 19, 20, 20};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{R.string.header_name, R.string.header_id, R.string
                        .header_anc_status, R.string.header_tt_1, R.string.header_tt_2, R.string
                        .header_tt_booster};
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
        viewHolder.serviceModeTTView().setVisibility(VISIBLE);

        setupTT1Layout(client, viewHolder);
        setupTT2Layout(client, viewHolder);
        setupTTBoosterLayout(client, viewHolder);
    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    public void setupTT1Layout(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder
            viewHolder) {
        AlertDTO ttAlert = client.getAlert(ANCServiceType.TT_1);
        ServiceProvidedDTO ttServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.TT_1.serviceName());
        viewHolder.hideViewsInTT1Layout();
        if (ttServiceProvided != null) {
            setServiceProvidedLayout(ttServiceProvided, viewHolder.layoutTT1Alert(),
                    viewHolder.txtTT1DoneTick(), viewHolder.txtTT1Type(), viewHolder.txtTT1Date());
        } else if (ttAlert != emptyAlert && ttAlert.name().equalsIgnoreCase(TT_1.serviceName())) {
            setAlertLayout(viewHolder.layoutTT1Alert(), viewHolder.txtTT1Type(),
                    viewHolder.txtTT1Date(), client, ttAlert);
        }
    }

    public void setupTT2Layout(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder
            viewHolder) {
        AlertDTO ttAlert = client.getAlert(ANCServiceType.TT_2);
        ServiceProvidedDTO ttServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.TT_2.serviceName());
        viewHolder.hideViewsInTT2Layout();
        if (ttServiceProvided != null) {
            setServiceProvidedLayout(ttServiceProvided, viewHolder.layoutTT2Alert(),
                    viewHolder.txtTT2DoneTick(), viewHolder.txtTT2Type(), viewHolder.txtTT2Date());
        } else if (ttAlert != emptyAlert && ttAlert.name().equalsIgnoreCase(TT_2.serviceName())) {
            setAlertLayout(viewHolder.layoutTT2Alert(), viewHolder.txtTT2Type(),
                    viewHolder.txtTT2Date(), client, ttAlert);
        }
    }

    public void setupTTBoosterLayout(ANCSmartRegisterClient client,
                                     NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO ttAlert = client.getAlert(ANCServiceType.TT_BOOSTER);
        ServiceProvidedDTO ttServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.TT_BOOSTER.serviceName());
        viewHolder.hideViewsInTTBoosterLayout();
        if (ttServiceProvided != null) {
            setServiceProvidedLayout(ttServiceProvided, viewHolder.layoutTTBoosterAlert(),
                    viewHolder.txtTTBoosterDoneTick(), viewHolder.txtTTBoosterType(),
                    viewHolder.txtTTBoosterDate());
        } else if (ttAlert != emptyAlert && ttAlert.name()
                .equalsIgnoreCase(TT_BOOSTER.serviceName())) {
            setAlertLayout(viewHolder.layoutTTBoosterAlert(), viewHolder.txtTTBoosterType(),
                    viewHolder.txtTTBoosterDate(), client, ttAlert);
        }
    }

    private void setServiceProvidedLayout(ServiceProvidedDTO ttServiceProvided, View
            serviceProvidedLayout, TextView txtDoneTick, TextView txtTTType, TextView txtTTDate) {
        serviceProvidedLayout.setVisibility(View.VISIBLE);
        serviceProvidedLayout.setBackgroundResource(R.color.status_bar_text_almost_white);

        txtDoneTick.setVisibility(View.VISIBLE);

        txtTTType.setVisibility(VISIBLE);
        txtTTType.setText(ttServiceProvided.name());
        txtTTType.setTextColor(getInstance().getColorResource(R.color.text_black));

        txtTTDate.setVisibility(VISIBLE);
        txtTTDate.setText("On " + ttServiceProvided.shortDate());
        txtTTDate.setTextColor(getInstance().getColorResource(R.color.text_black));
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
        layout.setOnClickListener(launchForm(TT, client, alert));
        typeView.setVisibility(View.VISIBLE);
        dateView.setVisibility(View.VISIBLE);
        typeView.setText(alert.name());
        dateView.setText("Due " + alert.shortDate());

        final AlertStatus alertStatus = alert.alertStatus();
        layout.setBackgroundResource(alertStatus.backgroundColorResourceId());
        typeView.setTextColor(alertStatus.fontColor());
        dateView.setTextColor(alertStatus.fontColor());
    }

}
