package org.smartregister.view.dialog;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.AllConstants;
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
import static org.smartregister.AllConstants.BOOLEAN_TRUE;
import static org.smartregister.AllConstants.DeliveryPlanFields.BIRTH_COMPANION;
import static org.smartregister.AllConstants.DeliveryPlanFields.DELIVERY_FACILITY_DH_VALUE;
import static org.smartregister.AllConstants.DeliveryPlanFields.DELIVERY_FACILITY_HOME_VALUE;
import static org.smartregister.AllConstants.DeliveryPlanFields.DELIVERY_FACILITY_NAME;
import static org.smartregister.AllConstants.DeliveryPlanFields.DELIVERY_FACILITY_SDH_VALUE;
import static org.smartregister.AllConstants.DeliveryPlanFields.PHONE_NUMBER;
import static org.smartregister.AllConstants.DeliveryPlanFields.REVIEWED_HRP_STATUS;
import static org.smartregister.AllConstants.DeliveryPlanFields.TRANSPORTATION_PLAN;
import static org.smartregister.domain.ANCServiceType.DELIVERY_PLAN;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;

public class DeliveryPlanServiceMode extends ServiceModeOption {

    public DeliveryPlanServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.anc_service_mode_delivery_plan);
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
                return new int[]{21, 9, 12, 58};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{R.string.header_name, R.string.header_id, R.string
                        .header_anc_status, R.string.header_delivery_plan};
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
        viewHolder.serviceModeDeliveryPlanViewsHolder().setVisibility(VISIBLE);

        setupDeliveryPlanLayout(client, viewHolder);
    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    public void setupDeliveryPlanLayout(ANCSmartRegisterClient client,
                                        NativeANCSmartRegisterViewHolder viewHolder) {
        AlertDTO deliveryPlanAlert = client.getAlert(ANCServiceType.DELIVERY_PLAN);
        ServiceProvidedDTO deliveryPlanServiceProvided = client
                .getServiceProvidedDTO(ANCServiceType.DELIVERY_PLAN.serviceName());
        viewHolder.hideViewsInDeliveryPlanViews();
        if (deliveryPlanServiceProvided != null) {
            viewHolder.layoutDeliveryPlanServiceProvided().setVisibility(VISIBLE);

            setDeliveryFacilityName(client, viewHolder, deliveryPlanServiceProvided);
            setTransportationPlan(viewHolder, deliveryPlanServiceProvided);
            setBirthCompanion(viewHolder, deliveryPlanServiceProvided);
            setAshaPhoneNumber(client, viewHolder);
            setContactPhoneNumber(viewHolder, deliveryPlanServiceProvided);
            setReviewedHRPStatus(client, viewHolder, deliveryPlanServiceProvided);
        } else if (deliveryPlanAlert != emptyAlert && deliveryPlanAlert.name().
                equalsIgnoreCase(DELIVERY_PLAN.serviceName())) {
            setAlertLayout(viewHolder.layoutDeliveryPlanAlert(),
                    viewHolder.txtDeliveryPlanDueType(), viewHolder.txtDeliveryPlanDueOn(), client,
                    deliveryPlanAlert);
        }
    }

    private void setDeliveryFacilityName(ANCSmartRegisterClient client,
                                         NativeANCSmartRegisterViewHolder viewHolder,
                                         ServiceProvidedDTO deliveryPlanServiceProvided) {
        viewHolder.lblDeliveryPlace().setVisibility(VISIBLE);
        viewHolder.txtDeliveryPlace().setVisibility(VISIBLE);
        String deliveryFacilityName = deliveryPlanServiceProvided.data()
                .get(DELIVERY_FACILITY_NAME);
        if (deliveryFacilityName != null) {
            viewHolder.txtDeliveryPlace().setText(humanize(deliveryFacilityName));
            if (isDeliveryPlaceAppropriate(client, deliveryFacilityName)) {
                setStatus(viewHolder.imgDeliveryPlaceStatus(),
                        CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_yes_large));
            } else {
                setStatus(viewHolder.imgDeliveryPlaceStatus(),
                        CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_remove));
            }
        } else {
            setStatus(viewHolder.imgDeliveryPlaceStatus(),
                    CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_remove));
        }
    }

    private void setTransportationPlan(NativeANCSmartRegisterViewHolder viewHolder,
                                       ServiceProvidedDTO deliveryPlanServiceProvided) {
        viewHolder.txtTransport().setVisibility(VISIBLE);
        viewHolder.lblTransport().setVisibility(VISIBLE);
        String transportationPlan = deliveryPlanServiceProvided.data().get(TRANSPORTATION_PLAN);
        setDeliveryPlanDetails(viewHolder.txtTransport(), viewHolder.imgTransportStatus(),
                transportationPlan);
    }

    private void setBirthCompanion(NativeANCSmartRegisterViewHolder viewHolder,
                                   ServiceProvidedDTO deliveryPlanServiceProvided) {
        viewHolder.txtHasCompanion().setVisibility(VISIBLE);
        viewHolder.lblHasCompanion().setVisibility(VISIBLE);
        String birthCompanion = deliveryPlanServiceProvided.data().get(BIRTH_COMPANION);
        if (birthCompanion != null && birthCompanion.equalsIgnoreCase(BOOLEAN_TRUE)) {
            viewHolder.txtHasCompanion().setText(humanize(birthCompanion));
            setStatus(viewHolder.imgHasCompanionStatus(),
                    CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_yes_large));
        } else {
            viewHolder.txtHasCompanion().setText(humanize(birthCompanion));
            setStatus(viewHolder.imgHasCompanionStatus(),
                    CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_remove));
        }
    }

    private void setAshaPhoneNumber(ANCSmartRegisterClient client,
                                    NativeANCSmartRegisterViewHolder viewHolder) {
        viewHolder.txtAshaPhoneNumber().setVisibility(VISIBLE);
        viewHolder.lblAshaPhoneNumber().setVisibility(VISIBLE);
        String ashaPhoneNumber = client.ashaPhoneNumber();
        setDeliveryPlanDetails(viewHolder.txtAshaPhoneNumber(),
                viewHolder.imgAshaPhoneNumberStatus(), ashaPhoneNumber);
    }

    private void setContactPhoneNumber(NativeANCSmartRegisterViewHolder viewHolder,
                                       ServiceProvidedDTO deliveryPlanServiceProvided) {
        viewHolder.txtContactPhoneNumber().setVisibility(VISIBLE);
        viewHolder.lblContactPhoneNumber().setVisibility(VISIBLE);
        String phoneNumber = deliveryPlanServiceProvided.data().get(PHONE_NUMBER);
        setDeliveryPlanDetails(viewHolder.txtContactPhoneNumber(),
                viewHolder.imgContactPhoneNumberStatus(), phoneNumber);
    }

    private void setReviewedHRPStatus(ANCSmartRegisterClient client,
                                      NativeANCSmartRegisterViewHolder viewHolder,
                                      ServiceProvidedDTO deliveryPlanServiceProvided) {
        if (client.isHighRisk()) {
            viewHolder.txtRisksReviewed().setVisibility(VISIBLE);
            viewHolder.lblRisksReviewed().setVisibility(VISIBLE);
            String reviewedHRPStatus = deliveryPlanServiceProvided.data().get(REVIEWED_HRP_STATUS);
            setDeliveryPlanDetails(viewHolder.txtRisksReviewed(),
                    viewHolder.imgRisksReviewedStatus(), reviewedHRPStatus);
        }
    }

    private boolean isDeliveryPlaceAppropriate(ANCSmartRegisterClient client, String
            deliveryFacilityName) {
        return isClientHighRiskAndDeliveryPlaceIsSDHOrDH(client, deliveryFacilityName)
                || isClientNotHighRiskAndDeliveryPlaceIsHome(client, deliveryFacilityName);
    }

    private boolean isClientNotHighRiskAndDeliveryPlaceIsHome(ANCSmartRegisterClient client,
                                                              String deliveryFacilityName) {
        return !client.isHighRisk() && (deliveryFacilityName
                .equalsIgnoreCase(DELIVERY_FACILITY_HOME_VALUE));
    }

    private boolean isClientHighRiskAndDeliveryPlaceIsSDHOrDH(ANCSmartRegisterClient client,
                                                              String deliveryFacilityName) {
        return client.isHighRisk() && (
                deliveryFacilityName.equalsIgnoreCase(DELIVERY_FACILITY_SDH_VALUE)
                        || deliveryFacilityName.equalsIgnoreCase(DELIVERY_FACILITY_DH_VALUE));
    }

    private void setStatus(ImageView imageView, Drawable drawable) {
        imageView.setVisibility(VISIBLE);
        imageView.setBackground(drawable);
    }

    private void setDeliveryPlanDetails(TextView txtDetails, ImageView transportStatus, String
            detail) {
        if (detail != null) {
            txtDetails.setText(humanize(detail));
            setStatus(transportStatus, CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_yes_large));
        } else {
            setStatus(transportStatus, CoreLibrary.getInstance().context().getDrawable(R.drawable.ic_remove));
        }
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
        layout.setOnClickListener(launchForm(AllConstants.FormNames.DELIVERY_PLAN, client, alert));
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
