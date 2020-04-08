package org.smartregister.view.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.smartregister.view.ListContract;
import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.presenter.ListPresenter;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rkodev
 */
public abstract class BaseListFragment<T extends ListContract.Identifiable> extends Fragment implements ListContract.View<T> {

    private View view;
    private ListableAdapter<T, ListableViewHolder<T>> mAdapter;
    private ProgressBar progressBar;
    private ListContract.Presenter<T> presenter;
    private List<T> list;
    private AtomicInteger incompleteRequests = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(getRootLayout(), container, false);
        bindLayout();
        loadPresenter();
        presenter.fetchList(onStartCallable(getArguments()));
        return view;
    }

    /***
     * Provide a callable with no reference to the current view
     *
     * Avoid holding references to the view to prevent memory leaks
     *
     * @return
     */
    @NonNull
    protected abstract Callable<List<T>> onStartCallable(@Nullable Bundle bundle);

    /**
     * @return
     */
    @LayoutRes
    protected abstract int getRootLayout();

    @IdRes
    protected abstract int getRecyclerViewID();

    @IdRes
    protected abstract int getProgressBarID();

    @Override
    public void bindLayout() {
        RecyclerView recyclerView = view.findViewById(getRecyclerViewID());
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        progressBar = view.findViewById(getProgressBarID());
        progressBar.setVisibility(View.GONE);

        mAdapter = adapter();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void renderData(List<T> identifiables) {
        this.list = identifiables;
    }

    @Override
    public void refreshView() {
        mAdapter.reloadData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        int result = loadingState ? incompleteRequests.incrementAndGet() : incompleteRequests.decrementAndGet();
        progressBar.setVisibility(result > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public abstract void onListItemClicked(T t, int layoutID);

    @NonNull
    @Override
    public abstract ListableAdapter<T, ListableViewHolder<T>> adapter();

    @NonNull
    @Override
    public ListContract.Presenter<T> loadPresenter() {
        if (presenter == null) {
            presenter = new ListPresenter<T>()
                    .with(this);
        }
        return presenter;
    }
}
