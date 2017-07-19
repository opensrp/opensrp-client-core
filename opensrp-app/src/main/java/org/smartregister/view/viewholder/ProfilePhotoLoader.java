package org.smartregister.view.viewholder;

import android.graphics.drawable.Drawable;
import org.smartregister.view.contract.SmartRegisterClient;

public interface ProfilePhotoLoader {
    public Drawable get(SmartRegisterClient client);
}
