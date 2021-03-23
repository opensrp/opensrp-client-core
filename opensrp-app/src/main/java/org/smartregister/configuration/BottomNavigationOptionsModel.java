package org.smartregister.configuration;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;

public interface BottomNavigationOptionsModel {

    void onBottomOptionSelection(Activity activity, @NonNull MenuItem menuItem);
}
