package org.ei.opensrp.view.activity;

import android.os.Bundle;
import org.ei.opensrp.view.controller.ReportIndicatorCaseListViewController;

import java.util.List;

import static org.ei.opensrp.AllConstants.CASE_IDS;
import static org.ei.opensrp.AllConstants.INDICATOR;
import static org.ei.opensrp.AllConstants.MONTH;

public class ReportIndicatorCaseListActivity extends SecuredWebActivity {
    @Override
    protected void onInitialization() {
        Bundle extras = getIntent().getExtras();
        List<String> caseIds = extras.getStringArrayList(CASE_IDS);
        String month = extras.getString(MONTH);
        String indicator = extras.getString(INDICATOR);

        webView.addJavascriptInterface(new ReportIndicatorCaseListViewController(this, indicator, caseIds, month), "context");
        webView.loadUrl("file:///android_asset/www/report_indicator_case_list.html");
    }
}
