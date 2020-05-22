package org.smartregister.view.activity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

import java.io.BufferedReader;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 07-05-2020.
 */
public class DynamicJsonFormActivity extends JsonFormActivity {


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
