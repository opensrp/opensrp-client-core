package org.smartregister.view.dialog;

import android.app.Activity;

public interface DialogOptionModel {
    DialogOption[] getDialogOptions();

    default void onDialogOptionSelection(DialogOption option, Object tag) {
        // Empty default implementation
    }

    default void onDialogOptionSelection(Activity activity, DialogOption option, Object tag) {
        onDialogOptionSelection(option, tag);
    }
}
