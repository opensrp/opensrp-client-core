package org.smartregister.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.ListContract;
import org.smartregister.chw.domain.ReportType;
import org.smartregister.chw.viewholder.ListableViewHolder;
import org.smartregister.chw.viewholder.ReportViewHolder;

import java.util.List;

public class ReportsFragmentAdapter extends ListableAdapter<ReportType, ListableViewHolder<ReportType>> {

    public ReportsFragmentAdapter(List<ReportType> items, ListContract.View<ReportType> view) {
        super(items, view);
    }

    @NonNull
    @Override
    public ListableViewHolder<ReportType> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_fragment_item, parent, false);
        return new ReportViewHolder(view);
    }

}
