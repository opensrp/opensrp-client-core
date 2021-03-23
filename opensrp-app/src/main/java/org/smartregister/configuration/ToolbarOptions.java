package org.smartregister.configuration;

import org.smartregister.view.dialog.DialogOptionModel;

import java.util.ArrayList;
import java.util.List;

public interface ToolbarOptions {

    public int getLogoResourceId();

    default int getToolBarColor() {
        return 0;
    }

    public int getFabTextStringResource();

    public boolean isFabEnabled();

    public boolean isNewToolbarEnabled();

    default List<Integer> getHiddenToolOptions() {
        return new ArrayList<>();
    }

    public DialogOptionModel getDialogOptionModel();

}
