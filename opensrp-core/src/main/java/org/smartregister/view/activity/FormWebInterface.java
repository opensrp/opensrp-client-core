package org.smartregister.view.activity;

import static org.smartregister.AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import timber.log.Timber;

public class FormWebInterface {
    private final String model;
    private final String form;
    private Activity activity;

    public FormWebInterface(String model, String form, Activity activity) {
        this.model = model;
        this.form = form;
        this.activity = activity;
    }

    @JavascriptInterface
    public String getModel() {
        return model;
    }

    @JavascriptInterface
    public String getForm() {
        return form;
    }

    @JavascriptInterface
    public void goBack() {
        activity.setResult(FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE);
        activity.finish();
    }

    @JavascriptInterface
    public void log(String message) {
        Timber.i(message);
    }

    @JavascriptInterface
    public void onLoadFinished() {
        ((SecuredWebActivity) activity).closeDialog();
    }
}
