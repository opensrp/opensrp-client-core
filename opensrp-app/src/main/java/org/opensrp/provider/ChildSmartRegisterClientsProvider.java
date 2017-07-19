package org.opensrp.provider;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import org.opensrp.R;
import org.opensrp.view.activity.SecuredActivity;
import org.opensrp.view.contract.ChildSmartRegisterClient;
import org.opensrp.view.contract.SmartRegisterClient;
import org.opensrp.view.contract.SmartRegisterClients;
import org.opensrp.view.controller.ChildSmartRegisterController;
import org.opensrp.view.dialog.FilterOption;
import org.opensrp.view.dialog.ServiceModeOption;
import org.opensrp.view.dialog.SortOption;
import org.opensrp.view.viewHolder.ChildRegisterProfilePhotoLoader;
import org.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.opensrp.view.viewHolder.OnClickFormLauncher;
import org.opensrp.view.viewHolder.ProfilePhotoLoader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ChildSmartRegisterClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final SecuredActivity activity;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;

    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected ChildSmartRegisterController controller;

    private ServiceModeOption currentServiceModeOption;

    public ChildSmartRegisterClientsProvider(SecuredActivity activity,
                                             View.OnClickListener onClickListener,
                                             ChildSmartRegisterController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        photoLoader = new ChildRegisterProfilePhotoLoader(
                activity.getResources().getDrawable(R.drawable.child_boy_infant),
                activity.getResources().getDrawable(R.drawable.child_girl_infant));

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) activity.getResources().getDimension(R.dimen.list_item_height));
    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;
        NativeChildSmartRegisterViewHolder viewHolder;
        if (convertView == null) {
            itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_child_client, null);
            viewHolder = new NativeChildSmartRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeChildSmartRegisterViewHolder) itemView.getTag();
        }

        ChildSmartRegisterClient client = (ChildSmartRegisterClient)smartRegisterClient;

        if(client.isDataError()) {
            itemView.setBackgroundColor(Color.parseColor("#FDC3C3"));
        }

        setupClientProfileView(client, viewHolder);
        setupIdDetailsView(client, viewHolder);

        viewHolder.hideAllServiceModeOptions();
        currentServiceModeOption.setupListView(client, viewHolder, onClickListener);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupClientProfileView(SmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupIdDetailsView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder) {
        viewHolder.idDetailsView().bindData(client);
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        currentServiceModeOption = serviceModeOption;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return new OnClickFormLauncher(activity, formName, entityId, metaData);
    }

}
