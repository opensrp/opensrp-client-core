package org.smartregister.sample.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.sample.domain.Report;
import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.fragment.BaseListFragment;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.List;
import java.util.concurrent.Callable;

public class ReportFragment extends BaseListFragment<Report> {
    public static final String TAG = "ReportFragment";

    @NonNull
    @Override
    protected Callable<List<Report>> onStartCallable(@Nullable Bundle bundle) {
        return null;
    }

    @Override
    protected int getRootLayout() {
        return 0;
    }

    @Override
    protected int getRecyclerViewID() {
        return 0;
    }

    @Override
    protected int getProgressBarID() {
        return 0;
    }

    @Override
    public void onListItemClicked(Report report, int layoutID) {

    }

    @NonNull
    @Override
    public ListableAdapter<Report, ListableViewHolder<Report>> adapter() {
        return null;
    }
}
