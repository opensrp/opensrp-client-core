package org.smartregister.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.FormUtils;

import java.io.BufferedReader;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 07-05-2020.
 */
public class DynamicJsonFormActivity extends JsonFormActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONObject jsonObject = getmJSONObject();
        if (FormUtils.isFormNew(jsonObject)) {
            showFormVersionUpdateDialog( getString(R.string.form_update_title), getString(R.string.form_update_message));
        }
    }

    public void showFormVersionUpdateDialog(@NonNull String title, @NonNull String message) {
        int clientId = FormUtils.getClientFormId(getmJSONObject());
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        negateIsNewClientForm(clientId);
                        dialog.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        negateIsNewClientForm(clientId);
                    }
                })
                .show();
    }

    private void negateIsNewClientForm(int clientFormId) {
        AppExecutors appExecutors = new AppExecutors();

        appExecutors.diskIO()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        CoreLibrary.getInstance().context().getClientFormRepository()
                                .setIsNew(false, clientFormId);
                    }
                });
    }

    @NonNull
    @Override
    public BufferedReader getRules(@NonNull Context context, @NonNull String fileName) throws IOException {
        try {
            FormUtils formUtils = FormUtils.getInstance(context);
            BufferedReader bufferedReader = formUtils.getRulesFromRepository(fileName);
            if (bufferedReader != null) {
                return bufferedReader;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return super.getRules(context, fileName);
    }

    @NonNull
    @Override
    public JSONObject getSubForm(String formIdentity, String subFormsLocation, Context context, boolean translateSubForm) throws Exception {
        FormUtils formUtils = FormUtils.getInstance(context);
        JSONObject dbForm =  formUtils.getSubFormJsonFromRepository(formIdentity, subFormsLocation, context, translateSubForm);
        if (dbForm == null) {
            return super.getSubForm(formIdentity, subFormsLocation, context, translateSubForm);
        }

        return dbForm;
    }
}
