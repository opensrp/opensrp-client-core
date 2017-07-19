package org.opensrp.view.activity;

import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.opensrp.AllConstants;
import org.opensrp.Context;
import org.opensrp.event.CapturedPhotoInformation;
import org.opensrp.event.Listener;

import java.util.Map;

import static org.opensrp.AllConstants.*;
import static org.opensrp.event.Event.ON_PHOTO_CAPTURED;
import static org.opensrp.util.Log.logInfo;

public abstract class SmartRegisterActivity extends SecuredWebActivity {
    protected Listener<CapturedPhotoInformation> photoCaptureListener;

    @Override
    protected void onInitialization() {
        onSmartRegisterInitialization();

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

    protected abstract void onSmartRegisterInitialization();

    @Override
    protected void onResumption() {
        webView.loadUrl("javascript:if(window.pageView) {window.pageView.reload();}");
    }
}
