package org.smartregister.configuration;

import android.database.Cursor;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.holders.BaseRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;
/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 23-09-2020
 */

public interface RegisterRowOptions<T extends BaseRegisterViewHolder> {

    boolean isDefaultPopulatePatientColumn();

    /**
     * You should set all the data that should be displayed for each client column here. For this use
     * the #baseRegisterViewHolder passed and in case you are using a custom one you can just cast it to
     * whatever you provided in {@link RegisterRowOptions#createCustomViewHolder}
     *
     * @param cursor                   cursor object on the current row
     * @param commonPersonObjectClient Contains the column maps for the current user
     * @param smartRegisterClient
     * @param baseRegisterViewHolder    The recycler view holder which holds the required views
     */
    void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull T baseRegisterViewHolder);

    boolean isCustomViewHolder();

    @Nullable
    T createCustomViewHolder(@NonNull View itemView);

    boolean useCustomViewLayout();

    @LayoutRes
    int getCustomViewLayoutId();

}
