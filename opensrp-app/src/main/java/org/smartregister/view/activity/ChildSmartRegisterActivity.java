package org.smartregister.view.activity;

import org.smartregister.view.controller.ChildSmartRegisterController;

public class ChildSmartRegisterActivity extends SmartRegisterActivity {
    @Override
    protected void onSmartRegisterInitialization() {
        webView.addJavascriptInterface(
                new ChildSmartRegisterController(context().serviceProvidedService(),
                        context().alertService(), context().allBeneficiaries(),
                        context().listCache(), context().smartRegisterClientsCache()), "context");
        webView.loadUrl("file:///android_asset/www/smart_registry/child_register.html");
    }
}
