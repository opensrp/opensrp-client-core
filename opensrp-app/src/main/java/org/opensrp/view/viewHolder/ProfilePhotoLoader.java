package org.opensrp.view.viewHolder;

import android.graphics.drawable.Drawable;
import org.opensrp.view.contract.SmartRegisterClient;

public interface ProfilePhotoLoader {
    public Drawable get(SmartRegisterClient client);
}
