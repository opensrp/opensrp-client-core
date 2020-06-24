package org.smartregister.listener;

import androidx.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-05-2020.
 */
public interface OnFormFetchedCallback<T> {

    void onFormFetched(@Nullable T form);
}
