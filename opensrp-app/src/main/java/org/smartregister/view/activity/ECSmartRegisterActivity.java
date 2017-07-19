package org.smartregister.view.activity;

import org.smartregister.view.controller.ECSmartRegisterController;

public class ECSmartRegisterActivity extends SmartRegisterActivity {
    @Override
    protected void onSmartRegisterInitialization() {
        webView.addJavascriptInterface(new ECSmartRegisterController(context().allEligibleCouples(),
                context().allBeneficiaries(), context().listCache(),
                context().ecClientsCache()), "context");
        webView.loadUrl("file:///android_asset/www/smart_registry/ec_register.html");
    }
}
