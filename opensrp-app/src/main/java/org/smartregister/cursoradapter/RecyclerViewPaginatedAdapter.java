package org.smartregister.cursoradapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

/**
 * Created by keyman on 09/07/18.
 */
public class RecyclerViewPaginatedAdapter<V extends RecyclerView.ViewHolder> extends RecyclerViewCursorAdapter {
    private final RecyclerViewProvider<RecyclerView.ViewHolder> listItemProvider;
    private CommonRepository commonRepository;

    public RecyclerViewPaginatedAdapter(Cursor cursor,
                                        RecyclerViewProvider<RecyclerView.ViewHolder>
                                                listItemProvider, CommonRepository
                                                commonRepository) {
        super(cursor);
        this.listItemProvider = listItemProvider;
        this.commonRepository = commonRepository;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == RecyclerViewCursorAdapter.Type.FOOTER.ordinal()) {
            View view = listItemProvider.inflater().inflate(R.layout.smart_register_pagination, parent, false);
            return new FooterViewHolder(view);
        } else {
            return listItemProvider.createViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        if (FooterViewHolder.class.isInstance(viewHolder)) {

        } else {
            CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                    personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
            pClient.setColumnmaps(personinlist.getColumnmaps());
            listItemProvider.getView(cursor, pClient, viewHolder);
        }
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView pageInfoView;
        private Button nextPageView;
        private Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(R.id.btn_next_page);
            previousPageView = view.findViewById(R.id.btn_previous_page);
            pageInfoView = view.findViewById(R.id.txt_page_info);
        }
    }


}
