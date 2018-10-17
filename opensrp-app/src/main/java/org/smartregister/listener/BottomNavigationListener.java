package org.smartregister.listener;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import org.smartregister.R;
import org.smartregister.view.activity.BaseRegisterActivity;

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Activity context;

    public BottomNavigationListener(Activity context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clients:
                ((BaseRegisterActivity) context).switchToBaseFragment();
                break;
            case R.id.action_search:
                ((BaseRegisterActivity) context).switchToFragment(1);
                break;
            case R.id.action_register:
                ((BaseRegisterActivity) context).startRegistration();
                break;
            case R.id.action_library:
                ((BaseRegisterActivity) context).switchToFragment(4);
                break;
            case R.string.action_me:
                ((BaseRegisterActivity) context).switchToFragment(3);
                break;
            default:
                break;
        }
        return true;
    }
}
