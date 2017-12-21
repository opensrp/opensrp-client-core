package org.smartregister.view.activity.mock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.NativeECSmartRegisterActivity;

/**
 * Created by kaderchowdhury on 11/11/17.
 */

public class NativeECSmartRegisterActivityMock extends NativeECSmartRegisterActivity {

//    NativeECSmartRegisterActivity activity;

    @Override
    protected void onStart() {
        super.onStart();
        //   activity = new NativeECSmartRegisterActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout;
        linearLayout = new LinearLayout(this);
        setContentView(linearLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return super.adapter();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return super.getDefaultOptionsProvider();
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return super.getNavBarOptionsProvider();
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return super.clientsProvider();
    }

    @Override
    protected void onInitialization() {
        super.onInitialization();
    }

    @Override
    public void setupViews() {
        super.setupViews();
    }

    @Override
    public void startRegistration() {
        super.startRegistration();
    }
}
