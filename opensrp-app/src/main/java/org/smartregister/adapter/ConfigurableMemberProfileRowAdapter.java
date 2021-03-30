package org.smartregister.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.R;
import org.smartregister.domain.ConfigurableMemberProfileRowData;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;

import java.util.List;

public class ConfigurableMemberProfileRowAdapter extends RecyclerView.Adapter<ConfigurableMemberProfileRowAdapter.MemberProfileBottomSectionViewHolder> {

    private List<ConfigurableMemberProfileRowData> dataList;
    private ConfigurableMemberProfileActivityContract.View view;

    public ConfigurableMemberProfileRowAdapter(ConfigurableMemberProfileActivityContract.View view, List<ConfigurableMemberProfileRowData> dataList) {
        this.view = view;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MemberProfileBottomSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.configurable_member_profile_row_item, parent, false);
        return new MemberProfileBottomSectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberProfileBottomSectionViewHolder holder, int position) {
        ConfigurableMemberProfileRowData data = dataList.get(position);
        holder.rowIcon.setImageResource(data.getRowIconId());
        holder.tvRowTitle.setText(data.getRowTitle());
        holder.tvRowDetail.setText(Html.fromHtml(data.getRowDetail()));
        holder.rowItemLinearLayout.setOnClickListener(v -> view.goToRowActivity(data.getRowClickedLaunchedClass()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MemberProfileBottomSectionViewHolder extends RecyclerView.ViewHolder {

        private ImageView rowIcon;
        private TextView tvRowTitle;
        private TextView tvRowDetail;
        private LinearLayout rowItemLinearLayout;

        private MemberProfileBottomSectionViewHolder(View view) {
            super(view);

            rowItemLinearLayout = view.findViewById(R.id.member_profile_row_item_layout);
            rowIcon = view.findViewById(R.id.row_icon);
            tvRowTitle = view.findViewById(R.id.text_view_title);
            tvRowDetail = view.findViewById(R.id.text_view_row_detail);
        }
    }
}
