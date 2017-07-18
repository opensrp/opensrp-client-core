package org.ei.opensrp.view.activity;

import android.content.Intent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.event.CapturedPhotoInformation;
import org.ei.opensrp.event.Listener;

import java.util.Map;

import static org.ei.opensrp.AllConstants.*;
import static org.ei.opensrp.event.Event.ON_PHOTO_CAPTURED;
import static org.ei.opensrp.util.Log.logInfo;

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
