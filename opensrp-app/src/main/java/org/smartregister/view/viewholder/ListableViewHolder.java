package org.smartregister.view.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.smartregister.view.ListContract;

/**
 * @author rkodev
 */

public abstract class ListableViewHolder<T extends ListContract.Identifiable> extends RecyclerView.ViewHolder implements ListContract.AdapterViewHolder<T> {
    public ListableViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
