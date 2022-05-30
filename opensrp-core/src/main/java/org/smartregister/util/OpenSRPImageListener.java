package org.smartregister.util;

import android.widget.ImageView;
import android.widget.RemoteViews;

public class OpenSRPImageListener {

    private final ImageView imageView;
    private final RemoteViews remoteView;
    private final int defaultImageResId;
    private final int errorImageResId;
    private final int imageViewId;
    private String absoluteFileName;
    private boolean hasImageViewTag;
    private String entityId;

    public OpenSRPImageListener(ImageView imageView, int defaultImageResId, int errorImageResId) {
        this.imageView = imageView;
        this.defaultImageResId = defaultImageResId;
        this.errorImageResId = errorImageResId;
        this.remoteView = null;
        this.imageViewId = 0;
    }

    public OpenSRPImageListener(ImageView imageView, String entityId, int defaultImageResId, int
            errorImageResId) {
        this(imageView, defaultImageResId, errorImageResId);
        setEntityId(entityId);
    }

    public OpenSRPImageListener(RemoteViews remoteView, int imageViewId, int defaultImageResId,
                                int errorImageResId) {
        this.remoteView = remoteView;
        this.defaultImageResId = defaultImageResId;
        this.errorImageResId = errorImageResId;
        this.imageView = null;
        this.imageViewId = imageViewId;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getDefaultImageResId() {
        return defaultImageResId;
    }

    public int getErrorImageResId() {
        return errorImageResId;
    }

    public String getAbsoluteFileName() {
        return absoluteFileName;
    }

    public void setAbsoluteFileName(String absoluteFileName) {
        this.absoluteFileName = absoluteFileName;
    }

    public void setHasImageTag(boolean hasTag) {
        this.hasImageViewTag = hasTag;
    }

    public boolean getHasImageViewTag() {
        return hasImageViewTag;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public RemoteViews getRemoteView() {
        return remoteView;
    }

    public int getImageViewId() {
        return imageViewId;
    }
}
