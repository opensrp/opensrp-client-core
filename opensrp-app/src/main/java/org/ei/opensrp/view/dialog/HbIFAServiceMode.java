package org.ei.opensrp.view.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.FloatUtil;
import org.ei.opensrp.util.IntegerUtil;
import org.ei.opensrp.view.contract.*;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.viewHolder.*;

import static android.view.View.VISIBLE;
import static org.ei.opensrp.AllConstants.HbTestFields.HB_LEVEL;
import static org.ei.opensrp.AllConstants.SPACE;
import static org.ei.opensrp.Context.getInstance;
import static org.ei.opensrp.R.string.*;
import static org.ei.opensrp.domain.ANCServiceType.HB_TEST;
import static org.ei.opensrp.domain.ANCServiceType.IFA;
import static org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import static org.ei.opensrp.view.contract.AlertDTO.emptyAlert;
import static org.ei.opensrp.view.contract.AlertStatus.COMPLETE;

public class HbIFAServiceMode extends ServiceModeOption {

    private LayoutInflater inflater;

    public HbIFAServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
        this.inflater = LayoutInflater.from(Context.getInstance().applicationContext());
    }

    @Override
    public String name() {
        return getInstance().getStringResource(anc_service_mode_hb_ifa);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 5;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{21, 9, 12, 26, 26};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        header_name, header_id, header_anc_status,
                        header_hb, header_ifa};
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client,
                              NativeANCSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {
        viewHolder.serviceModeHbIFAViewsHolder().setVisibility(VISIBLE);

        setupHbDetailsLayout(client, viewHolder);
        setupHbAlertLayout(client, viewHolder);
        setupIFADetailsLayout(client, viewHolder);
        setupIFAAlertLayout(client, viewHolder);
    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    public void setupHbDetailsLayout(ANCSmartRegisterClient client,
                                     NativeANCSmartRegisterViewHolder viewHolder) {
        viewHolder.layoutHbDetailsViewHolder().removeAllViews();
        for (ServiceProvidedDTO serviceProvided : client.allServicesProvidedForAServiceType(HB_TEST.displayName())) {
            String hbLevel = serviceProvided.data().get(HB_LEVEL);
            ViewGroup hbDetailsViewGroup = (ViewGroup) inflater.inflate(R.layout.smart_register_anc_hb_details_layout, null);
            ((TextView) hbDetailsViewGroup.findViewById(R.id.txt_hb_date)).setText(serviceProvided.shortDate());
            ((TextView) hbDetailsViewGroup.findViewById(R.id.txt_hb_level)).setText(hbLevel + getInstance().getStringResource(anc_service_mode_hb_unit));
            hbDetailsViewGroup.findViewById(R.id.hb_level_indicator)
                    .setBackgroundColor(getHbColor(hbLevel));

            viewHolder.layoutHbDetailsViewHolder().addView(hbDetailsViewGroup);
        }
    }

    public void setupIFADetailsLayout(ANCSmartRegisterClient client,
                                      NativeANCSmartRegisterViewHolder viewHolder) {
        viewHolder.layoutIFADetailsViewHolder().removeAllViews();
        for (ServiceProvidedDTO serviceProvided : client.allServicesProvidedForAServiceType(IFA.serviceName())) {
            String numberOfIFATablets = serviceProvided.data().get("dose");
            ViewGroup ifaDetailsViewGroup = (ViewGroup) inflater.inflate(R.layout.smart_register_anc_ifa_details_layout, null);
            ((TextView) ifaDetailsViewGroup.findViewById(R.id.txt_ifa_date)).setText(serviceProvided.shortDate());
            ((TextView) ifaDetailsViewGroup.findViewById(R.id.txt_number_of_ifa_tablets))
                    .setText(numberOfIFATablets + getInstance().getStringResource(anc_service_mode_ifa_tablets));
            viewHolder.layoutIFADetailsViewHolder().addView(ifaDetailsViewGroup);
        }
    }

    public void setupIFAAlertLayout(ANCSmartRegisterClient client,
                                    NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO ifaAlert = client.getAlert(IFA);
        ServiceProvidedDTO ifaServiceProvided = client.getServiceProvidedDTO(IFA.serviceName());
        viewHolder.hideViewsInIFAAlertLayout();
        if (ifaAlert != emptyAlert
                && ifaAlert.ancServiceType().name().equalsIgnoreCase(IFA.serviceName())
                && !ifaAlert.status().equalsIgnoreCase("complete")) {
            viewHolder.layoutIFAAlertInHbIFAServiceMode().setVisibility(VISIBLE);
            viewHolder.layoutIFAAlertInHbIFAServiceMode().setOnClickListener(launchForm(AllConstants.FormNames.IFA, client, ifaAlert));
            viewHolder.txtIFAType().setVisibility(VISIBLE);
            viewHolder.txtIFADate().setVisibility(VISIBLE);
            setAlertLayout(viewHolder.layoutIFAAlertInHbIFAServiceMode(),
                    viewHolder.txtIFAType(),
                    viewHolder.txtIFADate(), ifaAlert);
        } else if (ifaServiceProvided != null) {
            setServiceProvidedLayout(client,
                    ifaServiceProvided, viewHolder.layoutIFAAlertInHbIFAServiceMode(),
                    viewHolder.txtIFADoneTick(), viewHolder.txtIFAType(), viewHolder.txtIFADate());
        }
    }


    private int getHbColor(String hbLevel) {
        float hbValue = FloatUtil.tryParse(hbLevel, 0F);
        if (hbValue < 7)
            return getInstance().getColorResource(R.color.hb_level_dangerous);
        else if (hbValue >= 7 && hbValue < 11) {
            return getInstance().getColorResource(R.color.hb_level_high);
        } else {
            return getInstance().getColorResource(R.color.hb_level_normal);
        }
    }

    public void setupHbAlertLayout(ANCSmartRegisterClient client,
                                   NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO hbAlert = client.getAlert(HB_TEST);
        if (hbAlert != emptyAlert) {
            viewHolder.btnHbView().setVisibility(View.INVISIBLE);
            viewHolder.layoutHbAlert().setVisibility(VISIBLE);
            viewHolder.layoutHbAlert().setOnClickListener(launchForm(AllConstants.FormNames.HB_TEST, client, hbAlert));
            setAlertLayout(viewHolder.layoutHbAlert(),
                    viewHolder.txtHbDueType(),
                    viewHolder.txtHbDueOn(),
                    hbAlert);
        } else {
            viewHolder.layoutHbAlert().setVisibility(View.INVISIBLE);
            viewHolder.btnHbView().setVisibility(View.VISIBLE);
            viewHolder.btnHbView().setOnClickListener(provider().newFormLauncher(AllConstants.FormNames.HB_TEST, client.entityId(), null));
        }
    }

    private OnClickFormLauncher launchForm(String formName, ANCSmartRegisterClient client, AlertDTO alert) {
        return provider().newFormLauncher(formName, client.entityId(), "{\"entityId\":\"" + client.entityId() + "\",\"alertName\":\"" + alert.name() + "\"}");
    }

    private void setAlertLayout(View layout, TextView typeView,
                                TextView dateView, AlertDTO alert) {
        setAlertDate(dateView, alert);
        typeView.setText(alert.ancServiceType().shortName());

        final AlertStatus alertStatus = alert.alertStatus();
        layout.setBackgroundResource(alertStatus.backgroundColorResourceId());
        typeView.setTextColor(alertStatus.fontColor());
        dateView.setTextColor(alertStatus.fontColor());
    }

    private void setAlertDate(TextView dateView, AlertDTO alert) {
        if (alert.status().equalsIgnoreCase(COMPLETE.name()))
            dateView.setText(alert.shortDate());
        else
            dateView.setText(getInstance().getStringResource(R.string.str_due) + alert.shortDate());
    }

    private void setServiceProvidedLayout(ANCSmartRegisterClient client, ServiceProvidedDTO serviceProvided, View serviceProvidedLayout,
                                          TextView txtDoneTick, TextView txtServiceType, TextView txtServiceDate) {
        serviceProvidedLayout.setVisibility(View.VISIBLE);
        serviceProvidedLayout.setBackgroundResource(R.color.status_bar_text_almost_white);

        txtDoneTick.setVisibility(View.VISIBLE);

        txtServiceType.setVisibility(VISIBLE);
        txtServiceType.setText(serviceProvided.name() + SPACE + getTotalNumberOfIFATablets(client));
        txtServiceType.setTextColor(getInstance().getColorResource(R.color.text_black));

        txtServiceDate.setVisibility(VISIBLE);
        txtServiceDate.setText("On " + serviceProvided.shortDate());
        txtServiceDate.setTextColor(getInstance().getColorResource(R.color.text_black));
    }

    private String getTotalNumberOfIFATablets(ANCSmartRegisterClient client) {
        int totalNumberOfIFATablets = 0;
        for (ServiceProvidedDTO serviceProvided : client.allServicesProvidedForAServiceType(IFA.serviceName())) {
            totalNumberOfIFATablets += IntegerUtil.tryParse(serviceProvided.data().get("dose"), 0);
        }
        return Integer.toString(totalNumberOfIFATablets);
    }


}
