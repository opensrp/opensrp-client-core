package org.smartregister.view.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.smartregister.R;
import org.smartregister.view.customcontrols.*;

public class NativePNCSmartRegisterViewHolder {
    private final ClientProfileView profileInfoLayout;
    private final TextView txtThayiNumberView;
    private final DeliveryInfoView deliveryInfoView;
    private final TextView txtComplicationsView;
    private final ClientPNCPpFpMethodView pncPpFpMethodView;
    private final TextView txtPpFpView;
    private final LinearLayout childHolderLayout;
    private final ImageButton editButton;
    private final ViewGroup pncOverviewServiceModeView;
    private final ViewGroup pncVisitsServiceModeView;
    private final TextView txtNumberOfVisits;
    private final TextView txtDOB;
    private final TextView txtVisitComplicationsView;
    private final WebView wbvPncVisitsGraph;
    private final LinearLayout recentPNCVisitsLayout;
    private final TextView btnPncVisitView;
    private final View layoutPNCVisitAlert;
    private final TextView txtPNCVisitDoneOn;
    private final TextView txtPNCVisitDueType;
    private final TextView txtPNCVisitAlertDueOn;

    public NativePNCSmartRegisterViewHolder(ViewGroup itemView) {
        this.profileInfoLayout = (ClientProfileView) itemView.findViewById(R.id.profile_info_layout);
        this.profileInfoLayout.initialize();

        this.txtThayiNumberView = (TextView) itemView.findViewById(R.id.txt_thayi_number);

        this.pncOverviewServiceModeView = (ViewGroup) itemView.findViewById(R.id.pnc_overview_service_mode_views);

        this.deliveryInfoView = (DeliveryInfoView) pncOverviewServiceModeView.findViewById(R.id.delivery_info_layout);
        this.deliveryInfoView.initialize();

        txtComplicationsView = (TextView) pncOverviewServiceModeView.findViewById(R.id.txt_complications);

        pncPpFpMethodView = (ClientPNCPpFpMethodView) pncOverviewServiceModeView.findViewById(R.id.pp_fp_method_layout);
        pncPpFpMethodView.initialize();
        pncPpFpMethodView.setLayoutParams();

        txtPpFpView = (TextView) pncPpFpMethodView.findViewById(R.id.txt_pp_fp_method_view);

        childHolderLayout = (LinearLayout) pncOverviewServiceModeView.findViewById(R.id.lyt_pnc_child_holder);

        editButton = (ImageButton) pncOverviewServiceModeView.findViewById(R.id.btn_edit);

        this.pncVisitsServiceModeView = (ViewGroup) itemView.findViewById(R.id.pnc_visits_service_mode_views);

        txtNumberOfVisits = (TextView) pncVisitsServiceModeView.findViewById(R.id.txt_no_of_visits);

        txtDOB = (TextView) pncVisitsServiceModeView.findViewById(R.id.txt_dob);

        txtVisitComplicationsView = (TextView) pncVisitsServiceModeView.findViewById(R.id.txt_pnc_visits_complications);

        wbvPncVisitsGraph = (WebView) pncVisitsServiceModeView.findViewById(R.id.wbv_pnc_visit_graph);
        wbvPncVisitsGraph.getSettings().setJavaScriptEnabled(true);
        wbvPncVisitsGraph.setVerticalScrollbarOverlay(false);
        wbvPncVisitsGraph.setHorizontalScrollbarOverlay(false);
        wbvPncVisitsGraph.setWebViewClient(new PNCWebViewClient());
        wbvPncVisitsGraph.loadUrl("file:///android_asset/www/pnc_graph/pnc_visit_graph.html");

        recentPNCVisitsLayout = (LinearLayout) pncVisitsServiceModeView.findViewById(R.id.lyt_pnc_recent_visits_holder);

        btnPncVisitView = (TextView) pncVisitsServiceModeView.findViewById(R.id.btn_pnc_visit);
        layoutPNCVisitAlert = pncVisitsServiceModeView.findViewById(R.id.layout_pnc_visit_alert);
        txtPNCVisitDoneOn = (TextView) pncVisitsServiceModeView.findViewById(R.id.txt_pnc_visit_on);
        txtPNCVisitDueType = (TextView) pncVisitsServiceModeView.findViewById(R.id.txt_pnc_visit_due_type);
        txtPNCVisitAlertDueOn = (TextView) pncVisitsServiceModeView.findViewById(R.id.txt_anc_visit_due_on);

    }

    public void hideAllServiceModeOptions() {
        pncOverviewServiceModeView().setVisibility(View.GONE);
        pncVisitsServiceModeView().setVisibility(View.GONE);
    }

    public ClientProfileView profileInfoLayout() {
        return profileInfoLayout;
    }

    public TextView txtThayiNumberView() {
        return txtThayiNumberView;
    }

    public ViewGroup pncOverviewServiceModeView() {
        return pncOverviewServiceModeView;
    }

    public ViewGroup pncVisitsServiceModeView() {
        return pncVisitsServiceModeView;
    }

    public DeliveryInfoView deliveryInfoView() {
        return deliveryInfoView;
    }

    public TextView txtComplicationsView() {
        return txtComplicationsView;
    }

    public ClientPNCPpFpMethodView pncPpFpMethodView() {
        return pncPpFpMethodView;
    }

    public TextView txtPpFpView() {
        return txtPpFpView;
    }

    public LinearLayout childHolderLayout() {
        return childHolderLayout;
    }

    public ImageButton editButton() {
        return editButton;
    }

    public TextView txtNumberOfVisits() {
        return txtNumberOfVisits;
    }

    public TextView txtDOB() {
        return txtDOB;
    }

    public TextView txtVisitComplicationsView() {
        return txtVisitComplicationsView;
    }

    public WebView wbvPncVisitsGraph() {
        return wbvPncVisitsGraph;
    }

    public LinearLayout recentPNCVisits() {
        return recentPNCVisitsLayout;
    }

    public TextView btnPncVisitView() {
        return btnPncVisitView;
    }

    public View layoutPNCVisitAlert() {
        return layoutPNCVisitAlert;
    }

    public TextView txtPNCVisitDoneOn() {
        return txtPNCVisitDoneOn;
    }

    public TextView txtPNCVisitDueType() {
        return txtPNCVisitDueType;
    }

    public TextView txtPNCVisitAlertDueOn() {
        return txtPNCVisitAlertDueOn;
    }
}