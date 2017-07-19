package org.smartregister.view.viewHolder;

import android.graphics.drawable.Drawable;
import org.smartregister.view.contract.SmartRegisterClient;

public interface ProfilePhotoLoader {
    public Drawable get(SmartRegisterClient client);
}
