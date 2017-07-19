package org.smartregister.view.viewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import org.smartregister.R;
import org.smartregister.view.customControls.ClientIdDetailsView;
import org.smartregister.view.customControls.ClientProfileView;

public class NativeChildSmartRegisterViewHolder {
    private final ClientProfileView profileInfoLayout;
    private final ClientIdDetailsView idDetailsView;
    private final ImageButton editButton;
    private final TextView txtDobView;

    private final ViewGroup serviceModeViewsHolder;
    private final ViewGroup serviceModeOverviewView;
    private final ViewGroup serviceModeImmunization0to9View;
    private final ViewGroup serviceModeImmunization9PlusView;
    private final TextView txtLastServiceDate;
    private final TextView txtLastServiceName;
    private final TextView txtSickVisit;
    private final TextView lblIllnessDate;
    private final TextView txtIllness;
    private final TextView txtIllnessDate;
    private final View sicknessDeailLayout;
    private final TextView txtBcgPendingView;
    private final View layoutBcgOn;
    private final TextView txtBcgDoneOn;
    private final TextView txtHepBDoneOn;
    private final TextView txtPentavDoneOn;
    private final TextView txtOpvDoneOn;
    private final TextView txtMeaslesDoneOn;
    private final TextView txtOpvBoosterDoneOn;
    private final TextView txtDptBoosterDoneOn;
    private final TextView txtVitaminADoneOn;
    private final TextView btnOpv;
    private final View layoutOpvAlert;
    private final TextView btnHepB;
    private final View layoutHepBAlert;
    private final TextView btnPentav;
    private final View layoutPentavAlert;
    private final TextView txtOpvAlertDueOn;
    private final TextView txtHepBAlertDueOn;
    private final TextView txtPentavAlertDueOn;
    private final TextView txtOpvAlertDueType;
    private final TextView txtHepBAlertDueType;
    private final TextView txtPentavAlertDueType;
    private final TextView btnMeasles;
    private final View layoutMeaslesAlert;
    private final TextView txtMeaslesAlertDueType;
    private final TextView txtMeaslesAlertDueOn;
    private final TextView btnOpvBooster;
    private final View layoutOpvBoosterAlert;
    private final TextView txtOpvBoosterAlertDueType;
    private final TextView txtOpvBoosterAlertDueOn;
    private final TextView btnDptBooster;
    private final View layoutDptBoosterAlert;
    private final TextView txtDptBoosterAlertDueType;
    private final TextView txtDptBoosterAlertDueOn;
    private final TextView btnVitaminA;
    private final View layoutVitaminAAlert;
    private final TextView txtVitaminAAlertDueType;
    private final TextView txtVitaminAAlertDueOn;

