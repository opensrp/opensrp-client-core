package org.smartregister.cursoradapter;

import android.content.Context;
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
public class RecyclerViewPaginatedAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerViewCursorAdapter<VH> {
    private final RecyclerViewProvider<VH> listItemProvider;
    private Context context;
    private CommonRepository commonRepository;

    public RecyclerViewPaginatedAdapter(Context context, Cursor cursor,
                                        RecyclerViewProvider<VH>
                                                listItemProvider, CommonRepository
                                                commonRepository) {
        super(context, cursor);
        this.listItemProvider = listItemProvider;
        this.context = context;
        this.commonRepository = commonRepository;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return listItemProvider.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, Cursor cursor) {
        CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
        pClient.setColumnmaps(personinlist.getColumnmaps());
        listItemProvider.getView(cursor, pClient, viewHolder);
    }

}
