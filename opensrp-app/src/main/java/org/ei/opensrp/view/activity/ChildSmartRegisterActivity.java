package org.ei.opensrp.view.activity;

import org.ei.opensrp.view.controller.ChildSmartRegisterController;

public class ChildSmartRegisterActivity extends SmartRegisterActivity {
    @Override
    protected void onSmartRegisterInitialization() {
        webView.addJavascriptInterface(new ChildSmartRegisterController(
                context().serviceProvidedService(), context().alertService(),
                context().allBeneficiaries(), context().listCache(),
                context().smartRegisterClientsCache()), "context");
        webView.loadUrl("file:///android_asset/www/smart_registry/child_register.html");
    }
}
