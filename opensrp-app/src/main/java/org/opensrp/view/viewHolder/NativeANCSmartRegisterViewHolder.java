package org.opensrp.view.viewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.opensrp.R;
import org.opensrp.view.customControls.ANCClientIdDetailsView;
import org.opensrp.view.customControls.ANCStatusView;
import org.opensrp.view.customControls.ClientProfileView;

public class NativeANCSmartRegisterViewHolder {
    private final ClientProfileView profileInfoLayout;
    private final ANCClientIdDetailsView ancClientIdDetailsView;
    private final ANCStatusView ancStatusView;

    private final TextView btnAncVisitView;
    private final View layoutANCVisitAlert;
    private final TextView txtANCVisitDoneOn;
    private final TextView txtANCVisitDueType;
    private final TextView txtANCVisitAlertDueOn;
    private final ViewGroup serviceModeViewsHolder;
    private final ViewGroup serviceModeOverviewView;
    private final TextView txtRiskFactors;
    private final TextView btnTTView;
    private final View layoutTTAlert;
    private final TextView txtTTDoneOn;
    private final TextView txtTTDueType;
    private final TextView txtTTDueOn;
    private final TextView btnIFAView;
    private final View layoutIFAAlert;
    private final TextView txtIFADoneOn;
    private final TextView txtIFADueType;
    private final TextView txtIFADueOn;
    private final ImageButton btnEditView;
    private final ViewGroup serviceModeANCVisitsView;
    private final View layoutANCVisit1Alert;
    private final View layoutANCVisit1ServiceProvided;
    private final TextView txtANCVisit1DueType;
    private final TextView txtANCVisit1DueOn;
    private final TextView txtANCVisit1DoneDate;
    private final TextView lblANCVisit1Bp;
    private final TextView lblANCVisit1Weight;
    private final TextView txtANCVisit1BpValue;
    private final TextView txtANCVisit1WeightValue;
    private final View layoutANCVisit2Alert;
    private final View layoutANCVisit2ServiceProvided;
    private final TextView txtANCVisit2DueType;
    private final TextView txtANCVisit2DueOn;
    private final TextView txtANCVisit2DoneDate;
    private final TextView lblANCVisit2Bp;
    private final TextView lblANCVisit2Weight;
    private final TextView txtANCVisit2BpValue;
    private final TextView txtANCVisit2WeightValue;
    private final View layoutANCVisit3Alert;
    private final View layoutANCVisit3ServiceProvided;
    private final TextView txtANCVisit3DueType;
    private final TextView txtANCVisit3DueOn;
    private final TextView txtANCVisit3DoneDate;
    private final TextView lblANCVisit3Bp;
    private final TextView lblANCVisit3Weight;
    private final TextView txtANCVisit3BpValue;
    private final TextView txtANCVisit3WeightValue;
    private final TextView btnOtherANCVisit;
    private final View layoutANCVisit4Alert;
    private final View layoutANCVisit4ServiceProvided;
    private final TextView txtANCVisit4DueType;
    private final TextView txtANCVisit4DueOn;
    private final TextView txtANCVisit4DoneDate;
    private final TextView lblANCVisit4Bp;
    private final TextView lblANCVisit4Weight;
    private final TextView txtANCVisit4BpValue;
    private final TextView txtANCVisit4WeightValue;
    private final ViewGroup serviceModeTTView;
    private final View layoutTT1Alert;
    private final TextView txtTT1DoneTick;
    private final TextView txtTT1Type;
    private final TextView txtTT1Date;
    private final View layoutTT2Alert;
    private final TextView txtTT2DoneTick;
    private final TextView txtTT2Type;
    private final TextView txtTT2Date;
    private final View layoutTTBoosterAlert;
    private final TextView txtTTBoosterDoneTick;
    private final TextView txtTTBoosterType;
    private final TextView txtTTBoosterDate;
    private final ViewGroup serviceModeHbIFAViewsHolder;
    private final LinearLayout layoutHbDetailsViewHolder;
    private final View layoutHbAlert;
    private final TextView txtHbDueType;
    private final TextView txtHbDueOn;
    private final TextView btnHbView;
    private final LinearLayout layoutIFADetailsViewHolder;
    private final View layoutIFAAlertInHbIFAServiceMode;
    private final TextView txtIFADoneTick;
    private final TextView txtIFAType;
    private final TextView txtIFADate;
    private final ViewGroup serviceModeDeliveryPlanViewsHolder;
    private final View layoutDeliveryPlanAlert;
    private final TextView txtDeliveryPlanDueType;
    private final TextView txtDeliveryPlanDueOn;
    private final View layoutDeliveryPlanServiceProvided;
    private final TextView lblDeliveryPlace;
    private final TextView txtDeliveryPlace;
    private final TextView txtTransport;
    private final TextView lblTransport;
    private final TextView lblHasCompanion;
    private final TextView txtHasCompanion;
    private final TextView lblAshaPhoneNumber;
    private final TextView txtAshaPhoneNumber;
    private final TextView lblContactPhoneNumber;
    private final TextView txtContactPhoneNumber;
    private final TextView txtRisksReviewed;
    private final TextView lblRisksReviewed;
    private final ImageView imgDeliveryPlaceStatus;
    private final ImageView imgTransportStatus;
    private final ImageView imgHasCompanionStatus;
    private final ImageView imgAshaPhoneNumberStatus;
    private final ImageView imgContactPhoneNumberStatus;
    private final ImageView imgRisksReviewedStatus;

