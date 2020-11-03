package org.smartregister.cursoradapter.mock;

import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by samuelgithengi on 11/3/20.
 */
public class RecyclerViewFragmentMock extends RecyclerViewFragment {

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {//do nothing
    }

    @Override
    protected void startRegistration() {//do nothing
    }

    @Override
    protected void onCreation() {//do nothing
    }
}
