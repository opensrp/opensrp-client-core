package org.ei.opensrp.view.activity;

import org.ei.opensrp.event.CapturedPhotoInformation;
import org.ei.opensrp.event.Event;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.view.controller.PNCDetailController;

public class PNCDetailActivity extends SecuredWebActivity {
    private Listener<CapturedPhotoInformation> photoCaptureListener;

    @Override
    protected void onInitialization() {
        String caseId = (String) getIntent().getExtras().get("caseId");

        webView.addJavascriptInterface(new PNCDetailController(this, caseId,
                context().allEligibleCouples(), context().allBeneficiaries(),
                context().allTimelineEvents()), "context");
        webView.loadUrl("file:///android_asset/www/pnc_detail.html");

        photoCaptureListener = new Listener<CapturedPhotoInformation>() {
            @Override
            public void onEvent(CapturedPhotoInformation data) {
                if (webView != null) {
                    webView.loadUrl("javascript:pageView.reloadPhoto('" + data.entityId() + "', '" + data.photoPath() + "')");
                }
            }
        };
        Event.ON_PHOTO_CAPTURED.addListener(photoCaptureListener);
    }
}
