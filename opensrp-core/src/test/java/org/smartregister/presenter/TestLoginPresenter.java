package org.smartregister.presenter;

import org.smartregister.login.presenter.BaseLoginPresenter;

/**
 * Created by Vincent Karuri on 10/03/2020
 */
public class TestLoginPresenter extends BaseLoginPresenter {
    @Override
    public void processViewCustomizations() {
        //Do nothing
    }

    @Override
    public boolean isServerSettingsSet() {
        return false;
    }

    @Override
    public void setLanguage() {
        //Do nothing
    }

    @Override
    public void positionViews() {
        //Do nothing
    }
}
