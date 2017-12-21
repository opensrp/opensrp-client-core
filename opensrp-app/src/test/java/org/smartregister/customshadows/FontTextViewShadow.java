package org.smartregister.customshadows;

import android.content.Context;
import android.util.AttributeSet;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;

/**
 * Created by onadev on 15/06/2017.
 */
@Implements(CustomFontTextView.class)
public class FontTextViewShadow extends ShadowTextView {

    public void __constructor__(Context context, AttributeSet attrs, int defStyle) {

    }

    public void setFontVariant(final FontVariant variant) {

    }

    public void setFontVariant(int variant) {

    }

    public void __constructor__(Context context) {

    }

    public void __constructor__(Context context, AttributeSet attrs) {

    }

}
