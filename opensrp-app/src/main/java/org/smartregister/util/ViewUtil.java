package org.smartregister.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.ClientForm;
import org.smartregister.listener.RollbackDialogCallback;
import org.smartregister.repository.ClientFormRepository;

import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-05-2020.
 */
public class ViewUtil {


    public static AlertDialog showAvailableRollbackFormsDialog(@NonNull Context mContext, @NonNull List<ClientForm> clientFormList, @NonNull ClientForm currentClientForm, @NonNull RollbackDialogCallback rollbackDialogCallback) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
        builderSingle.setIcon(R.drawable.ic_icon_danger);
        builderSingle.setTitle("Select a rollback form");
        //builderSingle.setMessage("Due to an error on the current form, the form cannot be openned. Kindly select another rollback form to use for the time being.");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_singlechoice);

        for (ClientForm clientForm: clientFormList) {
            if (clientForm.getVersion().equals(currentClientForm.getVersion())) {
                arrayAdapter.add("v" + clientForm.getVersion() + mContext.getString(R.string.current_corrupted_form));
            } else {
                arrayAdapter.add("v" + clientForm.getVersion());
            }
        }

        arrayAdapter.add(AllConstants.CLIENT_FORM_ASSET_VERSION);

        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                rollbackDialogCallback.onCancelClicked();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String formVersion = arrayAdapter.getItem(which);

                if (formVersion.contains(mContext.getString(R.string.current_corrupted_form))) {
                    Toast.makeText(mContext, "You cannot select this form because it's corrupted!", Toast.LENGTH_LONG).show();
                } else {
                    ClientForm selectedClientForm;
                    ClientFormRepository clientFormRepository = CoreLibrary.getInstance().context().getClientFormRepository();
                    if (formVersion.equals(AllConstants.CLIENT_FORM_ASSET_VERSION)) {
                        selectedClientForm = new ClientForm();
                        selectedClientForm.setVersion(AllConstants.CLIENT_FORM_ASSET_VERSION);
                    } else {
                        selectedClientForm = clientFormList.get(which);

                        if (selectedClientForm != null) {
                            selectedClientForm.setActive(true);
                            clientFormRepository.addOrUpdate(selectedClientForm);
                        }
                    }

                    currentClientForm.setActive(false);
                    clientFormRepository.addOrUpdate(currentClientForm);

                    rollbackDialogCallback.onFormSelected(selectedClientForm);
                }

            }
        });
        return builderSingle.show();
    }
}
