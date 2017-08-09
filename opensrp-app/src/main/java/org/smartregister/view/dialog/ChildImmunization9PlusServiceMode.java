package org.smartregister.view.dialog;

import android.view.View;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.domain.ChildServiceType;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.AlertStatus;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.viewholder.NativeANCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewholder.NativePNCSmartRegisterViewHolder;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.smartregister.AllConstants.FormNames.CHILD_IMMUNIZATIONS;
import static org.smartregister.AllConstants.FormNames.VITAMIN_A;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;

public class ChildImmunization9PlusServiceMode extends ServiceModeOption {

    public ChildImmunization9PlusServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance()
                .getStringResource(R.string.child_service_mode_immunization_9_plus);
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
                return new int[]{R.string.header_name, R.string.header_id_no, R.string
                        .header_measles, R.string.header_opv_booster, R.string
                        .header_dpt_booster, R.string.header_vitamin_a};
            }
        };

    }

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {
        viewHolder.serviceModeImmunization9PlusView().setVisibility(VISIBLE);

        setupMeaslesLayout(client, viewHolder);
        setupOpvBoosterLayout(client, viewHolder);
        setupDptBoosterLayout(client, viewHolder);
        setupVitaminALayout(client, viewHolder);
    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder
            viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    public void setupMeaslesLayout(ChildSmartRegisterClient client,
                                   NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isMeaslesDone()) {
            viewHolder.measlesDoneOnView().setVisibility(VISIBLE);
            viewHolder.measlesDoneOnView().setText(client.measlesDoneDate());
        } else {
            viewHolder.measlesDoneOnView().setVisibility(INVISIBLE);
        }

        AlertDTO measlesAlert = client.getAlert(ChildServiceType.MEASLES);
        if (measlesAlert != emptyAlert) {
            viewHolder.addMeaslesView().setVisibility(INVISIBLE);
            viewHolder.layoutMeaslesAlertView().setVisibility(VISIBLE);
            viewHolder.layoutMeaslesAlertView()
                    .setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutMeaslesAlertView(),
                    viewHolder.measlesAlertDueTypeView(), viewHolder.measlesAlertDueOnView(),
                    measlesAlert);
        } else {
            viewHolder.layoutMeaslesAlertView().setVisibility(INVISIBLE);
            viewHolder.addMeaslesView().setVisibility(VISIBLE);
            viewHolder.addMeaslesView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    public void setupOpvBoosterLayout(ChildSmartRegisterClient client,
                                      NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isOpvBoosterDone()) {
            viewHolder.opvBoosterDoneOnView().setVisibility(VISIBLE);
            viewHolder.opvBoosterDoneOnView().setText(client.opvBoosterDoneDate());
        } else {
            viewHolder.opvBoosterDoneOnView().setVisibility(INVISIBLE);
        }

        AlertDTO opvBoosterAlert = client.getAlert(ChildServiceType.OPV_BOOSTER);
        if (opvBoosterAlert != emptyAlert) {
            viewHolder.addOpvBoosterView().setVisibility(INVISIBLE);
            viewHolder.layoutOpvBoosterAlertView().setVisibility(VISIBLE);
            viewHolder.layoutOpvBoosterAlertView()
                    .setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutOpvBoosterAlertView(),
                    viewHolder.opvBoosterAlertDueTypeView(), viewHolder.opvBoosterAlertDueOnView(),
                    opvBoosterAlert);
        } else {
            viewHolder.layoutOpvBoosterAlertView().setVisibility(INVISIBLE);
            viewHolder.addOpvBoosterView().setVisibility(VISIBLE);
            viewHolder.addOpvBoosterView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    public void setupDptBoosterLayout(ChildSmartRegisterClient client,
                                      NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isDptBoosterDone()) {
            viewHolder.dptBoosterDoneOnView().setVisibility(VISIBLE);
            viewHolder.dptBoosterDoneOnView().setText(client.dptBoosterDoneDate());
        } else {
            viewHolder.dptBoosterDoneOnView().setVisibility(INVISIBLE);
        }

        AlertDTO dptBoosterAlert = client.getAlert(ChildServiceType.DPTBOOSTER_1);
        if (dptBoosterAlert != emptyAlert) {
            viewHolder.addDptBoosterView().setVisibility(INVISIBLE);
            viewHolder.layoutDptBoosterAlertView().setVisibility(VISIBLE);
            viewHolder.layoutDptBoosterAlertView()
                    .setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutDptBoosterAlertView(),
                    viewHolder.dptBoosterAlertDueTypeView(), viewHolder.dptBoosterAlertDueOnView(),
                    dptBoosterAlert);
        } else {
            viewHolder.layoutDptBoosterAlertView().setVisibility(INVISIBLE);
            viewHolder.addDptBoosterView().setVisibility(VISIBLE);
            viewHolder.addDptBoosterView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    public void setupVitaminALayout(ChildSmartRegisterClient client,
                                    NativeChildSmartRegisterViewHolder viewHolder) {
        if (client.isVitaminADone()) {
            viewHolder.vitaminADoneOnView().setVisibility(VISIBLE);
            viewHolder.vitaminADoneOnView().setText(client.vitaminADoneDate());
        } else {
            viewHolder.vitaminADoneOnView().setVisibility(INVISIBLE);
        }

        AlertDTO vitaminAAlert = client.getAlert(ChildServiceType.VITAMIN_A);
        if (vitaminAAlert != emptyAlert) {
            viewHolder.addVitaminAView().setVisibility(INVISIBLE);
            viewHolder.layoutVitaminAAlertView().setVisibility(VISIBLE);
            viewHolder.layoutVitaminAAlertView()
                    .setOnClickListener(launchChildImmunizationForm(client));
            setAlertLayout(viewHolder.layoutVitaminAAlertView(),
                    viewHolder.vitaminAAlertDueTypeView(), viewHolder.vitaminAAlertDueOnView(),
                    vitaminAAlert);
        } else {
            viewHolder.layoutVitaminAAlertView().setVisibility(INVISIBLE);
            viewHolder.addVitaminAView().setVisibility(VISIBLE);
            viewHolder.addVitaminAView().setOnClickListener(launchVitaminAForm(client));
        }
    }

    private View.OnClickListener launchVitaminAForm(ChildSmartRegisterClient client) {
        return provider().newFormLauncher(VITAMIN_A, client.entityId(), null);
    }

    private OnClickFormLauncher launchChildImmunizationForm(ChildSmartRegisterClient client) {
        return provider().newFormLauncher(CHILD_IMMUNIZATIONS, client.entityId(), null);
    }

    private void setAlertLayout(View layout, TextView typeView, TextView dateView, AlertDTO alert) {
        typeView.setText(alert.type().shortName());
        dateView.setText("due " + alert.shortDate());

        final AlertStatus alertStatus = alert.alertStatus();
        layout.setBackgroundResource(alertStatus.backgroundColorResourceId());
        typeView.setTextColor(alertStatus.fontColor());
        dateView.setTextColor(alertStatus.fontColor());
    }
}
