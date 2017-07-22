package org.smartregister.view.customcontrols;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by koros on 1/12/16.
 */
public class OpenSRPWebView extends WebView {

    public OpenSRPWebView(Context context) {
        super(context, null);
    }

    public OpenSRPWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OpenSRPWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        disableLongClick();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            post(new Runnable() {
                @Override
                public void run() {
                    loadUrl("javascript:refreshDateFields()");
                    Log.d("OpenSRPWebView", "date widgets reloaded");
                }
            });
        }
    }

    public void disableLongClick() {
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        this.setLongClickable(false);
    }
}
