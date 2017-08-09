package org.smartregister.view.activity;

import org.smartregister.event.CapturedPhotoInformation;
import org.smartregister.event.Listener;

import static org.smartregister.event.Event.ON_PHOTO_CAPTURED;

public abstract class SmartRegisterActivity extends SecuredWebActivity {
    protected Listener<CapturedPhotoInformation> photoCaptureListener;

    @Override
    protected void onInitialization() {
        onSmartRegisterInitialization();

        photoCaptureListener = new Listener<CapturedPhotoInformation>() {
            @Override
            public void onEvent(CapturedPhotoInformation data) {
                if (webView != null) {
                    webView.loadUrl(
                            "javascript:pageView.reloadPhoto('" + data.entityId() + "', " + "'"
                                    + data.photoPath() + "')");
                }
            }
        };
        ON_PHOTO_CAPTURED.addListener(photoCaptureListener);
    }

    protected abstract void onSmartRegisterInitialization();

    @Override
    protected void onResumption() {
        webView.loadUrl("javascript:if(window.pageView) {window.pageView.reload();}");
    }
}
