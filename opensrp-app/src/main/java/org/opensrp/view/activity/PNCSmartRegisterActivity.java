package org.opensrp.view.activity;

import org.opensrp.view.controller.PNCSmartRegisterController;

public class PNCSmartRegisterActivity extends SmartRegisterActivity {
    @Override
    protected void onSmartRegisterInitialization() {
        webView.addJavascriptInterface(new PNCSmartRegisterController(
                context().serviceProvidedService(), context().alertService(),
                context().allEligibleCouples(), context().allBeneficiaries(), context().listCache(),
                context().pncClientsCache()), "context");
        webView.loadUrl("file:///android_asset/www/smart_registry/pnc_register.html");
    }
}
