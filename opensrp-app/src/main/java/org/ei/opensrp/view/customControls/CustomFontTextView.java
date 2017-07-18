package org.ei.opensrp.view.customControls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import org.ei.opensrp.R;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;

public class CustomFontTextView extends TextView {

    private Cache<Typeface> cache;

    @SuppressWarnings("UnusedDeclaration")
    public CustomFontTextView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CustomFontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, 0);
        cache = org.ei.opensrp.Context.getInstance().typefaceCache();
        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.org_ei_drishti_view_customControls_CustomFontTextView, 0, defStyle);
        int variant = attributes.getInt(
                R.styleable.org_ei_drishti_view_customControls_CustomFontTextView_fontVariant, 0);
        attributes.recycle();

        setFontVariant(variant);
    }

    public void setFontVariant(int variant) {
        setFontVariant(FontVariant.tryParse(variant, FontVariant.REGULAR));
    }

    public void setFontVariant(final FontVariant variant) {
        setTypeface(cache.get(variant.name(), new CacheableData<Typeface>() {
            @Override
            public Typeface fetch() {
                return Typeface.createFromAsset(
                        org.ei.opensrp.Context.getInstance().applicationContext().getAssets(),
                        variant.fontFile());

            }
        }));
    }
}
