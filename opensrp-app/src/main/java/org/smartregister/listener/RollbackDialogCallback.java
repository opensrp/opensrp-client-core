package org.smartregister.listener;

import android.support.annotation.NonNull;

import org.smartregister.domain.ClientForm;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-05-2020.
 */
public interface RollbackDialogCallback {

    void onFormSelected(@NonNull ClientForm selectedForm);

    void onCancelClicked();
}
