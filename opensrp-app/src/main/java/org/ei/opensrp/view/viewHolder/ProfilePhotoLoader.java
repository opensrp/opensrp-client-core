package org.ei.opensrp.view.viewHolder;

import android.graphics.drawable.Drawable;
import org.ei.opensrp.view.contract.SmartRegisterClient;

public interface ProfilePhotoLoader {
    public Drawable get(SmartRegisterClient client);
}