    public NativeChildSmartRegisterViewHolder(ViewGroup itemView) {
        profileInfoLayout = (ClientProfileView) itemView.findViewById(R.id.profile_info_layout);
        profileInfoLayout.initialize();

        idDetailsView = (ClientIdDetailsView) itemView.findViewById(R.id.client_id_details_layout);
        idDetailsView.initialize();

        serviceModeViewsHolder = (ViewGroup) itemView.findViewById(R.id.child_register_service_mode_options_view);
        serviceModeOverviewView = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.overview_service_mode_views);
        serviceModeImmunization0to9View = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.immunization0to9_service_mode_views);
        serviceModeImmunization9PlusView = (ViewGroup) serviceModeViewsHolder.findViewById(R.id.immunization9plus_service_mode_views);

        txtDobView = (TextView) serviceModeOverviewView.findViewById(R.id.child_register_dob);
        editButton = (ImageButton) serviceModeOverviewView.findViewById(R.id.btn_edit);
        txtLastServiceDate = (TextView) serviceModeOverviewView.findViewById(R.id.last_service_date);
        txtLastServiceName = (TextView) serviceModeOverviewView.findViewById(R.id.last_service_name);
        txtSickVisit = (TextView) serviceModeOverviewView.findViewById(R.id.btn_sick_visit);
        sicknessDeailLayout = serviceModeOverviewView.findViewById(R.id.sick_details_layout);
        lblIllnessDate = (TextView) sicknessDeailLayout.findViewById(R.id.lbl_illness_date);
        txtIllness = (TextView) sicknessDeailLayout.findViewById(R.id.txt_illness);
        txtIllnessDate = (TextView) sicknessDeailLayout.findViewById(R.id.txt_illness_date);

        txtBcgPendingView = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_bcg_pending);
        layoutBcgOn = serviceModeImmunization0to9View.findViewById(R.id.layout_bcg_on);
        txtBcgDoneOn = (TextView) layoutBcgOn.findViewById(R.id.txt_bcg_on);

        btnOpv = (TextView) serviceModeImmunization0to9View.findViewById(R.id.btn_opv);
        layoutOpvAlert = serviceModeImmunization0to9View.findViewById(R.id.layout_opv_alert);
        txtOpvDoneOn = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_opv_on);
        txtOpvAlertDueType = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_opv_due_type);
        txtOpvAlertDueOn = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_opv_due_on);

        btnHepB = (TextView) serviceModeImmunization0to9View.findViewById(R.id.btn_hep_b_birth);
        layoutHepBAlert = serviceModeImmunization0to9View.findViewById(R.id.layout_hep_b_birth_alert);
        txtHepBDoneOn = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_hep_b_on);
        txtHepBAlertDueType = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_hep_b_birth_due_type);
        txtHepBAlertDueOn = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_hep_b_birth_due_on);

        txtPentavDoneOn = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_pentav_on);
        btnPentav = (TextView) serviceModeImmunization0to9View.findViewById(R.id.btn_pentav);
        layoutPentavAlert = serviceModeImmunization0to9View.findViewById(R.id.layout_pentav_alert);
        txtPentavAlertDueType = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_pentav_due_type);
        txtPentavAlertDueOn = (TextView) serviceModeImmunization0to9View.findViewById(R.id.txt_pentav_due_on);

        txtMeaslesDoneOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_measles_on);
        btnMeasles = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.btn_measles);
        layoutMeaslesAlert = serviceModeImmunization9PlusView.findViewById(R.id.layout_measles_alert);
        txtMeaslesAlertDueType = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_measles_due_type);
        txtMeaslesAlertDueOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_measles_due_on);

        txtOpvBoosterDoneOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_opv_booster_on);
        btnOpvBooster = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.btn_opv_booster);
        layoutOpvBoosterAlert = serviceModeImmunization9PlusView.findViewById(R.id.layout_opv_booster_alert);
        txtOpvBoosterAlertDueType = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_opv_booster_due_type);
        txtOpvBoosterAlertDueOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_opv_booster_due_on);

        txtDptBoosterDoneOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_dpt_booster_on);
        btnDptBooster = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.btn_dpt_booster);
        layoutDptBoosterAlert = serviceModeImmunization9PlusView.findViewById(R.id.layout_dpt_booster_alert);
        txtDptBoosterAlertDueType = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_dpt_booster_due_type);
        txtDptBoosterAlertDueOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_dpt_booster_due_on);

        txtVitaminADoneOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_vitamin_a_on);
        btnVitaminA = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.btn_vitamin_a);
        layoutVitaminAAlert = serviceModeImmunization9PlusView.findViewById(R.id.layout_vitamin_a_alert);
        txtVitaminAAlertDueType = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_vitamin_a_due_type);
        txtVitaminAAlertDueOn = (TextView) serviceModeImmunization9PlusView.findViewById(R.id.txt_vitamin_a_due_on);
    }

    public ClientProfileView profileInfoLayout() {
        return profileInfoLayout;
    }

    public ClientIdDetailsView idDetailsView() {
        return idDetailsView;
    }

    public TextView dobView() {
        return txtDobView;
    }

    public ImageButton editButton() {
        return editButton;
    }

    public ViewGroup serviceModeViewsHolder() {
        return serviceModeViewsHolder;
    }

    public ViewGroup serviceModeImmunization9PlusView() {
        return serviceModeImmunization9PlusView;
    }

    public ViewGroup serviceModeImmunization0to9View() {
        return serviceModeImmunization0to9View;
    }

    public ViewGroup serviceModeOverviewView() {
        return serviceModeOverviewView;
    }

    public void hideAllServiceModeOptions() {
        serviceModeOverviewView().setVisibility(View.GONE);
        serviceModeImmunization0to9View().setVisibility(View.GONE);
        serviceModeImmunization9PlusView().setVisibility(View.GONE);
    }

    public TextView lastServiceDateView() {
        return txtLastServiceDate;
    }

    public TextView lastServiceNameView() {
        return txtLastServiceName;
    }

    public TextView sickVisitView() {
        return txtSickVisit;
    }

    public TextView illnessDateLabelView() {
        return lblIllnessDate;
    }

    public TextView illnessView() {
        return txtIllness;
    }

    public TextView illnessDateView() {
        return txtIllnessDate;
    }

    public View sicknessDetailLayout() {
        return sicknessDeailLayout;
    }

    public TextView bcgPendingView() {
        return txtBcgPendingView;
    }

    public View bcgDoneLayout() {
        return layoutBcgOn;
    }

    public TextView bcgDoneOnView() {
        return txtBcgDoneOn;
    }

    public TextView hepBDoneOnView() {
        return txtHepBDoneOn;
    }

    public TextView pentavDoneOnView() {
        return txtPentavDoneOn;
    }

    public TextView vitaminADoneOnView() {
        return txtVitaminADoneOn;
    }

    public TextView dptBoosterDoneOnView() {
        return txtDptBoosterDoneOn;
    }

    public TextView opvBoosterDoneOnView() {
        return txtOpvBoosterDoneOn;
    }

    public TextView measlesDoneOnView() {
        return txtMeaslesDoneOn;
    }

    public TextView opvDoneOnView() {
        return txtOpvDoneOn;
    }

    public View layoutPentavAlertView() {
        return layoutPentavAlert;
    }

    public TextView addPentavView() {
        return btnPentav;
    }

    public View layoutHepBAlertView() {
        return layoutHepBAlert;
    }

    public TextView addHepBView() {
        return btnHepB;
    }

    public View layoutOpvAlertView() {
        return layoutOpvAlert;
    }

    public TextView addOpvView() {
        return btnOpv;
    }

    public TextView opvAlertDueOnView() {
        return txtOpvAlertDueOn;
    }

    public TextView hepBAlertDueOnView() {
        return txtHepBAlertDueOn;
    }

    public TextView pentavAlertDueOnView() {
        return txtPentavAlertDueOn;
    }

    public TextView pentavAlertDueTypeView() {
        return txtPentavAlertDueType;
    }

    public TextView hepBAlertDueTypeView() {
        return txtHepBAlertDueType;
    }

    public TextView opvAlertDueTypeView() {
        return txtOpvAlertDueType;
    }

    public TextView vitaminAAlertDueOnView() {
        return txtVitaminAAlertDueOn;
    }

    public TextView vitaminAAlertDueTypeView() {
        return txtVitaminAAlertDueType;
    }

    public View layoutVitaminAAlertView() {
        return layoutVitaminAAlert;
    }

    public TextView addVitaminAView() {
        return btnVitaminA;
    }

    public TextView dptBoosterAlertDueOnView() {
        return txtDptBoosterAlertDueOn;
    }

    public TextView dptBoosterAlertDueTypeView() {
        return txtDptBoosterAlertDueType;
    }

    public View layoutDptBoosterAlertView() {
        return layoutDptBoosterAlert;
    }

    public TextView addDptBoosterView() {
        return btnDptBooster;
    }

    public TextView opvBoosterAlertDueOnView() {
        return txtOpvBoosterAlertDueOn;
    }

    public TextView opvBoosterAlertDueTypeView() {
        return txtOpvBoosterAlertDueType;
    }

    public View layoutOpvBoosterAlertView() {
        return layoutOpvBoosterAlert;
    }

    public TextView addOpvBoosterView() {
        return btnOpvBooster;
    }

    public TextView measlesAlertDueOnView() {
        return txtMeaslesAlertDueOn;
    }

    public TextView measlesAlertDueTypeView() {
        return txtMeaslesAlertDueType;
    }

    public View layoutMeaslesAlertView() {
        return layoutMeaslesAlert;
    }

    public TextView addMeaslesView() {
        return btnMeasles;
    }
}