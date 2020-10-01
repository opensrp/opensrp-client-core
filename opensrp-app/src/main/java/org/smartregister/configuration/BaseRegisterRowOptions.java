package org.smartregister.configuration;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.holders.BaseRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020
 */

public class BaseRegisterRowOptions implements RegisterRowOptions<BaseRegisterViewHolder> {

    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull BaseRegisterViewHolder registerViewHolder) {
        // Do nothing
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }

    @Nullable
    @Override
    public BaseRegisterViewHolder createCustomViewHolder(@NonNull View itemView) {
        return null;
    }

    @Override
    public boolean useCustomViewLayout() {
        return false;
    }

    @Override
    public int getCustomViewLayoutId() {
        return 0;
    }
}
