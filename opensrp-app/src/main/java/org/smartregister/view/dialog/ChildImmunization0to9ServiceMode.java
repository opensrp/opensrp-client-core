package org.smartregister.view.dialog;

import android.view.View;
import android.widget.TextView;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.domain.ChildServiceType;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.*;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.viewHolder.*;

import static org.smartregister.AllConstants.FormNames.CHILD_IMMUNIZATIONS;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;

public class ChildImmunization0to9ServiceMode extends ServiceModeOption {

    public ChildImmunization0to9ServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.child_service_mode_immunization_0_to_9);
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
                return new int[]{26, 14, 15, 15, 15, 15};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_id_no, R.string.header_bcg,
                        R.string.header_hep_b_birth, R.string.header_opv, R.string.header_pentavalent};
            }
        };

    }

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {
        viewHolder.serviceModeImmunization0to9View().setVisibility(View.VISIBLE);

        setupBcgLayout(client, viewHolder);
        setupHepBLayout(client, viewHolder);
        setupOpvLayout(client, viewHolder);
        setupPentavLayout(client, viewHolder);
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

    private void setupBcgLayout(ChildSmartRegisterClient client,
                                NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isBcgDone()) {
            viewHolder.bcgDoneLayout().setVisibility(View.VISIBLE);
            viewHolder.bcgDoneOnView().setText("On " + client.bcgDoneDate());
            viewHolder.bcgPendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.bcgDoneLayout().setVisibility(View.INVISIBLE);
            viewHolder.bcgPendingView().setVisibility(View.VISIBLE);
            viewHolder.bcgPendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    public void setupHepBLayout(ChildSmartRegisterClient client,
                                NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isHepBDone()) {
            viewHolder.hepBDoneOnView().setVisibility(View.VISIBLE);
            viewHolder.hepBDoneOnView().setText(client.hepBDoneDate());
        } else {
            viewHolder.hepBDoneOnView().setVisibility(View.INVISIBLE);
        }

        AlertDTO hepBAlert = client.getAlert(ChildServiceType.HEPB_0);
        if (hepBAlert != emptyAlert) {
            viewHolder.addHepBView().setVisibility(View.INVISIBLE);
            viewHolder.layoutHepBAlertView().setVisibility(View.VISIBLE);
            viewHolder.layoutHepBAlertView().setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutHepBAlertView(),
                    viewHolder.hepBAlertDueTypeView(),
                    viewHolder.hepBAlertDueOnView(),
                    hepBAlert);
        } else {
            viewHolder.layoutHepBAlertView().setVisibility(View.INVISIBLE);
            viewHolder.addHepBView().setVisibility(View.VISIBLE);
            viewHolder.addHepBView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    public void setupOpvLayout(ChildSmartRegisterClient client,
                               NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isOpvDone()) {
            viewHolder.opvDoneOnView().setVisibility(View.VISIBLE);
            viewHolder.opvDoneOnView().setText(client.opvDoneDate());
        } else {
            viewHolder.opvDoneOnView().setVisibility(View.INVISIBLE);
        }

        AlertDTO opvAlert = client.getAlert(ChildServiceType.OPV_0);
        if (opvAlert != emptyAlert) {
            viewHolder.addOpvView().setVisibility(View.INVISIBLE);
            viewHolder.layoutOpvAlertView().setVisibility(View.VISIBLE);
            viewHolder.layoutOpvAlertView().setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutOpvAlertView(),
                    viewHolder.opvAlertDueTypeView(),
                    viewHolder.opvAlertDueOnView(),
                    opvAlert);
        } else {
            viewHolder.layoutOpvAlertView().setVisibility(View.INVISIBLE);
            viewHolder.addOpvView().setVisibility(View.VISIBLE);
            viewHolder.addOpvView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    public void setupPentavLayout(ChildSmartRegisterClient client,
                                  NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isPentavDone()) {
            viewHolder.pentavDoneOnView().setVisibility(View.VISIBLE);
            viewHolder.pentavDoneOnView().setText(client.pentavDoneDate());
        } else {
            viewHolder.pentavDoneOnView().setVisibility(View.INVISIBLE);
        }

        AlertDTO pentavAlert = client.getAlert(ChildServiceType.PENTAVALENT_1);
        if (pentavAlert != emptyAlert) {
            viewHolder.addPentavView().setVisibility(View.INVISIBLE);
            viewHolder.layoutPentavAlertView().setVisibility(View.VISIBLE);
            viewHolder.layoutPentavAlertView().setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutPentavAlertView(),
                    viewHolder.pentavAlertDueTypeView(),
                    viewHolder.pentavAlertDueOnView(),
                    pentavAlert);
        } else {
            viewHolder.layoutPentavAlertView().setVisibility(View.INVISIBLE);
            viewHolder.addPentavView().setVisibility(View.VISIBLE);
            viewHolder.addPentavView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }
    private OnClickFormLauncher launchChildImmunizationForm(ChildSmartRegisterClient client) {
        return provider().newFormLauncher(CHILD_IMMUNIZATIONS, client.entityId(), null);
    }
    private void setAlertLayout(View layout, TextView typeView,
                                TextView dateView, AlertDTO alert) {
        typeView.setText(alert.type().shortName());
        dateView.setText("due " + alert.shortDate());

        final AlertStatus alertStatus = alert.alertStatus();
        layout.setBackgroundResource(alertStatus.backgroundColorResourceId());
        typeView.setTextColor(alertStatus.fontColor());
        dateView.setTextColor(alertStatus.fontColor());
    }
}