    public NativeANCSmartRegisterViewHolder(ViewGroup itemView) {
        profileInfoLayout = (ClientProfileView) itemView.findViewById(R.id.profile_info_layout);
        profileInfoLayout.initialize();

        ancClientIdDetailsView = (ANCClientIdDetailsView) itemView.findViewById(R.id.client_id_details_layout);
        ancClientIdDetailsView.initialize();

        ancStatusView = (ANCStatusView) itemView.findViewById(R.id.client_status_layout);
        ancStatusView.initialize();

        serviceModeViewsHolder = (ViewGroup) itemView.findViewById(R.id.anc_register_service_mode_options_view);
        serviceModeOverviewView = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.overview_service_mode_views);
        serviceModeANCVisitsView = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.anc_visit_service_mode_views);
        serviceModeTTView = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.tt_service_mode_views);
        serviceModeHbIFAViewsHolder = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.hb_ifa_service_mode_views);
        serviceModeDeliveryPlanViewsHolder = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.delivery_plan_service_mode_views);

        txtRiskFactors = (TextView) serviceModeOverviewView.findViewById(R.id.txt_risk_factors);

        btnAncVisitView = (TextView) serviceModeOverviewView.findViewById(R.id.btn_anc_visit);
        layoutANCVisitAlert = serviceModeOverviewView.findViewById(R.id.layout_anc_visit_alert);
        txtANCVisitDoneOn = (TextView) serviceModeOverviewView.findViewById(R.id.txt_anc_visit_on);
        txtANCVisitDueType = (TextView) serviceModeOverviewView.findViewById(R.id.txt_anc_visit_due_type);
        txtANCVisitAlertDueOn = (TextView) serviceModeOverviewView.findViewById(R.id.txt_anc_visit_due_on);

        btnTTView = (TextView) serviceModeOverviewView.findViewById(R.id.btn_tt);
        layoutTTAlert = serviceModeOverviewView.findViewById(R.id.layout_tt_alert);
        txtTTDoneOn = (TextView) serviceModeOverviewView.findViewById(R.id.txt_tt_on);
        txtTTDueType = (TextView) serviceModeOverviewView.findViewById(R.id.txt_tt_due_type);
        txtTTDueOn = (TextView) serviceModeOverviewView.findViewById(R.id.txt_tt_due_on);

        btnIFAView = (TextView) serviceModeOverviewView.findViewById(R.id.btn_ifa);
        layoutIFAAlert = serviceModeOverviewView.findViewById(R.id.layout_ifa_alert);
        txtIFADoneOn = (TextView) serviceModeOverviewView.findViewById(R.id.txt_ifa_on);
        txtIFADueType = (TextView) serviceModeOverviewView.findViewById(R.id.txt_ifa_due_type);
        txtIFADueOn = (TextView) serviceModeOverviewView.findViewById(R.id.txt_ifa_due_on);

        btnEditView = (ImageButton) serviceModeOverviewView.findViewById(R.id.btn_edit);

        layoutANCVisit1Alert = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_1_alert);
        layoutANCVisit1ServiceProvided = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_1_service_provided);
        txtANCVisit1DueType = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_1_due_type);
        txtANCVisit1DueOn = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_1_due_on);
        txtANCVisit1DoneDate = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_1_done_date);
        lblANCVisit1Bp = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_1_bp);
        lblANCVisit1Weight = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_1_weight);
        txtANCVisit1BpValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_1_bp_value);
        txtANCVisit1WeightValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_1_weight_value);

        layoutANCVisit2Alert = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_2_alert);
        layoutANCVisit2ServiceProvided = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_2_service_provided);
        txtANCVisit2DueType = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_2_due_type);
        txtANCVisit2DueOn = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_2_due_on);
        txtANCVisit2DoneDate = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_2_done_date);
        lblANCVisit2Bp = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_2_bp);
        lblANCVisit2Weight = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_2_weight);
        txtANCVisit2BpValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_2_bp_value);
        txtANCVisit2WeightValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_2_weight_value);

        layoutANCVisit3Alert = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_3_alert);
        layoutANCVisit3ServiceProvided = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_3_service_provided);
        txtANCVisit3DueType = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_3_due_type);
        txtANCVisit3DueOn = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_3_due_on);
        txtANCVisit3DoneDate = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_3_done_date);
        lblANCVisit3Bp = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_3_bp);
        lblANCVisit3Weight = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_3_weight);
        txtANCVisit3BpValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_3_bp_value);
        txtANCVisit3WeightValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_3_weight_value);

        layoutANCVisit4Alert = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_4_alert);
        layoutANCVisit4ServiceProvided = serviceModeANCVisitsView.findViewById(R.id.layout_anc_visit_4_service_provided);
        txtANCVisit4DueType = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_4_due_type);
        txtANCVisit4DueOn = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_4_due_on);
        txtANCVisit4DoneDate = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_4_done_date);
        lblANCVisit4Bp = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_4_bp);
        lblANCVisit4Weight = (TextView) serviceModeANCVisitsView.findViewById(R.id.lbl_anc_visit_4_weight);
        txtANCVisit4BpValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_4_bp_value);
        txtANCVisit4WeightValue = (TextView) serviceModeANCVisitsView.findViewById(R.id.txt_anc_visit_4_weight_value);

        btnOtherANCVisit = (TextView) serviceModeANCVisitsView.findViewById(R.id.btn_other_anc_visit);

        layoutTT1Alert = serviceModeTTView.findViewById(R.id.layout_tt_1);
        txtTT1DoneTick = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_1_done);
        txtTT1Type = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_1_type);
        txtTT1Date = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_1_date);

        layoutTT2Alert = serviceModeTTView.findViewById(R.id.layout_tt_2);
        txtTT2DoneTick = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_2_done);
        txtTT2Type = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_2_type);
        txtTT2Date = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_2_date);

        layoutTTBoosterAlert = serviceModeTTView.findViewById(R.id.layout_tt_booster);
        txtTTBoosterDoneTick = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_booster_done);
        txtTTBoosterType = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_booster_type);
        txtTTBoosterDate = (TextView) serviceModeTTView.findViewById(R.id.txt_tt_booster_date);

        layoutHbDetailsViewHolder = (LinearLayout) serviceModeHbIFAViewsHolder.findViewById(R.id.layout_hb_details_holder);

        layoutHbAlert = serviceModeHbIFAViewsHolder.findViewById(R.id.layout_hb_alert);
        btnHbView = (TextView) serviceModeHbIFAViewsHolder.findViewById(R.id.btn_hb);
        txtHbDueType = (TextView) serviceModeHbIFAViewsHolder.findViewById(R.id.txt_hb_due_type);
        txtHbDueOn = (TextView) serviceModeHbIFAViewsHolder.findViewById(R.id.txt_hb_due_on);

        layoutIFADetailsViewHolder = (LinearLayout) serviceModeHbIFAViewsHolder.findViewById(R.id.layout_ifa_details_holder);

        layoutIFAAlertInHbIFAServiceMode = serviceModeHbIFAViewsHolder.findViewById(R.id.layout_ifa);
        txtIFADoneTick = (TextView) serviceModeHbIFAViewsHolder.findViewById(R.id.txt_ifa_done);
        txtIFAType = (TextView) serviceModeHbIFAViewsHolder.findViewById(R.id.txt_ifa_type);
        txtIFADate = (TextView) serviceModeHbIFAViewsHolder.findViewById(R.id.txt_ifa_date);

        layoutDeliveryPlanAlert = serviceModeDeliveryPlanViewsHolder.findViewById(R.id.layout_delivery_plan_alert);
        layoutDeliveryPlanServiceProvided = serviceModeDeliveryPlanViewsHolder.findViewById(R.id.layout_delivery_plan_service_provided);
        txtDeliveryPlanDueType = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_delivery_plan_due_type);
        txtDeliveryPlanDueOn = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_delivery_plan_due_on);
        imgDeliveryPlaceStatus = (ImageView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.img_delivery_place_status);
        lblDeliveryPlace = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.lbl_delivery_place);
        txtDeliveryPlace = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_delivery_place);
        imgTransportStatus = (ImageView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.img_transport_status);
        lblTransport = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.lbl_transport);
        txtTransport = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_transport);
        imgHasCompanionStatus = (ImageView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.img_has_companion_status);
        lblHasCompanion = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.lbl_has_companion);
        txtHasCompanion = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_has_companion);
        imgAshaPhoneNumberStatus = (ImageView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.img_asha_phone_number_status);
        lblAshaPhoneNumber = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.lbl_asha_phone_number);
        txtAshaPhoneNumber = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_asha_phone_number);
        imgContactPhoneNumberStatus = (ImageView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.img_contact_phone_number_status);
        lblContactPhoneNumber = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.lbl_contact_phone_number);
        txtContactPhoneNumber = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_contact_phone_number);
        imgRisksReviewedStatus = (ImageView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.img_risk_reviewed_status);
        lblRisksReviewed = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.lbl_risks_reviewed);
        txtRisksReviewed = (TextView) serviceModeDeliveryPlanViewsHolder.findViewById(R.id.txt_risks_reviewed);
    }

    public ClientProfileView profileInfoLayout() {
        return profileInfoLayout;
    }

    public ANCClientIdDetailsView ancClientIdDetailsView() {
        return ancClientIdDetailsView;
    }

    public ANCStatusView ancStatusView() {
        return ancStatusView;
    }

    public TextView btnAncVisitView() {
        return btnAncVisitView;
    }

    public View layoutANCVisitAlert() {
        return layoutANCVisitAlert;
    }

    public TextView txtANCVisitDoneOn() {
        return txtANCVisitDoneOn;
    }

    public TextView txtANCVisitDueType() {
        return txtANCVisitDueType;
    }

    public TextView txtANCVisitAlertDueOn() {
        return txtANCVisitAlertDueOn;
    }

    public TextView txtRiskFactors() {
        return txtRiskFactors;
    }

    public ViewGroup serviceModeOverviewView() {
        return serviceModeOverviewView;
    }

    public TextView btnTTView() {
        return btnTTView;
    }

    public View layoutTTAlert() {
        return layoutTTAlert;
    }

    public TextView txtTTDoneOn() {
        return txtTTDoneOn;
    }

    public TextView txtTTDueType() {
        return txtTTDueType;
    }

    public TextView txtTTDueOn() {
        return txtTTDueOn;
    }

    public TextView btnIFAView() {
        return btnIFAView;
    }

    public View layoutIFAAlert() {
        return layoutIFAAlert;
    }

    public TextView txtIFADoneOn() {
        return txtIFADoneOn;
    }

    public TextView txtIFADueType() {
        return txtIFADueType;
    }

    public TextView txtIFADueOn() {
        return txtIFADueOn;
    }

    public ImageButton btnEditView() {
        return btnEditView;
    }

    public ViewGroup serviceModeANCVisitsView() {
        return serviceModeANCVisitsView;
    }

    public View layoutANCVisit1Alert() {
        return layoutANCVisit1Alert;
    }

    public TextView txtANCVisit1DueType() {
        return txtANCVisit1DueType;
    }

    public TextView txtANCVisit1DueOn() {
        return txtANCVisit1DueOn;
    }

    public TextView txtANCVisit1DoneDate() {
        return txtANCVisit1DoneDate;
    }

    public TextView lblANCVisit1Bp() {
        return lblANCVisit1Bp;
    }

    public TextView lblANCVisit1Weight() {
        return lblANCVisit1Weight;
    }

    public TextView txtANCVisit1BpValue() {
        return txtANCVisit1BpValue;
    }

    public TextView txtANCVisit1WeightValue() {
        return txtANCVisit1WeightValue;
    }

    public void hideAllServiceModeOptions() {
        serviceModeOverviewView().setVisibility(View.GONE);
        serviceModeANCVisitsView().setVisibility(View.GONE);
        serviceModeTTView().setVisibility(View.GONE);
        serviceModeHbIFAViewsHolder().setVisibility(View.GONE);
        serviceModeDeliveryPlanViewsHolder().setVisibility(View.GONE);
    }

    public View layoutANCVisit2Alert() {
        return layoutANCVisit2Alert;
    }

    public TextView txtANCVisit2DueType() {
        return txtANCVisit2DueType;
    }

    public TextView txtANCVisit2DueOn() {
        return txtANCVisit2DueOn;
    }

    public TextView txtANCVisit2DoneDate() {
        return txtANCVisit2DoneDate;
    }

    public TextView lblANCVisit2Bp() {
        return lblANCVisit2Bp;
    }

    public TextView lblANCVisit2Weight() {
        return lblANCVisit2Weight;
    }

    public TextView txtANCVisit2BpValue() {
        return txtANCVisit2BpValue;
    }

    public TextView txtANCVisit2WeightValue() {
        return txtANCVisit2WeightValue;
    }

    public View layoutANCVisit3Alert() {
        return layoutANCVisit3Alert;
    }

    public TextView txtANCVisit3DueType() {
        return txtANCVisit3DueType;
    }

    public TextView txtANCVisit3DueOn() {
        return txtANCVisit3DueOn;
    }

    public TextView txtANCVisit3DoneDate() {
        return txtANCVisit3DoneDate;
    }

    public TextView lblANCVisit3Bp() {
        return lblANCVisit3Bp;
    }

    public TextView lblANCVisit3Weight() {
        return lblANCVisit3Weight;
    }

    public TextView txtANCVisit3BpValue() {
        return txtANCVisit3BpValue;
    }

    public TextView txtANCVisit3WeightValue() {
        return txtANCVisit3WeightValue;
    }

    public TextView btnOtherANCVisit() {
        return btnOtherANCVisit;
    }

    public View layoutANCVisit4Alert() {
        return layoutANCVisit4Alert;
    }

    public TextView txtANCVisit4DueType() {
        return txtANCVisit4DueType;
    }

    public TextView txtANCVisit4DueOn() {
        return txtANCVisit4DueOn;
    }

    public TextView txtANCVisit4DoneDate() {
        return txtANCVisit4DoneDate;
    }

    public TextView lblANCVisit4Bp() {
        return lblANCVisit4Bp;
    }

    public TextView lblANCVisit4Weight() {
        return lblANCVisit4Weight;
    }

    public TextView txtANCVisit4BpValue() {
        return txtANCVisit4BpValue;
    }

    public TextView txtANCVisit4WeightValue() {
        return txtANCVisit4WeightValue;
    }

    public void hideViewsInANCVisit1Layout() {
        layoutANCVisit1Alert().setVisibility(View.GONE);
        layoutANCVisit1ServiceProvided().setVisibility(View.GONE);
        txtANCVisit1DueType().setVisibility(View.GONE);
        txtANCVisit1DueOn().setVisibility(View.GONE);
        txtANCVisit1DoneDate().setVisibility(View.GONE);
        lblANCVisit1Bp().setVisibility(View.GONE);
        lblANCVisit1Weight().setVisibility(View.GONE);
        txtANCVisit1BpValue().setVisibility(View.GONE);
        txtANCVisit1WeightValue().setVisibility(View.GONE);
    }

    public void hideViewsInANCVisit2Layout() {
        layoutANCVisit2Alert().setVisibility(View.GONE);
        layoutANCVisit2ServiceProvided().setVisibility(View.GONE);
        txtANCVisit2DueType().setVisibility(View.GONE);
        txtANCVisit2DueOn().setVisibility(View.GONE);
        txtANCVisit2DoneDate().setVisibility(View.GONE);
        lblANCVisit2Bp().setVisibility(View.GONE);
        lblANCVisit2Weight().setVisibility(View.GONE);
        txtANCVisit2BpValue().setVisibility(View.GONE);
        txtANCVisit2WeightValue().setVisibility(View.GONE);
    }

    public void hideViewsInANCVisit3Layout() {
        layoutANCVisit3Alert().setVisibility(View.GONE);
        layoutANCVisit3ServiceProvided().setVisibility(View.GONE);
        txtANCVisit3DueType().setVisibility(View.GONE);
        txtANCVisit3DueOn().setVisibility(View.GONE);
        txtANCVisit3DoneDate().setVisibility(View.GONE);
        lblANCVisit3Bp().setVisibility(View.GONE);
        lblANCVisit3Weight().setVisibility(View.GONE);
        txtANCVisit3BpValue().setVisibility(View.GONE);
        txtANCVisit3WeightValue().setVisibility(View.GONE);
    }

    public void hideViewsInANCVisit4Layout() {
        layoutANCVisit4Alert().setVisibility(View.GONE);
        layoutANCVisit4ServiceProvided().setVisibility(View.GONE);
        txtANCVisit4DueType().setVisibility(View.GONE);
        txtANCVisit4DueOn().setVisibility(View.GONE);
        txtANCVisit4DoneDate().setVisibility(View.GONE);
        lblANCVisit4Bp().setVisibility(View.GONE);
        lblANCVisit4Weight().setVisibility(View.GONE);
        txtANCVisit4BpValue().setVisibility(View.GONE);
        txtANCVisit4WeightValue().setVisibility(View.GONE);
    }


    public View layoutANCVisit1ServiceProvided() {
        return layoutANCVisit1ServiceProvided;
    }

    public View layoutANCVisit2ServiceProvided() {
        return layoutANCVisit2ServiceProvided;
    }

    public View layoutANCVisit3ServiceProvided() {
        return layoutANCVisit3ServiceProvided;
    }

    public View layoutANCVisit4ServiceProvided() {
        return layoutANCVisit4ServiceProvided;
    }

    public ViewGroup serviceModeTTView() {
        return serviceModeTTView;
    }

    public View layoutTT1Alert() {
        return layoutTT1Alert;
    }

    public TextView txtTT1DoneTick() {
        return txtTT1DoneTick;
    }

    public TextView txtTT1Type() {
        return txtTT1Type;
    }

    public TextView txtTT1Date() {
        return txtTT1Date;
    }

    public void hideViewsInTT1Layout() {
        layoutTT1Alert().setVisibility(View.GONE);
        txtTT1DoneTick().setVisibility(View.GONE);
        txtTT1Type().setVisibility(View.GONE);
        txtTT1Date().setVisibility(View.GONE);
    }

    public View layoutTT2Alert() {
        return layoutTT2Alert;
    }

    public TextView txtTT2DoneTick() {
        return txtTT2DoneTick;
    }

    public TextView txtTT2Type() {
        return txtTT2Type;
    }

    public TextView txtTT2Date() {
        return txtTT2Date;
    }

    public void hideViewsInTT2Layout() {
        layoutTT2Alert().setVisibility(View.GONE);
        txtTT2DoneTick().setVisibility(View.GONE);
        txtTT2Type().setVisibility(View.GONE);
        txtTT2Date().setVisibility(View.GONE);
    }

    public View layoutTTBoosterAlert() {
        return layoutTTBoosterAlert;
    }

    public TextView txtTTBoosterDoneTick() {
        return txtTTBoosterDoneTick;
    }

    public TextView txtTTBoosterType() {
        return txtTTBoosterType;
    }

    public TextView txtTTBoosterDate() {
        return txtTTBoosterDate;
    }

    public void hideViewsInTTBoosterLayout() {
        layoutTTBoosterAlert().setVisibility(View.GONE);
        txtTTBoosterDoneTick().setVisibility(View.GONE);
        txtTTBoosterType().setVisibility(View.GONE);
        txtTTBoosterDate().setVisibility(View.GONE);
    }

    public LinearLayout layoutHbDetailsViewHolder() {
        return layoutHbDetailsViewHolder;
    }

    public ViewGroup serviceModeHbIFAViewsHolder() {
        return serviceModeHbIFAViewsHolder;
    }

    public View layoutHbAlert() {
        return layoutHbAlert;
    }

    public TextView txtHbDueType() {
        return txtHbDueType;
    }

    public TextView txtHbDueOn() {
        return txtHbDueOn;
    }

    public TextView btnHbView() {
        return btnHbView;
    }

    public LinearLayout layoutIFADetailsViewHolder() {
        return layoutIFADetailsViewHolder;
    }

    public View layoutIFAAlertInHbIFAServiceMode() {
        return layoutIFAAlertInHbIFAServiceMode;
    }

    public TextView txtIFADoneTick() {
        return txtIFADoneTick;
    }

    public TextView txtIFAType() {
        return txtIFAType;
    }

    public TextView txtIFADate() {
        return txtIFADate;
    }

    public void hideViewsInIFAAlertLayout() {
        layoutIFAAlertInHbIFAServiceMode().setVisibility(View.GONE);
        txtIFADoneTick().setVisibility(View.GONE);
        txtIFAType().setVisibility(View.GONE);
        txtIFADate().setVisibility(View.GONE);
    }

    public ViewGroup serviceModeDeliveryPlanViewsHolder() {
        return serviceModeDeliveryPlanViewsHolder;
    }

    public View layoutDeliveryPlanAlert() {
        return layoutDeliveryPlanAlert;
    }

    public TextView txtDeliveryPlanDueType() {
        return txtDeliveryPlanDueType;
    }

    public TextView txtDeliveryPlanDueOn() {
        return txtDeliveryPlanDueOn;
    }

    public View layoutDeliveryPlanServiceProvided() {
        return layoutDeliveryPlanServiceProvided;
    }

    public TextView lblDeliveryPlace() {
        return lblDeliveryPlace;
    }

    public TextView txtDeliveryPlace() {
        return txtDeliveryPlace;
    }

    public void hideViewsInDeliveryPlanViews() {
        layoutDeliveryPlanAlert().setVisibility(View.GONE);
        txtDeliveryPlanDueType().setVisibility(View.GONE);
        txtDeliveryPlanDueOn().setVisibility(View.GONE);
        layoutDeliveryPlanServiceProvided().setVisibility(View.GONE);
        lblDeliveryPlace().setVisibility(View.GONE);
        txtDeliveryPlace().setVisibility(View.GONE);
        txtTransport().setVisibility(View.GONE);
        lblTransport().setVisibility(View.GONE);
        lblHasCompanion().setVisibility(View.GONE);
        txtHasCompanion().setVisibility(View.GONE);
        lblAshaPhoneNumber().setVisibility(View.GONE);
        txtAshaPhoneNumber().setVisibility(View.GONE);
        lblContactPhoneNumber().setVisibility(View.GONE);
        txtContactPhoneNumber().setVisibility(View.GONE);
        txtRisksReviewed().setVisibility(View.GONE);
        lblRisksReviewed().setVisibility(View.GONE);
        imgDeliveryPlaceStatus().setVisibility(View.GONE);
        imgAshaPhoneNumberStatus().setVisibility(View.GONE);
        imgContactPhoneNumberStatus().setVisibility(View.GONE);
        imgHasCompanionStatus().setVisibility(View.GONE);
        imgRisksReviewedStatus().setVisibility(View.GONE);
        imgTransportStatus().setVisibility(View.GONE);
    }

    public TextView txtTransport() {
        return txtTransport;
    }

    public TextView lblTransport() {
        return lblTransport;
    }

    public TextView lblHasCompanion() {
        return lblHasCompanion;
    }

    public TextView txtHasCompanion() {
        return txtHasCompanion;
    }

    public TextView lblAshaPhoneNumber() {
        return lblAshaPhoneNumber;
    }

    public TextView txtAshaPhoneNumber() {
        return txtAshaPhoneNumber;
    }

    public TextView lblContactPhoneNumber() {
        return lblContactPhoneNumber;
    }

    public TextView txtContactPhoneNumber() {
        return txtContactPhoneNumber;
    }

    public TextView txtRisksReviewed() {
        return txtRisksReviewed;
    }

    public TextView lblRisksReviewed() {
        return lblRisksReviewed;
    }

    public ImageView imgDeliveryPlaceStatus() {
        return imgDeliveryPlaceStatus;
    }

    public ImageView imgTransportStatus() {
        return imgTransportStatus;
    }

    public ImageView imgHasCompanionStatus() {
        return imgHasCompanionStatus;
    }

    public ImageView imgAshaPhoneNumberStatus() {
        return imgAshaPhoneNumberStatus;
    }

    public ImageView imgContactPhoneNumberStatus() {
        return imgContactPhoneNumberStatus;
    }

    public ImageView imgRisksReviewedStatus() {
        return imgRisksReviewedStatus;
    }
}