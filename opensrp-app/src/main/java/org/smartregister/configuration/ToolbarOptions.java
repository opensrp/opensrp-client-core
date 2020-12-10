package org.smartregister.configuration;

import org.smartregister.view.dialog.DialogOptionModel;

public interface ToolbarOptions {

    public int getLogoResourceId();

    public int getFabTextStringResource();

    public boolean isFabEnabled();

    public boolean isNewToolbarEnabled();

    public DialogOptionModel getDialogOptionModel();

}
