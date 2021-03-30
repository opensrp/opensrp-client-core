package org.smartregister.holders;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.R;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 23-09-2020
 */

public class BaseRegisterViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewParentName;
    public TextView textViewChildName;
    public TextView textViewGender;
    public Button dueButton;
    public View dueButtonLayout;
    public View childColumn;
    public TextView tvRegisterType;
    public TextView tvLocation;

    public TextView secondDotDivider;
    public TextView firstDotDivider;

    public BaseRegisterViewHolder(View itemView) {
        super(itemView);

        textViewParentName = itemView.findViewById(R.id.tv_baseRegisterListRow_guardianName);
        textViewChildName = itemView.findViewById(R.id.tv_baseRegisterListRow_clientName);
        textViewGender = itemView.findViewById(R.id.tv_baseRegisterListRow_gender);
        dueButton = itemView.findViewById(R.id.btn_baseRegisterListRow_clientAction);
        dueButtonLayout = itemView.findViewById(R.id.ll_baseRegisterListRow_clientActionWrapper);
        tvRegisterType = itemView.findViewById(R.id.tv_baseRegisterListRow_registerType);
        tvLocation = itemView.findViewById(R.id.tv_baseRegisterListRow_location);

        childColumn = itemView.findViewById(R.id.view_clientColumn);
        secondDotDivider = itemView.findViewById(R.id.tv_baseRegisterListRow_secondDotDivider);
        firstDotDivider = itemView.findViewById(R.id.tv_baseRegisterListRow_firstDotDivider);
    }

    public void showGuardianName() {
        textViewParentName.setVisibility(View.VISIBLE);
    }

    public void removeGuardianName() {
        textViewParentName.setVisibility(View.GONE);
    }

    public void showPersonLocation() {
        tvLocation.setVisibility(View.VISIBLE);
        secondDotDivider.setVisibility(View.VISIBLE);
    }

    public void removePersonLocation() {
        tvLocation.setVisibility(View.GONE);
        secondDotDivider.setVisibility(View.GONE);
    }

    public void showRegisterType() {
        tvRegisterType.setVisibility(View.VISIBLE);
        firstDotDivider.setVisibility(View.VISIBLE);
    }

    public void hideRegisterType() {
        tvRegisterType.setVisibility(View.GONE);
        firstDotDivider.setVisibility(View.GONE);
    }
}