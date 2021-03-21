package org.smartregister.listener;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.smartregister.configuration.BottomNavigationOptionsModel;

public class ConfigurableBottomNavigationListener extends BottomNavigationListener {

    private BottomNavigationOptionsModel bottomNavigationOptionsModel;

    public ConfigurableBottomNavigationListener(Activity context, BottomNavigationOptionsModel model) {
        super(context);
        this.bottomNavigationOptionsModel = model;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        bottomNavigationOptionsModel.onBottomOptionSelection(context, menuItem);
        return true;
    }

}
