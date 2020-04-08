package org.smartregister.view;


import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author rkodev
 */

public interface ListContract {

    interface Model<T extends ListContract.Identifiable> {

    }

    interface View<T extends ListContract.Identifiable> {

        void bindLayout();

        void renderData(List<T> identifiables);

        void refreshView();

        void setLoadingState(boolean loadingState);

        void onListItemClicked(T t, @IdRes int layoutID);

        /***
         * check fetch execution error and handle gracefully
         *
         * You can force close the view, retry a couple of times or display a new view
         *
         * @param ex
         */
        void onFetchError(Exception ex);

        @NonNull
        ListableAdapter<T, ListableViewHolder<T>> adapter();

        @NonNull
        Presenter<T> loadPresenter();

        boolean hasDivider();
    }

    interface Presenter<T extends ListContract.Identifiable> {

        void fetchList(@NonNull Callable<List<T>> callable);

        void onFetchRequestError(Exception e);

        void onItemsFetched(List<T> identifiables);

        /**
         * binds the view
         *
         * @param view
         */
        <V extends View<T>> Presenter<T> with(V view);

        /**
         * binds interactor
         *
         * @param interactor
         * @return
         */
        <I extends Interactor<T>> Presenter<T> using(I interactor);

        /**
         * binds a views model
         *
         * @param model
         * @return
         */
        <M extends Model<T>> Presenter<T> withModel(M model);

        @Nullable
        View<T> getView();

        <M extends Model<T>> M getModel();
    }

    interface Interactor<T extends ListContract.Identifiable> {

        /**
         * @param callable
         * @param presenter
         */
        void runRequest(Callable<List<T>> callable, Presenter<T> presenter);
    }

    interface Identifiable {
        @NonNull
        String getID();
    }


    interface AdapterViewHolder<T extends ListContract.Identifiable> {

        /**
         * bind view to object
         *
         * @param t
         */
        void bindView(T t, ListContract.View<T> view);

        /**
         * reset the view details
         */
        void resetView();
    }

}
