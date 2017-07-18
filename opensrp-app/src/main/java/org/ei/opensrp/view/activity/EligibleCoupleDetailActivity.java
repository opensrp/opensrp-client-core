package org.ei.opensrp.view.activity;

import org.ei.opensrp.event.CapturedPhotoInformation;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.view.controller.EligibleCoupleDetailController;

import static org.ei.opensrp.event.Event.ON_PHOTO_CAPTURED;

public class EligibleCoupleDetailActivity extends SecuredWebActivity {
    private Listener<CapturedPhotoInformation> photoCaptureListener;

    @Override
    protected void onInitialization() {
        String caseId = (String) getIntent().getExtras().get("caseId");

        webView.addJavascriptInterface(new EligibleCoupleDetailController(this, caseId,
                context().allEligibleCouples(), context().allTimelineEvents()), "context");
        webView.loadUrl("file:///android_asset/www/ec_detail.html");

        photoCaptureListener = new Listener<CapturedPhotoInformation>() {
            @Override
            public void onEvent(CapturedPhotoInformation data) {
                if (webView != null) {
                    webView.loadUrl("javascript:pageView.reloadPhoto('" + data.entityId() + "', '" + data.photoPath() + "')");
                }
            }
        };
        ON_PHOTO_CAPTURED.addListener(photoCaptureListener);
    }
}
