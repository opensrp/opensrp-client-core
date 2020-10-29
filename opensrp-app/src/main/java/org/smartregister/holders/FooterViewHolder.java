package org.smartregister.holders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 23-09-2020
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {
    public TextView pageInfoView;
    public Button nextPageView;
    public Button previousPageView;

    public FooterViewHolder(View view) {
        super(view);

        nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
        previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
        pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
    }
}