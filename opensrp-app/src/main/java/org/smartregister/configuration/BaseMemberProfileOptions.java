package org.smartregister.configuration;

import org.smartregister.view.activity.SecuredActivity;

public interface BaseMemberProfileOptions {

    Class<? extends SecuredActivity> getModuleMedicalHistoryActivity();

    Class<? extends BaseMemberProfileRowsDataProvider> getMemberProfileDataProvider();

}
