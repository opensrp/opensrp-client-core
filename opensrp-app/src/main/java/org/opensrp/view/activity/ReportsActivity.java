package org.opensrp.view.activity;

import org.opensrp.view.controller.ReportsController;

public class ReportsActivity extends SecuredWebActivity {
    @Override
    protected void onInitialization() {
        webView.addJavascriptInterface(new ReportsController(this), "context");
        webView.loadUrl("file:///android_asset/www/reports.html");
    }
}
