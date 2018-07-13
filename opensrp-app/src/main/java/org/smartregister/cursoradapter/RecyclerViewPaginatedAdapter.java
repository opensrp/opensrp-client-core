package org.smartregister.cursoradapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

/**
 * Created by keyman on 09/07/18.
 */
public class RecyclerViewPaginatedAdapter<V extends RecyclerView.ViewHolder> extends RecyclerViewCursorAdapter<V> {
    private final RecyclerViewProvider<V> listItemProvider;
    private CommonRepository commonRepository;

    public RecyclerViewPaginatedAdapter(Cursor cursor,
                                        RecyclerViewProvider<V>
                                                listItemProvider, CommonRepository
                                                commonRepository) {
        super(cursor);
        this.listItemProvider = listItemProvider;
        this.commonRepository = commonRepository;
    }

    @NonNull
    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return listItemProvider.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(V viewHolder, Cursor cursor) {
        CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
        pClient.setColumnmaps(personinlist.getColumnmaps());
        listItemProvider.getView(cursor, pClient, viewHolder);
    }

}
