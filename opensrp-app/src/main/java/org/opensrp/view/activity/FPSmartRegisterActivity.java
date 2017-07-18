package org.opensrp.view.activity;

import org.opensrp.view.controller.FPSmartRegisterController;

public class FPSmartRegisterActivity extends SmartRegisterActivity {

    @Override
    protected void onSmartRegisterInitialization() {
        webView.addJavascriptInterface(new FPSmartRegisterController(context().allEligibleCouples(),
                context().allBeneficiaries(), context().alertService(), context().listCache(),
                context().fpClientsCache()), "context");
        webView.loadUrl("file:///android_asset/www/smart_registry/fp_register.html");
    }
}
