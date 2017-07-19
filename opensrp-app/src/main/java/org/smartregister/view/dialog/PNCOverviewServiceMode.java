package org.smartregister.view.dialog;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.*;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.viewHolder.*;

import static android.view.View.VISIBLE;
import static org.smartregister.AllConstants.FormNames.PNC_POSTPARTUM_FAMILY_PLANNING;
import static org.smartregister.Context.getInstance;
import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class PNCOverviewServiceMode extends ServiceModeOption {

    private Drawable iconPencilDrawable;

    private final LayoutInflater inflater;

    public PNCOverviewServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
        this.inflater = LayoutInflater.from(Context.getInstance().applicationContext());
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.pnc_register_service_mode_overview);
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
                return new int[]{24, 8, 17, 15, 12, 24};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_thayi_number, R.string.header_delivery_info,
                        R.string.header_delivery_complications, R.string.header_pp_fp, R.string.header_child};
            }
        };
    }

    @Override
    public void setupListView(PNCSmartRegisterClient client,
                              NativePNCSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {
        viewHolder.pncOverviewServiceModeView().setVisibility(VISIBLE);
        setupDeliveryInfo(client, viewHolder);
        setupComplicationsView(client, viewHolder);
        setPpFpMethodView(client, viewHolder, getInstance().getColorResource(R.color.text_black));
        setChildView(client, viewHolder);
        setupEditView(client, viewHolder, clientSectionClickListener);
    }

    private void setupDeliveryInfo(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder) {
        viewHolder.deliveryInfoView().bindData(client);
    }

    private void setupComplicationsView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder) {
        viewHolder.txtComplicationsView().setText(client.deliveryComplications());
    }

    private void setPpFpMethodView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, int txtColorBlack) {
        viewHolder.pncPpFpMethodView().bindData(client, txtColorBlack);
        viewHolder.txtPpFpView().setOnClickListener(launchForm(PNC_POSTPARTUM_FAMILY_PLANNING, client.entityId(), null));
        viewHolder.txtPpFpView().setTag(client);
    }

    private void setChildView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder) {
        viewHolder.childHolderLayout().removeAllViews();
        for (ChildClient child : client.children()) {
            ViewGroup childViewGroup = (ViewGroup) inflater.inflate(R.layout.smart_register_pnc_child_layout, null);
            ((TextView) childViewGroup.findViewById(R.id.txt_child_gender)).setText(child.gender());
            ((TextView) childViewGroup.findViewById(R.id.txt_child_weight)).setText(String.format(org.smartregister.Context.getInstance().getStringResource(R.string.str_pnc_child_weight), child.weight()));
            viewHolder.childHolderLayout().addView(childViewGroup);
        }
    }

    private void setupEditView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener onClickListener) {
        if (iconPencilDrawable == null) {
            iconPencilDrawable = Context.getInstance().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }

    private OnClickFormLauncher launchForm(String formName, String entityId, String metaData) {
        return provider().newFormLauncher(formName, entityId, metaData);
    }

    @Override
    public void setupListView(FPSmartRegisterClient client,
                              NativeFPSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

}
